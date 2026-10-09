import type { Config } from "@netlify/functions";
import { addAuditLog, error, json } from "../lib/store.js";

export default async (req: Request) => {
  if (req.method !== "POST") return error("Method not allowed", 405);
  const body = await req.json().catch(() => null);
  const action = String(body?.action ?? "").trim().slice(0, 300);
  const officer = String(body?.officer ?? "Principal Officer").trim().slice(0, 100);
  if (!action) return error("Action is required.");
  await addAuditLog(action, officer);
  return json({ ok: true }, 201);
};

export const config: Config = { path: "/api/audit" };
