CREATE TABLE "acquisition_leads" (
	"id" text PRIMARY KEY,
	"officer_name" text NOT NULL,
	"email" text NOT NULL,
	"scale" text NOT NULL,
	"intent_verified" boolean NOT NULL,
	"status" text DEFAULT 'Pending Transfer Escrow' NOT NULL,
	"submitted_at" timestamp DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "audit_logs" (
	"id" serial PRIMARY KEY,
	"officer" text NOT NULL,
	"action" text NOT NULL,
	"hash" text NOT NULL,
	"created_at" timestamp DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "coi_items" (
	"id" text PRIMARY KEY,
	"tenant_name" text NOT NULL,
	"policy_number" text NOT NULL,
	"carrier_name" text NOT NULL,
	"gl_limit" text NOT NULL,
	"umbrella_limit" text NOT NULL,
	"named_insured" text NOT NULL,
	"expiration_date" text NOT NULL,
	"severity" text NOT NULL,
	"deficiency_notes" text,
	"updated_at" timestamp DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "leases" (
	"id" text PRIMARY KEY,
	"tenant_name" text NOT NULL,
	"property_address" text NOT NULL,
	"square_footage" integer NOT NULL,
	"monthly_rent" double precision NOT NULL,
	"commencement_date" text NOT NULL,
	"expiration_date" text NOT NULL,
	"cpi_index" text NOT NULL,
	"escalation_cap_percent" double precision NOT NULL,
	"match_score" integer NOT NULL,
	"is_flagged" boolean DEFAULT false NOT NULL,
	"flag_reason" text,
	"coi_status" text DEFAULT 'CLEAR' NOT NULL,
	"gl_coverage" double precision NOT NULL,
	"named_insured_verified" boolean DEFAULT false NOT NULL,
	"audit_hash" text NOT NULL,
	"executive_summary" text,
	"created_at" timestamp DEFAULT now() NOT NULL
);
--> statement-breakpoint
CREATE TABLE "portfolio_settings" (
	"id" integer PRIMARY KEY,
	"portfolio_size_sf" integer NOT NULL,
	"rental_weight_per_sf" double precision NOT NULL,
	"parsed_doc_count" integer NOT NULL,
	"updated_at" timestamp DEFAULT now() NOT NULL
);
