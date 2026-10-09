package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leases")
data class LeaseItem(
    @PrimaryKey val id: String,
    val tenantName: String,
    val propertyAddress: String,
    val squareFootage: Long,
    val monthlyRent: Double,
    val commencementDate: String,
    val expirationDate: String,
    val cpiIndex: String,
    val escalationCapPercent: Double,
    val matchScore: Int,
    val isFlagged: Boolean,
    val flagReason: String? = null,
    val coiStatus: String, // "CLEAR", "WARNING", "CRITICAL"
    val glCoverage: Double,
    val namedInsuredVerified: Boolean,
    val auditHash: String
)

@Entity(tableName = "acquisition_leads")
data class AcquisitionLead(
    @PrimaryKey val id: String,
    val officerName: String,
    val email: String,
    val scale: String,
    val intentVerified: Boolean,
    val submittedAt: Long,
    val status: String = "Pending Transfer Escrow"
)

data class PortfolioState(
    val portfolioSizeSf: Long = 128_450L,
    val rentalWeightPerSf: Double = 5.80,
    val monthlyRunRate: Double = (128_450L * 5.80) / 12.0,
    val activeFlagsCount: Int = 4,
    val complianceRate: Double = 98.4,
    val infrastructureScale: String = "100k - 1M SF",
    val parsedDocCount: Int = 18,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class CoiAuditItem(
    val id: String,
    val tenantName: String,
    val policyNumber: String,
    val carrierName: String,
    val glLimit: String,
    val umbrellaLimit: String,
    val namedInsured: String,
    val expirationDate: String,
    val severity: String, // "CLEAR", "WARNING", "CRITICAL"
    val deficiencyNotes: String? = null
)

data class MonthlyImpact(
    val monthNumber: Int,
    val monthName: String,
    val projectedRent: Double,
    val inflationDelta: Double,
    val escalationPercent: Float
)

data class AuditLog(
    val id: String,
    val timestamp: String,
    val officer: String,
    val action: String,
    val hash: String
)

enum class UserRole(val title: String, val badge: String, val clearance: String) {
    PRINCIPAL_OFFICER("Principal Corporate Officer", "TIER-1 EXEC", "Full IP & Financial Clearance"),
    PORTFOLIO_MANAGER("Portfolio Asset Manager", "OPS-LEAD", "Read / Write Portfolio Operations"),
    AUDITOR("Compliance & Risk Auditor", "SEC-AUDIT", "Read-Only Cryptographic Audit")
}
