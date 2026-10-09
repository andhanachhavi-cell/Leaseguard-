import type { Config, Context } from "@netlify/functions";
import { eq } from "drizzle-orm";
import { db } from "../../db/index.js";
import { coiItems } from "../../db/schema.js";
import { addAuditLog, error, json } from "../lib/store.js";

const ACTIONS = {
  deficiency: { severity: "WARNING", note: (t: string) => `Notice of Insurance Deficiency sent to ${t}.` },
  verify: { severity: "CLEAR", note: () => "Updated Certificate of Insurance verified & active." },
} as const;

export default async (req: Request, context: Context) => {
  if (req.method !== "POST") return error("Method not allowed", 405);
  const { id } = context.params;
  const body = await req.json().catch(() => null);
  const action = ACTIONS[body?.action as keyof typeof ACTIONS];
  if (!action) return error("Unknown action.");

  const [item] = await db.select().from(coiItems).where(eq(coiItems.id, id));
  if (!item) return error("COI record not found.", 404);

  const [updated] = await db
    .update(coiItems)
    .set({ severity: action.severity, deficiencyNotes: action.note(item.tenantName), updatedAt: new Date() })
    .where(eq(coiItems.id, id))
    .returning();
  await addAuditLog(`COI Compliance Updated for ${item.tenantName} -> ${action.severity}`, body?.officer || "Compliance Auditor");
  return json(updated);
};

export const config: Config = { path: "/api/coi/:id" };
