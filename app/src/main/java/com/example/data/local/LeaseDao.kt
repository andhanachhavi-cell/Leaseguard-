package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AcquisitionLead
import com.example.data.model.LeaseItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaseDao {
    @Query("SELECT * FROM leases ORDER BY isFlagged DESC, matchScore DESC")
    fun getAllLeases(): Flow<List<LeaseItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLease(lease: LeaseItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(leases: List<LeaseItem>)

    @Query("SELECT COUNT(*) FROM leases")
    suspend fun getLeaseCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: AcquisitionLead)

    @Query("SELECT * FROM acquisition_leads ORDER BY submittedAt DESC")
    fun getAllLeads(): Flow<List<AcquisitionLead>>
}
