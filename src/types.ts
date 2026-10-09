export type Severity = "CLEAR" | "WARNING" | "CRITICAL";

export interface Lease {
  id: string;
  tenantName: string;
  propertyAddress: string;
  squareFootage: number;
  monthlyRent: number;
  commencementDate: string;
  expirationDate: string;
  cpiIndex: string;
  escalationCapPercent: number;
  matchScore: number;
  isFlagged: boolean;
  flagReason: string | null;
  coiStatus: Severity;
  glCoverage: number;
  namedInsuredVerified: boolean;
  auditHash: string;
  executiveSummary: string | null;
  createdAt: string;
}

export interface CoiItem {
  id: string;
  tenantName: string;
  policyNumber: string;
  carrierName: string;
  glLimit: string;
  umbrellaLimit: string;
  namedInsured: string;
  expirationDate: string;
  severity: Severity;
  deficiencyNotes: string | null;
}

export interface AuditLog {
  id: number;
  officer: string;
  action: string;
  hash: string;
  createdAt: string;
}

export interface Portfolio {
  portfolioSizeSf: number;
  rentalWeightPerSf: number;
  monthlyRunRate: number;
  activeFlagsCount: number;
  complianceRate: number;
  infrastructureScale: string;
  parsedDocCount: number;
  lastUpdated: string;
}

export interface AppState {
  portfolio: Portfolio;
  leases: Lease[];
  coiItems: CoiItem[];
  auditLogs: AuditLog[];
  leadCount: number;
}

export interface AcquisitionLead {
  id: string;
  officerName: string;
  email: string;
  scale: string;
  status: string;
}

export const ROLES = [
  { key: "PRINCIPAL_OFFICER", title: "Principal Corporate Officer", badge: "TIER-1 EXEC", clearance: "Full IP & Financial Clearance", canWrite: true },
  { key: "PORTFOLIO_MANAGER", title: "Portfolio Asset Manager", badge: "OPS-LEAD", clearance: "Read / Write Portfolio Operations", canWrite: true },
  { key: "AUDITOR", title: "Compliance & Risk Auditor", badge: "SEC-AUDIT", clearance: "Read-Only Cryptographic Audit", canWrite: false },
] as const;

export type Role = (typeof ROLES)[number];
