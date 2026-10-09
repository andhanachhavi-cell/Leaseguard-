package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.model.AcquisitionLead
import com.example.data.model.AuditLog
import com.example.data.model.CoiAuditItem
import com.example.data.model.LeaseItem
import com.example.data.model.PortfolioState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class LeaseGuardRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val leaseDao = database.leaseDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("leaseguard_prefs", Context.MODE_PRIVATE)

    private val _portfolioState = MutableStateFlow(loadInitialPortfolioState())
    val portfolioState: StateFlow<PortfolioState> = _portfolioState.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(initialAuditLogs())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _coiMatrixItems = MutableStateFlow<List<CoiAuditItem>>(initialCoiItems())
    val coiMatrixItems: StateFlow<List<CoiAuditItem>> = _coiMatrixItems.asStateFlow()

    val leasesFlow: Flow<List<LeaseItem>> = leaseDao.getAllLeases()
    val leadsFlow: Flow<List<AcquisitionLead>> = leaseDao.getAllLeads()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (leaseDao.getLeaseCount() == 0) {
                leaseDao.insertAll(initialLeases())
            }
        }
    }

    private fun loadInitialPortfolioState(): PortfolioState {
        val sf = prefs.getLong("portfolio_sf", 128_450L)
        val rate = prefs.getFloat("rental_weight", 5.80f).toDouble()
        val parsedCount = prefs.getInt("parsed_doc_count", 18)
        val flags = calculateFlagsForSize(sf)
        val monthlyRunRate = (sf * rate) / 12.0
        val scale = when {
            sf < 100_000 -> "Under 100k SF"
            sf <= 1_000_000 -> "100k - 1M SF"
            else -> "1M+ SF Sovereign Tier"
        }
        return PortfolioState(
            portfolioSizeSf = sf,
            rentalWeightPerSf = rate,
            monthlyRunRate = monthlyRunRate,
            activeFlagsCount = flags,
            complianceRate = 98.4,
            infrastructureScale = scale,
            parsedDocCount = parsedCount,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun updatePortfolioStats(newSizeSf: Long, rentalWeight: Double = 5.80) {
        val annualRent = newSizeSf * rentalWeight
        val monthlyRunRate = annualRent / 12.0
        val flags = calculateFlagsForSize(newSizeSf)
        val scale = when {
            newSizeSf < 100_000 -> "Under 100k SF"
            newSizeSf <= 1_000_000 -> "100k - 1M SF"
            else -> "1M+ SF Sovereign Tier"
        }

        prefs.edit()
            .putLong("portfolio_sf", newSizeSf)
            .putFloat("rental_weight", rentalWeight.toFloat())
            .apply()

        _portfolioState.value = _portfolioState.value.copy(
            portfolioSizeSf = newSizeSf,
            rentalWeightPerSf = rentalWeight,
            monthlyRunRate = monthlyRunRate,
            activeFlagsCount = flags,
            infrastructureScale = scale,
            lastUpdated = System.currentTimeMillis()
        )

        addAuditLog(
            action = "Updated Portfolio SF: ${"%,d".format(newSizeSf)} (Run Rate: \$${"%,.2f".format(monthlyRunRate)}/mo)",
            officer = "Principal Officer"
        )
    }

    fun calculateFlagsForSize(sizeSf: Long): Int {
        return when {
            sizeSf <= 100_000L -> 2
            sizeSf <= 500_000L -> 4
            sizeSf <= 1_000_000L -> 7
            else -> 12
        }
    }

    suspend fun addParsedLease(
        tenantName: String,
        address: String,
        squareFootage: Long,
        monthlyRent: Double,
        cpiIndex: String,
        escalationCapPercent: Double,
        isFlagged: Boolean,
        flagReason: String? = null,
        coiStatus: String = "CLEAR"
    ) {
        val newLease = LeaseItem(
            id = UUID.randomUUID().toString(),
            tenantName = tenantName,
            propertyAddress = address,
            squareFootage = squareFootage,
            monthlyRent = monthlyRent,
            commencementDate = "Oct 01, 2026",
            expirationDate = "Sep 30, 2031",
            cpiIndex = cpiIndex,
            escalationCapPercent = escalationCapPercent,
            matchScore = (94..99).random(),
            isFlagged = isFlagged,
            flagReason = flagReason,
            coiStatus = coiStatus,
            glCoverage = 2_000_000.0,
            namedInsuredVerified = true,
            auditHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(10).uppercase()
        )
        leaseDao.insertLease(newLease)

        // Increment parsed count
        val currentParsed = _portfolioState.value.parsedDocCount + 1
        prefs.edit().putInt("parsed_doc_count", currentParsed).apply()
        _portfolioState.value = _portfolioState.value.copy(parsedDocCount = currentParsed)

        addAuditLog(
            action = "AI Parsed New Lease: $tenantName ($address, ${"%,d".format(squareFootage)} SF)",
            officer = "AI Ingestion Engine"
        )
    }

    suspend fun submitAcquisitionLead(lead: AcquisitionLead) {
        leaseDao.insertLead(lead)
        addAuditLog(
            action = "IP Acquisition Dossier Submitted: ${lead.officerName} (${lead.email}, ${lead.scale})",
            officer = lead.officerName
        )
    }

    fun addAuditLog(action: String, officer: String = "Principal Officer") {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
        val hash = "0x" + UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
        val newLog = AuditLog(
            id = UUID.randomUUID().toString(),
            timestamp = sdf.format(Date()),
            officer = officer,
            action = action,
            hash = hash
        )
        _auditLogs.value = listOf(newLog) + _auditLogs.value.take(19)
    }

    fun updateCoiStatus(itemId: String, newSeverity: String, note: String?) {
        _coiMatrixItems.value = _coiMatrixItems.value.map {
            if (it.id == itemId) {
                it.copy(severity = newSeverity, deficiencyNotes = note)
            } else it
        }
        addAuditLog(
            action = "COI Compliance Updated for Item #$itemId -> $newSeverity",
            officer = "Compliance Auditor"
        )
    }

    private fun initialLeases(): List<LeaseItem> = listOf(
        LeaseItem(
            id = "lease_1",
            tenantName = "Vertex Capital Partners",
            propertyAddress = "100 Montgomery St, Fl 14, San Francisco CA",
            squareFootage = 38_200L,
            monthlyRent = 18_463.33,
            commencementDate = "Jan 01, 2022",
            expirationDate = "Nov 15, 2026",
            cpiIndex = "CPI-U + 2.5%",
            escalationCapPercent = 3.5,
            matchScore = 91,
            isFlagged = true,
            flagReason = "Renewal window expires in 42 days; tenant disputing CPI index adjustment calculation without cap.",
            coiStatus = "WARNING",
            glCoverage = 2_000_000.0,
            namedInsuredVerified = false,
            auditHash = "0x8F4AB2190E"
        ),
        LeaseItem(
            id = "lease_2",
            tenantName = "Horizon Health Systems",
            propertyAddress = "450 Lexington Ave, Suite 900, New York NY",
            squareFootage = 42_500L,
            monthlyRent = 20_541.67,
            commencementDate = "Jul 01, 2023",
            expirationDate = "Aug 30, 2030",
            cpiIndex = "CPI-W + 1.8%",
            escalationCapPercent = 4.0,
            matchScore = 98,
            isFlagged = false,
            flagReason = null,
            coiStatus = "CLEAR",
            glCoverage = 5_000_000.0,
            namedInsuredVerified = true,
            auditHash = "0x3C19A780DF"
        ),
        LeaseItem(
            id = "lease_3",
            tenantName = "Nexus Media Group",
            propertyAddress = "77 Water St, Fl 22, New York NY",
            squareFootage = 18_650L,
            monthlyRent = 9_014.17,
            commencementDate = "Mar 15, 2021",
            expirationDate = "Oct 31, 2026",
            cpiIndex = "Uncapped CPI-U",
            escalationCapPercent = 0.0,
            matchScore = 88,
            isFlagged = true,
            flagReason = "Uncapped CPI exposure clause detected; missing mandatory $5M commercial umbrella rider.",
            coiStatus = "CRITICAL",
            glCoverage = 1_000_000.0,
            namedInsuredVerified = false,
            auditHash = "0x9E88FF114A"
        ),
        LeaseItem(
            id = "lease_4",
            tenantName = "Apex Global Logistics",
            propertyAddress = "600 Brickell Ave, Tower A, Miami FL",
            squareFootage = 24_100L,
            monthlyRent = 11_648.33,
            commencementDate = "May 01, 2024",
            expirationDate = "Apr 30, 2029",
            cpiIndex = "Fixed 3.2%",
            escalationCapPercent = 3.5,
            matchScore = 96,
            isFlagged = false,
            flagReason = null,
            coiStatus = "CLEAR",
            glCoverage = 2_000_000.0,
            namedInsuredVerified = true,
            auditHash = "0x7E99D4431B"
        ),
        LeaseItem(
            id = "lease_5",
            tenantName = "Sovereign Tower LLC",
            propertyAddress = "200 South Wacker Dr, Floors 30-32, Chicago IL",
            squareFootage = 55_000L,
            monthlyRent = 26_583.33,
            commencementDate = "Jan 01, 2024",
            expirationDate = "Dec 31, 2033",
            cpiIndex = "CPI-U + 2.0%",
            escalationCapPercent = 4.5,
            matchScore = 99,
            isFlagged = false,
            flagReason = null,
            coiStatus = "CLEAR",
            glCoverage = 5_000_000.0,
            namedInsuredVerified = true,
            auditHash = "0x1A2BC3D488"
        ),
        LeaseItem(
            id = "lease_6",
            tenantName = "Quantum Robotics Lab",
            propertyAddress = "1000 Tech Center Way, Bldg C, Austin TX",
            squareFootage = 31_000L,
            monthlyRent = 14_983.33,
            commencementDate = "Sep 01, 2023",
            expirationDate = "Aug 31, 2028",
            cpiIndex = "CPI-U + 2.2%",
            escalationCapPercent = 3.8,
            matchScore = 97,
            isFlagged = false,
            flagReason = null,
            coiStatus = "CLEAR",
            glCoverage = 3_000_000.0,
            namedInsuredVerified = true,
            auditHash = "0x44B2E1097C"
        )
    )

    private fun initialCoiItems(): List<CoiAuditItem> = listOf(
        CoiAuditItem(
            id = "coi_1",
            tenantName = "Vertex Capital Partners",
            policyNumber = "TRV-882910-GL",
            carrierName = "Travelers Commercial Insurance",
            glLimit = "$2,000,000 / $4,000,000",
            umbrellaLimit = "$2,000,000",
            namedInsured = "Pending Additional Endorsement",
            expirationDate = "Nov 15, 2026",
            severity = "WARNING",
            deficiencyNotes = "Under-limit: Umbrella limit is $2M. Sovereign building criteria mandates $5M umbrella."
        ),
        CoiAuditItem(
            id = "coi_2",
            tenantName = "Nexus Media Group",
            policyNumber = "CHB-901244-GL",
            carrierName = "Chubb Global Corporate",
            glLimit = "$1,000,000 / $2,000,000",
            umbrellaLimit = "Missing / Not Filed",
            namedInsured = "Deficient (Landlord Omitted)",
            expirationDate = "Lapsed 14 days ago",
            severity = "CRITICAL",
            deficiencyNotes = "Immediate Stop-Work: Policy has lapsed and landlord is omitted from named insured rider."
        ),
        CoiAuditItem(
            id = "coi_3",
            tenantName = "Horizon Health Systems",
            policyNumber = "LBM-449102-GL",
            carrierName = "Liberty Mutual CRE Group",
            glLimit = "$5,000,000 / $10,000,000",
            umbrellaLimit = "$10,000,000",
            namedInsured = "Verified & Endorsed",
            expirationDate = "Aug 30, 2027",
            severity = "CLEAR",
            deficiencyNotes = null
        ),
        CoiAuditItem(
            id = "coi_4",
            tenantName = "Apex Global Logistics",
            policyNumber = "HIG-229184-GL",
            carrierName = "Hartford Underwriters",
            glLimit = "$2,000,000 / $4,000,000",
            umbrellaLimit = "$5,000,000",
            namedInsured = "Verified & Endorsed",
            expirationDate = "Jan 14, 2027",
            severity = "CLEAR",
            deficiencyNotes = null
        ),
        CoiAuditItem(
            id = "coi_5",
            tenantName = "Sovereign Tower LLC",
            policyNumber = "AIG-773129-GL",
            carrierName = "AIG Commercial Risk",
            glLimit = "$5,000,000 / $10,000,000",
            umbrellaLimit = "$15,000,000",
            namedInsured = "Verified & Endorsed",
            expirationDate = "Dec 31, 2027",
            severity = "CLEAR",
            deficiencyNotes = null
        ),
        CoiAuditItem(
            id = "coi_6",
            tenantName = "Quantum Robotics Lab",
            policyNumber = "ZUR-661028-GL",
            carrierName = "Zurich North America",
            glLimit = "$3,000,000 / $6,000,000",
            umbrellaLimit = "$5,000,000",
            namedInsured = "Verified & Endorsed",
            expirationDate = "Mar 19, 2027",
            severity = "CLEAR",
            deficiencyNotes = null
        ),
        CoiAuditItem(
            id = "coi_7",
            tenantName = "Aegis FinTech Corp",
            policyNumber = "CNA-110482-GL",
            carrierName = "CNA Financial Corp",
            glLimit = "$2,000,000 / $4,000,000",
            umbrellaLimit = "$3,000,000",
            namedInsured = "Missing Subrogation Waiver",
            expirationDate = "Oct 28, 2026",
            severity = "WARNING",
            deficiencyNotes = "Waiver of Subrogation form WOS-2024 is missing from certificate bundle."
        ),
        CoiAuditItem(
            id = "coi_8",
            tenantName = "BioGenix Diagnostics",
            policyNumber = "BER-554190-GL",
            carrierName = "Berkshire Hathaway Guard",
            glLimit = "$4,000,000 / $8,000,000",
            umbrellaLimit = "$10,000,000",
            namedInsured = "Verified & Endorsed",
            expirationDate = "Jul 22, 2027",
            severity = "CLEAR",
            deficiencyNotes = null
        )
    )

    private fun initialAuditLogs(): List<AuditLog> = listOf(
        AuditLog("log_1", "19:30:12", "Principal Officer", "Zero-Trust Encryption Session Initialized (TLS 1.3)", "0x7F2A190C"),
        AuditLog("log_2", "19:28:44", "AI Ingestion Engine", "OCR Vector Scan Completed on Portfolio (18 Documents)", "0x3B8820EF"),
        AuditLog("log_3", "19:25:01", "Compliance Auditor", "COI Flag Triggered for Nexus Media Group (Lapsed Coverage)", "0x991C74AA"),
        AuditLog("log_4", "19:20:18", "System Daemon", "Escalation Matrix Synchronized with Bureau of Labor Statistics", "0x44D91B22")
    )
}
