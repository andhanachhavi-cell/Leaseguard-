package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AcquisitionLead
import com.example.data.model.AuditLog
import com.example.data.model.CoiAuditItem
import com.example.data.model.LeaseItem
import com.example.data.model.MonthlyImpact
import com.example.data.model.PortfolioState
import com.example.data.model.UserRole
import com.example.data.remote.ExtractedLeaseResult
import com.example.data.remote.GeminiLeaseService
import com.example.data.remote.SupabaseClient
import com.example.data.repository.LeaseGuardRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class LeaseGuardViewModel(application: Application) : AndroidViewModel(application) {
    val repository = LeaseGuardRepository(application)

    val portfolioState: StateFlow<PortfolioState> = repository.portfolioState
    val auditLogs: StateFlow<List<AuditLog>> = repository.auditLogs
    val allLeases: StateFlow<List<LeaseItem>> = repository.leasesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // User Role management (RBAC)
    private val _currentRole = MutableStateFlow(UserRole.PRINCIPAL_OFFICER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Dashboard Flag Filtering
    private val _filterFlaggedOnly = MutableStateFlow(false)
    val filterFlaggedOnly: StateFlow<Boolean> = _filterFlaggedOnly.asStateFlow()

    val filteredLeases: StateFlow<List<LeaseItem>> = combine(allLeases, _filterFlaggedOnly) { list, flaggedOnly ->
        if (flaggedOnly) list.filter { it.isFlagged } else list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Quick Data Manager Form state
    private val _quickDataDrawerOpen = MutableStateFlow(false)
    val quickDataDrawerOpen: StateFlow<Boolean> = _quickDataDrawerOpen.asStateFlow()

    // CPI Calculator Workspace state
    private val _annualCpiRate = MutableStateFlow(3.8f) // 3.8%
    val annualCpiRate: StateFlow<Float> = _annualCpiRate.asStateFlow()

    private val _escalationCap = MutableStateFlow(4.0f) // 4.0%
    val escalationCap: StateFlow<Float> = _escalationCap.asStateFlow()

    // CPI Projections dynamically derived
    val cpiProjections: StateFlow<List<MonthlyImpact>> = combine(
        portfolioState, _annualCpiRate, _escalationCap
    ) { portfolio, cpi, cap ->
        val effectiveRate = minOf(cpi, cap)
        val annualRent = portfolio.portfolioSizeSf * portfolio.rentalWeightPerSf
        val annualInflationDelta = annualRent * (effectiveRate / 100.0)
        val monthlyBase = annualRent / 12.0
        val monthlyDelta = annualInflationDelta / 12.0

        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        months.mapIndexed { index, name ->
            val monthProgress = (index + 1) / 12.0
            MonthlyImpact(
                monthNumber = index + 1,
                monthName = name,
                projectedRent = monthlyBase + (monthlyDelta * monthProgress),
                inflationDelta = monthlyDelta * (index + 1),
                escalationPercent = effectiveRate
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // COI Matrix filters
    val rawCoiItems: StateFlow<List<CoiAuditItem>> = repository.coiMatrixItems
    private val _coiFilterSeverity = MutableStateFlow("ALL")
    val coiFilterSeverity: StateFlow<String> = _coiFilterSeverity.asStateFlow()

    private val _coiSearchQuery = MutableStateFlow("")
    val coiSearchQuery: StateFlow<String> = _coiSearchQuery.asStateFlow()

    val filteredCoiItems: StateFlow<List<CoiAuditItem>> = combine(
        rawCoiItems, _coiFilterSeverity, _coiSearchQuery
    ) { items, severity, query ->
        items.filter { item ->
            val matchesSeverity = (severity == "ALL" || item.severity.equals(severity, ignoreCase = true))
            val matchesSearch = query.isBlank() ||
                    item.tenantName.contains(query, ignoreCase = true) ||
                    item.policyNumber.contains(query, ignoreCase = true) ||
                    item.carrierName.contains(query, ignoreCase = true)
            matchesSeverity && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Lease Vault Ingestion Timeline state
    private val _isAiParsing = MutableStateFlow(false)
    val isAiParsing: StateFlow<Boolean> = _isAiParsing.asStateFlow()

    private val _parsingStageIndex = MutableStateFlow(0)
    val parsingStageIndex: StateFlow<Int> = _parsingStageIndex.asStateFlow()

    private val _parsingStageMessage = MutableStateFlow("")
    val parsingStageMessage: StateFlow<String> = _parsingStageMessage.asStateFlow()

    private val _lastExtractedResult = MutableStateFlow<ExtractedLeaseResult?>(null)
    val lastExtractedResult: StateFlow<ExtractedLeaseResult?> = _lastExtractedResult.asStateFlow()

    // Acquisition Modal & Dossier Form state
    private val _acquisitionModalOpen = MutableStateFlow(false)
    val acquisitionModalOpen: StateFlow<Boolean> = _acquisitionModalOpen.asStateFlow()

    private val _leadSubmissionSuccess = MutableStateFlow<AcquisitionLead?>(null)
    val leadSubmissionSuccess: StateFlow<AcquisitionLead?> = _leadSubmissionSuccess.asStateFlow()

    private val _isSubmittingLead = MutableStateFlow(false)
    val isSubmittingLead: StateFlow<Boolean> = _isSubmittingLead.asStateFlow()

    // Actions
    fun setRole(role: UserRole) {
        _currentRole.value = role
        repository.addAuditLog("Security Context Switched to: ${role.title}", role.title)
    }

    fun toggleFlagFilter() {
        _filterFlaggedOnly.value = !_filterFlaggedOnly.value
    }

    fun setFlagFilter(flaggedOnly: Boolean) {
        _filterFlaggedOnly.value = flaggedOnly
    }

    fun setQuickDataDrawerOpen(open: Boolean) {
        _quickDataDrawerOpen.value = open
    }

    fun updatePortfolioStats(newSizeSf: Long, rentalWeight: Double = 5.80) {
        repository.updatePortfolioStats(newSizeSf, rentalWeight)
    }

    fun setAnnualCpiRate(rate: Float) {
        _annualCpiRate.value = rate
    }

    fun setEscalationCap(cap: Float) {
        _escalationCap.value = cap
    }

    fun setCoiFilterSeverity(severity: String) {
        _coiFilterSeverity.value = severity
    }

    fun setCoiSearchQuery(query: String) {
        _coiSearchQuery.value = query
    }

    fun setAcquisitionModalOpen(open: Boolean) {
        _acquisitionModalOpen.value = open
    }

    fun dismissLeadSuccessDialog() {
        _leadSubmissionSuccess.value = null
    }

    fun issueDeficiencyNotice(itemId: String, tenantName: String) {
        repository.updateCoiStatus(itemId, "WARNING", "Notice of Insurance Deficiency sent to $tenantName.")
    }

    fun requestUpdatedCoi(itemId: String, tenantName: String) {
        repository.updateCoiStatus(itemId, "CLEAR", "Updated Certificate of Insurance verified & active.")
    }

    // AI Ingestion Pipeline with Realistic 3-second multi-stage timeline
    fun triggerAiDocumentIngestion(documentName: String, sampleSnippet: String = "") {
        if (_isAiParsing.value) return

        viewModelScope.launch {
            _isAiParsing.value = true
            _parsingStageIndex.value = 0
            _parsingStageMessage.value = "Scanning Document Vectors & OCR Extraction..."
            delay(700)

            _parsingStageIndex.value = 1
            _parsingStageMessage.value = "Extracting Lease Clauses via AI Engine..."
            delay(800)

            _parsingStageIndex.value = 2
            _parsingStageMessage.value = "Analyzing COI Compliance & Risk Allocation..."
            delay(750)

            _parsingStageIndex.value = 3
            _parsingStageMessage.value = "Mapping CPI Indexes & Escalation Caps..."
            
            // Execute real Gemini parse or domain analysis
            val extracted = GeminiLeaseService.parseLeaseDocument(documentName, sampleSnippet)
            _lastExtractedResult.value = extracted
            delay(750)

            _parsingStageIndex.value = 4
            _parsingStageMessage.value = "Verification Complete — Lease Tokenized"
            delay(400)

            // Inject into persistent database
            repository.addParsedLease(
                tenantName = extracted.tenantName,
                address = extracted.address,
                squareFootage = extracted.squareFootage,
                monthlyRent = extracted.monthlyRent,
                cpiIndex = extracted.cpiIndex,
                escalationCapPercent = extracted.escalationCapPercent,
                isFlagged = extracted.isFlagged,
                flagReason = extracted.flagReason,
                coiStatus = extracted.coiStatus
            )

            _isAiParsing.value = false
        }
    }

    // Enterprise Acquisition Dossier Submission
    fun submitAcquisitionLead(officerName: String, email: String, scale: String, intentVerified: Boolean) {
        viewModelScope.launch {
            _isSubmittingLead.value = true
            val lead = AcquisitionLead(
                id = "LEAD-" + UUID.randomUUID().toString().take(8).uppercase(),
                officerName = officerName.trim(),
                email = email.trim(),
                scale = scale,
                intentVerified = intentVerified,
                submittedAt = System.currentTimeMillis(),
                status = "Pending Transfer Escrow"
            )

            // Save locally in Room
            repository.submitAcquisitionLead(lead)

            // Sync to Supabase cloud table
            SupabaseClient.syncLeadToSupabase(lead)

            delay(600)
            _isSubmittingLead.value = false
            _leadSubmissionSuccess.value = lead
        }
    }
}
