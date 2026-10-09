import type { Config } from "@netlify/functions";
import { eq } from "drizzle-orm";
import { db } from "../../db/index.js";
import { portfolioSettings } from "../../db/schema.js";
import { addAuditLog, ensureSeeded, error, getPortfolio, json } from "../lib/store.js";

export default async (req: Request) => {
  if (req.method !== "PUT") return error("Method not allowed", 405);
  const body = await req.json().catch(() => null);
  const sf = Math.round(Number(body?.portfolioSizeSf));
  const rate = Number(body?.rentalWeightPerSf ?? 5.8);
  if (!Number.isFinite(sf) || sf <= 0 || sf > 2_000_000_000) return error("Portfolio size must be a positive number of square feet.");
  if (!Number.isFinite(rate) || rate <= 0 || rate > 10_000) return error("Rent per SF must be a positive number.");

  await ensureSeeded();
  await db
    .update(portfolioSettings)
    .set({ portfolioSizeSf: sf, rentalWeightPerSf: rate, updatedAt: new Date() })
    .where(eq(portfolioSettings.id, 1));

  const monthly = (sf * rate) / 12;
  await addAuditLog(
    `Updated Portfolio SF: ${sf.toLocaleString("en-US")} (Run Rate: $${monthly.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}/mo)`,
    body?.officer || "Principal Officer",
  );
  return json(await getPortfolio());
};

export const config: Config = { path: "/api/portfolio" };
