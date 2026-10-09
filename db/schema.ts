import { pgTable, serial, text, integer, doublePrecision, boolean, timestamp } from "drizzle-orm/pg-core";

export const leases = pgTable("leases", {
  id: text().primaryKey(),
  tenantName: text("tenant_name").notNull(),
  propertyAddress: text("property_address").notNull(),
  squareFootage: integer("square_footage").notNull(),
  monthlyRent: doublePrecision("monthly_rent").notNull(),
  commencementDate: text("commencement_date").notNull(),
  expirationDate: text("expiration_date").notNull(),
  cpiIndex: text("cpi_index").notNull(),
  escalationCapPercent: doublePrecision("escalation_cap_percent").notNull(),
  matchScore: integer("match_score").notNull(),
  isFlagged: boolean("is_flagged").notNull().default(false),
  flagReason: text("flag_reason"),
  coiStatus: text("coi_status").notNull().default("CLEAR"),
  glCoverage: doublePrecision("gl_coverage").notNull(),
  namedInsuredVerified: boolean("named_insured_verified").notNull().default(false),
  auditHash: text("audit_hash").notNull(),
  executiveSummary: text("executive_summary"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
});

export const coiItems = pgTable("coi_items", {
  id: text().primaryKey(),
  tenantName: text("tenant_name").notNull(),
  policyNumber: text("policy_number").notNull(),
  carrierName: text("carrier_name").notNull(),
  glLimit: text("gl_limit").notNull(),
  umbrellaLimit: text("umbrella_limit").notNull(),
  namedInsured: text("named_insured").notNull(),
  expirationDate: text("expiration_date").notNull(),
  severity: text().notNull(),
  deficiencyNotes: text("deficiency_notes"),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

export const auditLogs = pgTable("audit_logs", {
  id: serial().primaryKey(),
  officer: text().notNull(),
  action: text().notNull(),
  hash: text().notNull(),
  createdAt: timestamp("created_at").defaultNow().notNull(),
});

export const acquisitionLeads = pgTable("acquisition_leads", {
  id: text().primaryKey(),
  officerName: text("officer_name").notNull(),
  email: text().notNull(),
  scale: text().notNull(),
  intentVerified: boolean("intent_verified").notNull(),
  status: text().notNull().default("Pending Transfer Escrow"),
  submittedAt: timestamp("submitted_at").defaultNow().notNull(),
});

export const portfolioSettings = pgTable("portfolio_settings", {
  id: integer().primaryKey(),
  portfolioSizeSf: integer("portfolio_size_sf").notNull(),
  rentalWeightPerSf: doublePrecision("rental_weight_per_sf").notNull(),
  parsedDocCount: integer("parsed_doc_count").notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});
