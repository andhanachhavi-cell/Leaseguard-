import type { leases, coiItems } from "../../db/schema.js";

type NewLease = typeof leases.$inferInsert;
type NewCoi = typeof coiItems.$inferInsert;

export const DEFAULT_PORTFOLIO = { portfolioSizeSf: 128_450, rentalWeightPerSf: 5.8, parsedDocCount: 18 };

export const SEED_LEASES: NewLease[] = [
  {
    id: "lease_1", tenantName: "Vertex Capital Partners", propertyAddress: "100 Montgomery St, Fl 14, San Francisco CA",
    squareFootage: 38_200, monthlyRent: 18_463.33, commencementDate: "Jan 01, 2022", expirationDate: "Nov 15, 2026",
    cpiIndex: "CPI-U + 2.5%", escalationCapPercent: 3.5, matchScore: 91, isFlagged: true,
    flagReason: "Renewal window expires in 42 days; tenant disputing CPI index adjustment calculation without cap.",
    coiStatus: "WARNING", glCoverage: 2_000_000, namedInsuredVerified: false, auditHash: "0x8F4AB2190E",
  },
  {
    id: "lease_2", tenantName: "Horizon Health Systems", propertyAddress: "450 Lexington Ave, Suite 900, New York NY",
    squareFootage: 42_500, monthlyRent: 20_541.67, commencementDate: "Jul 01, 2023", expirationDate: "Aug 30, 2030",
    cpiIndex: "CPI-W + 1.8%", escalationCapPercent: 4.0, matchScore: 98, isFlagged: false, flagReason: null,
    coiStatus: "CLEAR", glCoverage: 5_000_000, namedInsuredVerified: true, auditHash: "0x3C19A780DF",
  },
  {
    id: "lease_3", tenantName: "Nexus Media Group", propertyAddress: "77 Water St, Fl 22, New York NY",
    squareFootage: 18_650, monthlyRent: 9_014.17, commencementDate: "Mar 15, 2021", expirationDate: "Oct 31, 2026",
    cpiIndex: "Uncapped CPI-U", escalationCapPercent: 0, matchScore: 88, isFlagged: true,
    flagReason: "Uncapped CPI exposure clause detected; missing mandatory $5M commercial umbrella rider.",
    coiStatus: "CRITICAL", glCoverage: 1_000_000, namedInsuredVerified: false, auditHash: "0x9E88FF114A",
  },
  {
    id: "lease_4", tenantName: "Apex Global Logistics", propertyAddress: "600 Brickell Ave, Tower A, Miami FL",
    squareFootage: 24_100, monthlyRent: 11_648.33, commencementDate: "May 01, 2024", expirationDate: "Apr 30, 2029",
    cpiIndex: "Fixed 3.2%", escalationCapPercent: 3.5, matchScore: 96, isFlagged: false, flagReason: null,
    coiStatus: "CLEAR", glCoverage: 2_000_000, namedInsuredVerified: true, auditHash: "0x7E99D4431B",
  },
  {
    id: "lease_5", tenantName: "Sovereign Tower LLC", propertyAddress: "200 South Wacker Dr, Floors 30-32, Chicago IL",
    squareFootage: 55_000, monthlyRent: 26_583.33, commencementDate: "Jan 01, 2024", expirationDate: "Dec 31, 2033",
    cpiIndex: "CPI-U + 2.0%", escalationCapPercent: 4.5, matchScore: 99, isFlagged: false, flagReason: null,
    coiStatus: "CLEAR", glCoverage: 5_000_000, namedInsuredVerified: true, auditHash: "0x1A2BC3D488",
  },
  {
    id: "lease_6", tenantName: "Quantum Robotics Lab", propertyAddress: "1000 Tech Center Way, Bldg C, Austin TX",
    squareFootage: 31_000, monthlyRent: 14_983.33, commencementDate: "Sep 01, 2023", expirationDate: "Aug 31, 2028",
    cpiIndex: "CPI-U + 2.2%", escalationCapPercent: 3.8, matchScore: 97, isFlagged: false, flagReason: null,
    coiStatus: "CLEAR", glCoverage: 3_000_000, namedInsuredVerified: true, auditHash: "0x44B2E1097C",
  },
];

export const SEED_COI: NewCoi[] = [
  {
    id: "coi_1", tenantName: "Vertex Capital Partners", policyNumber: "TRV-882910-GL", carrierName: "Travelers Commercial Insurance",
    glLimit: "$2,000,000 / $4,000,000", umbrellaLimit: "$2,000,000", namedInsured: "Pending Additional Endorsement",
    expirationDate: "Nov 15, 2026", severity: "WARNING",
    deficiencyNotes: "Under-limit: Umbrella limit is $2M. Sovereign building criteria mandates $5M umbrella.",
  },
  {
    id: "coi_2", tenantName: "Nexus Media Group", policyNumber: "CHB-901244-GL", carrierName: "Chubb Global Corporate",
    glLimit: "$1,000,000 / $2,000,000", umbrellaLimit: "Missing / Not Filed", namedInsured: "Deficient (Landlord Omitted)",
    expirationDate: "Lapsed 14 days ago", severity: "CRITICAL",
    deficiencyNotes: "Immediate Stop-Work: Policy has lapsed and landlord is omitted from named insured rider.",
  },
  {
    id: "coi_3", tenantName: "Horizon Health Systems", policyNumber: "LBM-449102-GL", carrierName: "Liberty Mutual CRE Group",
    glLimit: "$5,000,000 / $10,000,000", umbrellaLimit: "$10,000,000", namedInsured: "Verified & Endorsed",
    expirationDate: "Aug 30, 2027", severity: "CLEAR", deficiencyNotes: null,
  },
  {
    id: "coi_4", tenantName: "Apex Global Logistics", policyNumber: "HIG-229184-GL", carrierName: "Hartford Underwriters",
    glLimit: "$2,000,000 / $4,000,000", umbrellaLimit: "$5,000,000", namedInsured: "Verified & Endorsed",
    expirationDate: "Jan 14, 2027", severity: "CLEAR", deficiencyNotes: null,
  },
  {
    id: "coi_5", tenantName: "Sovereign Tower LLC", policyNumber: "AIG-773129-GL", carrierName: "AIG Commercial Risk",
    glLimit: "$5,000,000 / $10,000,000", umbrellaLimit: "$15,000,000", namedInsured: "Verified & Endorsed",
    expirationDate: "Dec 31, 2027", severity: "CLEAR", deficiencyNotes: null,
  },
  {
    id: "coi_6", tenantName: "Quantum Robotics Lab", policyNumber: "ZUR-661028-GL", carrierName: "Zurich North America",
    glLimit: "$3,000,000 / $6,000,000", umbrellaLimit: "$5,000,000", namedInsured: "Verified & Endorsed",
    expirationDate: "Mar 19, 2027", severity: "CLEAR", deficiencyNotes: null,
  },
  {
    id: "coi_7", tenantName: "Aegis FinTech Corp", policyNumber: "CNA-110482-GL", carrierName: "CNA Financial Corp",
    glLimit: "$2,000,000 / $4,000,000", umbrellaLimit: "$3,000,000", namedInsured: "Missing Subrogation Waiver",
    expirationDate: "Oct 28, 2026", severity: "WARNING",
    deficiencyNotes: "Waiver of Subrogation form WOS-2024 is missing from certificate bundle.",
  },
  {
    id: "coi_8", tenantName: "BioGenix Diagnostics", policyNumber: "BER-554190-GL", carrierName: "Berkshire Hathaway Guard",
    glLimit: "$4,000,000 / $8,000,000", umbrellaLimit: "$10,000,000", namedInsured: "Verified & Endorsed",
    expirationDate: "Jul 22, 2027", severity: "CLEAR", deficiencyNotes: null,
  },
];

export const SEED_AUDIT = [
  { officer: "System Daemon", action: "Escalation Matrix Synchronized with Bureau of Labor Statistics" },
  { officer: "Compliance Auditor", action: "COI Flag Triggered for Nexus Media Group (Lapsed Coverage)" },
  { officer: "AI Ingestion Engine", action: "OCR Vector Scan Completed on Portfolio (18 Documents)" },
  { officer: "Principal Officer", action: "Zero-Trust Encryption Session Initialized (TLS 1.3)" },
];
