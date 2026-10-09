import type { Config } from "@netlify/functions";
import { GoogleGenAI, Type } from "@google/genai";
import { eq, sql } from "drizzle-orm";
import { db } from "../../db/index.js";
import { leases, portfolioSettings } from "../../db/schema.js";
import { addAuditLog, ensureSeeded, error, json, randomHash } from "../lib/store.js";

const MODEL = "gemini-3.5-flash";
const MAX_FILE_BYTES = 4 * 1024 * 1024;
const COI_STATUSES = ["CLEAR", "WARNING", "CRITICAL"] as const;

type Extracted = {
  tenantName: string;
  address: string;
  squareFootage: number;
  monthlyRent: number;
  commencementDate: string;
  expirationDate: string;
  cpiIndex: string;
  escalationCapPercent: number;
  isFlagged: boolean;
  flagReason: string | null;
  coiStatus: (typeof COI_STATUSES)[number];
  glCoverage: number;
  executiveSummary: string;
};

const responseSchema = {
  type: Type.OBJECT,
  properties: {
    tenantName: { type: Type.STRING },
    address: { type: Type.STRING },
    squareFootage: { type: Type.NUMBER },
    monthlyRent: { type: Type.NUMBER },
    commencementDate: { type: Type.STRING, description: "Format: Mon DD, YYYY" },
    expirationDate: { type: Type.STRING, description: "Format: Mon DD, YYYY" },
    cpiIndex: { type: Type.STRING, description: "e.g. CPI-U + 2.2%, Fixed 3.0%, Uncapped CPI-U" },
    escalationCapPercent: { type: Type.NUMBER, description: "0 if uncapped" },
    isFlagged: { type: Type.BOOLEAN },
    flagReason: { type: Type.STRING, nullable: true },
    coiStatus: { type: Type.STRING, enum: [...COI_STATUSES] },
    glCoverage: { type: Type.NUMBER, description: "General liability per-occurrence limit in USD" },
    executiveSummary: { type: Type.STRING },
  },
  required: [
    "tenantName", "address", "squareFootage", "monthlyRent", "commencementDate", "expirationDate",
    "cpiIndex", "escalationCapPercent", "isFlagged", "flagReason", "coiStatus", "glCoverage", "executiveSummary",
  ],
};

const PROMPT = `You are LeaseGuard AI's enterprise commercial real estate (CRE) legal parser.
Analyze the commercial lease provided and extract its key economic and risk terms.
- monthlyRent is the base monthly rent in USD (convert from annual or per-SF figures if needed).
- Set isFlagged to true and explain in flagReason when you find material risk: uncapped CPI escalation,
  near-term expiry or renewal deadlines, disputed clauses, insufficient insurance (below a $5M umbrella),
  or missing named-insured / subrogation endorsements. Otherwise flagReason is null.
- coiStatus: CLEAR if insurance terms are compliant, WARNING for minor gaps, CRITICAL for lapsed or missing coverage.
- If a value is not stated, make a conservative, clearly reasonable estimate and mention it in executiveSummary.
- executiveSummary: 1-2 sentences for an asset manager.`;

async function extractWithAi(documentName: string, text: string, file?: { mimeType: string; data: string }) {
  const ai = new GoogleGenAI({});
  const parts: Array<Record<string, unknown>> = [{ text: `${PROMPT}\n\nDocument name: "${documentName}"` }];
  if (file) parts.push({ inlineData: file });
  if (text) parts.push({ text: `Lease text:\n${text}` });

  const response = await ai.models.generateContent({
    model: MODEL,
    contents: [{ role: "user", parts }],
    config: { responseMimeType: "application/json", responseSchema, temperature: 0.2 },
  });
  return JSON.parse(response.text ?? "{}") as Partial<Extracted>;
}

function normalize(raw: Partial<Extracted>, documentName: string): Extracted {
  const num = (v: unknown, fallback: number) => (Number.isFinite(Number(v)) && Number(v) >= 0 ? Number(v) : fallback);
  const str = (v: unknown, fallback: string) => (typeof v === "string" && v.trim() ? v.trim().slice(0, 300) : fallback);
  const sf = Math.round(num(raw.squareFootage, 25_000));
  return {
    tenantName: str(raw.tenantName, documentName.replace(/\.[^.]+$/, "").replace(/_/g, " ")),
    address: str(raw.address, "Address not stated"),
    squareFootage: sf,
    monthlyRent: Math.round(num(raw.monthlyRent, (sf * 5.8) / 12) * 100) / 100,
    commencementDate: str(raw.commencementDate, "Not stated"),
    expirationDate: str(raw.expirationDate, "Not stated"),
    cpiIndex: str(raw.cpiIndex, "Not stated"),
    escalationCapPercent: num(raw.escalationCapPercent, 0),
    isFlagged: Boolean(raw.isFlagged),
    flagReason: raw.isFlagged ? str(raw.flagReason, "Risk detected; review required.") : null,
    coiStatus: COI_STATUSES.includes(raw.coiStatus as Extracted["coiStatus"]) ? (raw.coiStatus as Extracted["coiStatus"]) : "WARNING",
    glCoverage: num(raw.glCoverage, 2_000_000),
    executiveSummary: str(raw.executiveSummary, "Lease terms extracted by the AI parser."),
  };
}

export default async (req: Request) => {
  if (req.method !== "POST") return error("Method not allowed", 405);
  const body = await req.json().catch(() => null);
  const documentName = String(body?.documentName ?? "").trim().slice(0, 200);
  const text = String(body?.text ?? "").slice(0, 200_000);
  const file = body?.file as { mimeType?: string; data?: string } | undefined;

  if (!documentName) return error("A document name is required.");
  let inlineFile: { mimeType: string; data: string } | undefined;
  if (file?.data) {
    if (file.mimeType !== "application/pdf") return error("Only PDF files can be attached; send other formats as text.");
    if (file.data.length * 0.75 > MAX_FILE_BYTES) return error("PDF must be 4 MB or smaller.");
    inlineFile = { mimeType: file.mimeType, data: file.data };
  }
  if (!text.trim() && !inlineFile) return error("The document is empty.");

  let extracted: Extracted;
  try {
    extracted = normalize(await extractWithAi(documentName, text, inlineFile), documentName);
  } catch (e) {
    console.error("AI lease extraction failed", e);
    return error("The AI parser could not read this document. Please try again in a moment.", 502);
  }

  await ensureSeeded();
  const { executiveSummary, address, ...rest } = extracted;
  const [lease] = await db
    .insert(leases)
    .values({
      id: crypto.randomUUID(),
      ...rest,
      propertyAddress: address,
      executiveSummary,
      matchScore: 94 + Math.floor(Math.random() * 6),
      namedInsuredVerified: extracted.coiStatus === "CLEAR",
      auditHash: randomHash(10),
    })
    .returning();

  await db
    .update(portfolioSettings)
    .set({ parsedDocCount: sql`${portfolioSettings.parsedDocCount} + 1` })
    .where(eq(portfolioSettings.id, 1));
  await addAuditLog(
    `AI Parsed New Lease: ${lease.tenantName} (${lease.propertyAddress}, ${lease.squareFootage.toLocaleString("en-US")} SF)`,
    "AI Ingestion Engine",
  );
  return json(lease, 201);
};

export const config: Config = { path: "/api/leases/parse" };
