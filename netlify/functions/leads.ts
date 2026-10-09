import type { Config } from "@netlify/functions";
import { db } from "../../db/index.js";
import { acquisitionLeads } from "../../db/schema.js";
import { addAuditLog, error, json } from "../lib/store.js";

const SCALES = ["Under 100k SF (Growth Tier)", "100k - 1M SF (Enterprise Standard)", "1M+ SF Sovereign Tier"];

export default async (req: Request) => {
  if (req.method !== "POST") return error("Method not allowed", 405);
  const body = await req.json().catch(() => null);
  const officerName = String(body?.officerName ?? "").trim().slice(0, 120);
  const email = String(body?.email ?? "").trim().slice(0, 200);
  const scale = String(body?.scale ?? "");

  if (officerName.length < 2) return error("Please enter the officer's full name.");
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return error("Please enter a valid email address.");
  if (!SCALES.includes(scale)) return error("Please choose a deployment scale.");
  if (body?.intentVerified !== true) return error("Corporate authority must be confirmed.");

  const [lead] = await db
    .insert(acquisitionLeads)
    .values({
      id: "LEAD-" + crypto.randomUUID().slice(0, 8).toUpperCase(),
      officerName,
      email,
      scale,
      intentVerified: true,
    })
    .returning();
  await addAuditLog(`IP Acquisition Dossier Submitted: ${officerName} (${scale})`, officerName);
  return json(lead, 201);
};

export const config: Config = { path: "/api/leads" };
