package com.rgbstudios.roster.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.data.model.Suggestion
import com.rgbstudios.roster.utils.DatabaseField
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.storage.storage
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class SupabaseRepository {

    private var staffSubscriptionInitialized = false
    private var suggestionsSubscriptionInitialized = false
    private var authStateListenerInitialized = false

    private var authStateListenerJob: Job? = null
    private var staffSubscription: Job? = null
    private var suggestionsSubscription: Job? = null

    private suspend fun getSupabaseClient(): SupabaseClient {
        return try {
            SupabaseClientInstance.getClient()
        } catch (e: Exception) {
            throw Exception("Offline mode - using cached data")
        }
    }

    fun isAuthStateListenerActive(): Boolean = authStateListenerInitialized
    fun areStaffSubscriptionsActive(): Boolean = staffSubscriptionInitialized
    fun areSuggestionsSubscriptionsActive(): Boolean = suggestionsSubscriptionInitialized

    // --- Auth Methods ---
    suspend fun addAuthStateListener(
        scope: CoroutineScope,
        callback: (Boolean) -> Unit
    ) {
        if (authStateListenerInitialized) return
        authStateListenerInitialized = true

        try {
            val client = getSupabaseClient()
            authStateListenerJob = client.auth.sessionStatus
                .onEach { status ->
                    when (status) {
                        is SessionStatus.Authenticated -> callback(true)
                        is SessionStatus.NotAuthenticated -> callback(false)
                        is SessionStatus.RefreshFailure -> callback(false)
                        SessionStatus.Initializing -> callback(false)
                    }
                }
                .launchIn(scope)
        } catch (e: Exception) {
            authStateListenerInitialized = false
            callback(false)
        }
    }

    suspend fun signInAdmin(email: String, password: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val client = getSupabaseClient()
                client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
            }
            Result.success(Unit)
        } catch (e: ResponseException) {
            // Handle API-specific exceptions
            val errorMessage = when (e.response.status.value) {
                400 -> "Invalid email or password. Please try again."
                401 -> "Unauthorized access. Please check your credentials."
                404 -> "User not found. Please sign up first."
                else -> "An unexpected error occurred. Please try again later."
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            // Handle other exceptions
            Result.failure(Exception("An error occurred. Please check your connection and try again."))
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val client = getSupabaseClient()
                client.auth.signOut()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Staff Methods ---

    suspend fun fetchStaffList(): Result<List<StaffMember>> {
        return try {
            val staff = withContext(Dispatchers.IO) {
                val client = getSupabaseClient()
                client.from("staff")
                    .select()
                    .decodeList<StaffMember>()
            }
            Result.success(staff)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addStaff(staff: StaffMember, imageBitmap: Bitmap?): Result<Unit> {
        return try {
            val staffId = staff.id.ifEmpty { java.util.UUID.randomUUID().toString() }
            val imageUrl = imageBitmap?.let { uploadImage(it, staffId) } ?: ""
            val newStaff = staff.copy(id = staffId, imageUrl = imageUrl)

            withContext(Dispatchers.IO) {
                val client = getSupabaseClient()
                client.from("staff").insert(newStaff)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadImage(
        imageBitmap: Bitmap,
        staffId: String
    ): String {
        val client = getSupabaseClient()
        val storage = client.storage.from("staff-photos")
        val fileName = "$staffId.jpg"

        return withContext(Dispatchers.IO) {
            try {
                // Compress bitmap to JPEG format with 80% quality
                val outputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val compressedBytes = outputStream.toByteArray()

                storage.upload(fileName, compressedBytes){
                    upsert = true
                }
                storage.publicUrl(fileName)
            } catch (e: Exception) {
                throw Exception("Image upload failed: ${e.localizedMessage}")
            }
        }
    }

    suspend fun editStaffData(
        staff: StaffMember,
        imageBitmap: Bitmap?,
        fieldToEdit: String
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val client = getSupabaseClient()

                when (fieldToEdit) {
                    DatabaseField.OnCallDates.name -> {
                        val updatedCallDates = staff.onCallDates.ifEmpty {
                            emptyList()
                        }
                        client.from("staff")
                            .update(mapOf("on_call_dates" to updatedCallDates)) {
                                filter {
                                    eq("id", staff.id)
                                }
                            }

                    }

                    DatabaseField.GymCallDates.name -> {
                        val updatedGymDates = staff.gymCallDates.ifEmpty {
                            emptyList()
                        }

                        client.from("staff")
                            .update(mapOf("gym_call_dates" to updatedGymDates)) {
                                filter {
                                    eq("id", staff.id)
                                }
                            }

                    }

                    DatabaseField.LeaveDates.name -> {
                        val updatedLeaveDates = staff.leaveDates.ifEmpty {
                            emptyList()
                        }

                        client.from("staff")
                            .update(mapOf("leave_dates" to updatedLeaveDates)) {
                                filter {
                                    eq("id", staff.id)
                                }
                            }
                    }

                    else -> {
                        val updatedStringFields = mutableMapOf(
                            "first_name" to staff.firstName,
                            "last_name" to staff.lastName,
                            "phone" to staff.phone
                        )

                        if (imageBitmap != null) {
                            val imageUrl = uploadImage(imageBitmap, staff.id)
                            updatedStringFields["image_url"] = imageUrl
                        }

                        client.from("staff")
                            .update(updatedStringFields) {
                                filter { eq("id", staff.id) }
                            }

                        val updatedIntFields = mapOf(
                            "role" to staff.role,
                            "unit" to staff.unit
                        )

                        client.from("staff")
                            .update(updatedIntFields) {
                                filter { eq("id", staff.id) }
                            }
                    }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            val errorMessage = if (imageBitmap != null) {
                "Image uploaded successfully, but staff details update failed"
            } else {
                "Staff details update failed"
            }

            Log.e("UpdateError", errorMessage, e)
            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun removeStaff(staffId: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val client = getSupabaseClient()
                client.from("staff")
                    .delete {
                        filter {
                            eq("id", staffId)
                        }
                    }
                // Delete the corresponding image in storage:
                client.storage.from("staff-photos").delete("$staffId.jpg")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Suggestions Methods ---

    suspend fun fetchSuggestions(): Result<List<Suggestion>> {
        return try {
            val suggestions = withContext(Dispatchers.IO) {
                val client = getSupabaseClient()
                client.from("suggestions")
                    .select()
                    .decodeList<Suggestion>()
            }
            Result.success(suggestions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addSuggestion(suggestion: Suggestion): Result<Unit> {
        return try {
            val suggestionId = suggestion.id.ifEmpty { java.util.UUID.randomUUID().toString() }
            val newSuggestion = suggestion.copy(id = suggestionId)

            withContext(Dispatchers.IO) {
                val client = getSupabaseClient()
                client.from("suggestions").insert(newSuggestion)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resolveSuggestion(suggestionId: String, adminName: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val client = getSupabaseClient()
                client.from("suggestions")
                    .update(
                        mapOf(
                            "resolved" to true,
                            "resolvedBy" to adminName,
                            "resolvedTimestamp" to System.currentTimeMillis()
                        )
                    ) {
                        filter {
                            eq("id", suggestionId)
                        }
                    }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSuggestion(suggestionId: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val client = getSupabaseClient()
                client.from("suggestions")
                    .delete {
                        filter {
                            eq("id", suggestionId)
                        }
                    }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Realtime Subscriptions ---
    @OptIn(SupabaseExperimental::class)
    suspend fun subscribeToStaffUpdates(
        scope: CoroutineScope,
        onUpdate: (List<StaffMember>) -> Unit
    ) {
        if (staffSubscriptionInitialized) return
        staffSubscriptionInitialized = true

        try {
            val client = getSupabaseClient()
            // Pass StaffMember::id as the primary key reference
            staffSubscription = client.from("staff")
                .selectAsFlow(StaffMember::id)
                .onEach { updatedStaffList ->
                    onUpdate(updatedStaffList)
                    OfflineRepository.saveStaff(updatedStaffList)
                }
                .launchIn(scope)
        } catch (e: Exception) {
            staffSubscriptionInitialized = false
            Log.e("SupabaseRepository", "Staff subscription failed", e)
        }
    }

    @OptIn(SupabaseExperimental::class)
    suspend fun subscribeToSuggestionsUpdates(
        scope: CoroutineScope,
        onUpdate: (List<Suggestion>) -> Unit
    ) {
        if (suggestionsSubscriptionInitialized) return
        suggestionsSubscriptionInitialized = true

        try {
            val client = getSupabaseClient()
            suggestionsSubscription = client.from("suggestions")
                .selectAsFlow(Suggestion::id)
                .onEach { updatedSuggestions ->
                    onUpdate(updatedSuggestions)
                    OfflineRepository.saveSuggestions(updatedSuggestions)
                }
                .launchIn(scope)
        } catch (e: Exception) {
            suggestionsSubscriptionInitialized = false
            Log.e("SupabaseRepository", "Staff subscription failed", e)
        }
    }

    fun cleanup() {
        authStateListenerJob?.cancel()
        staffSubscription?.cancel()
        suggestionsSubscription?.cancel()

        staffSubscriptionInitialized = false
        suggestionsSubscriptionInitialized = false
        authStateListenerInitialized = false
    }

}
