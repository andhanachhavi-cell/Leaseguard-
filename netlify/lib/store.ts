import { desc, eq, sql } from "drizzle-orm";
import { db } from "../../db/index.js";
import { acquisitionLeads, auditLogs, coiItems, leases, portfolioSettings } from "../../db/schema.js";
import { DEFAULT_PORTFOLIO, SEED_AUDIT, SEED_COI, SEED_LEASES } from "./seed.js";

export const randomHash = (len = 8) => "0x" + crypto.randomUUID().replace(/-/g, "").slice(0, len).toUpperCase();

export function flagsForSize(sf: number) {
  if (sf <= 100_000) return 2;
  if (sf <= 500_000) return 4;
  if (sf <= 1_000_000) return 7;
  return 12;
}

export function scaleForSize(sf: number) {
  if (sf < 100_000) return "Under 100k SF";
  if (sf <= 1_000_000) return "100k - 1M SF";
  return "1M+ SF Sovereign Tier";
}

/** Seeds the demo portfolio on first use. Idempotent: the settings row acts as the seed marker. */
export async function ensureSeeded() {
  const existing = await db.select({ id: portfolioSettings.id }).from(portfolioSettings).where(eq(portfolioSettings.id, 1));
  if (existing.length) return;
  const inserted = await db
    .insert(portfolioSettings)
    .values({ id: 1, ...DEFAULT_PORTFOLIO })
    .onConflictDoNothing()
    .returning({ id: portfolioSettings.id });
  if (!inserted.length) return; // another request seeded concurrently
  await db.insert(leases).values(SEED_LEASES).onConflictDoNothing();
  await db.insert(coiItems).values(SEED_COI).onConflictDoNothing();
  for (const log of SEED_AUDIT) {
    await db.insert(auditLogs).values({ ...log, hash: randomHash() });
  }
}

export async function addAuditLog(action: string, officer = "Principal Officer") {
  await db.insert(auditLogs).values({ officer, action, hash: randomHash() });
}

export async function getPortfolio() {
  const [row] = await db.select().from(portfolioSettings).where(eq(portfolioSettings.id, 1));
  const s = row ?? { ...DEFAULT_PORTFOLIO, updatedAt: new Date() };
  return {
    portfolioSizeSf: s.portfolioSizeSf,
    rentalWeightPerSf: s.rentalWeightPerSf,
    monthlyRunRate: (s.portfolioSizeSf * s.rentalWeightPerSf) / 12,
    activeFlagsCount: flagsForSize(s.portfolioSizeSf),
    complianceRate: 98.4,
    infrastructureScale: scaleForSize(s.portfolioSizeSf),
    parsedDocCount: s.parsedDocCount,
    lastUpdated: s.updatedAt,
  };
}

export async function getFullState() {
  await ensureSeeded();
  const [portfolio, leaseRows, coiRows, logRows, leadCount] = await Promise.all([
    getPortfolio(),
    db.select().from(leases).orderBy(desc(leases.createdAt), leases.id),
    db.select().from(coiItems).orderBy(coiItems.id),
    db.select().from(auditLogs).orderBy(desc(auditLogs.id)).limit(20),
    db.select({ count: sql<number>`count(*)::int` }).from(acquisitionLeads),
  ]);
  return { portfolio, leases: leaseRows, coiItems: coiRows, auditLogs: logRows, leadCount: leadCount[0]?.count ?? 0 };
}

export const json = (data: unknown, status = 200) => Response.json(data, { status });
export const error = (message: string, status = 400) => Response.json({ error: message }, { status });
