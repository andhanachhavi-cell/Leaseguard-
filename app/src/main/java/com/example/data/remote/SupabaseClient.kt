package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AcquisitionLead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object SupabaseClient {
    private const val TAG = "SupabaseClient"
    
    // Supabase Credentials
    val SUPABASE_URL: String = try {
        BuildConfig.SUPABASE_URL.ifEmpty { "https://yzbwgkqfmhcjuckemap.supabase.co" }
    } catch (e: Throwable) {
        "https://yzbwgkqfmhcjuckemap.supabase.co"
    }

    val SUPABASE_ANON_KEY: String = try {
        BuildConfig.SUPABASE_ANON_KEY.ifEmpty {
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl6Yndna3FmbWhjcWp1Y2tlbWFwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTE0NDQxOTIsImV4cCI6MjEwNzAyMDE5Mn0.WiSdkQ6ilaW2D_GntuMIwCyjznKRAA3oxCAmMfj11-k"
        }
    } catch (e: Throwable) {
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl6Yndna3FmbWhjcWp1Y2tlbWFwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTE0NDQxOTIsImV4cCI6MjEwNzAyMDE5Mn0.WiSdkQ6ilaW2D_GntuMIwCyjznKRAA3oxCAmMfj11-k"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun syncLeadToSupabase(lead: AcquisitionLead): Boolean = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                put("id", lead.id)
                put("officer_name", lead.officerName)
                put("email", lead.email)
                put("infrastructure_scale", lead.scale)
                put("intent_verified", lead.intentVerified)
                put("submitted_at", lead.submittedAt)
                put("status", lead.status)
            }.toString()

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$SUPABASE_URL/rest/v1/acquisition_leads")
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer $SUPABASE_ANON_KEY")
                .header("Content-Type", "application/json")
                .header("Prefer", "resolution=merge-duplicates")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Log.d(TAG, "Supabase sync response code: ${response.code}")
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.w(TAG, "Supabase sync failed (offline or table setup pending): ${e.message}")
            false
        }
    }
}
