package com.rgbstudios.roster.data.viewmodel
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.data.model.Suggestion
import com.rgbstudios.roster.data.repository.NetworkMonitor
import com.rgbstudios.roster.data.repository.OfflineRepository
import com.rgbstudios.roster.data.repository.SupabaseRepository
import com.rgbstudios.roster.utils.DatabaseField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RosterViewModel(
    private val repository: SupabaseRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _networkStatus = MutableStateFlow(false)
    val networkStatus: StateFlow<Boolean> = _networkStatus

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _staffList = MutableStateFlow<List<StaffMember>>(emptyList())
    val staffList: StateFlow<List<StaffMember>> get() = _staffList

    private val _suggestions = MutableStateFlow<List<Suggestion>>(emptyList())
    val suggestions: StateFlow<List<Suggestion>> get() = _suggestions

    private val _adminSignedIn = MutableStateFlow(false)
    val adminSignedIn: StateFlow<Boolean> get() = _adminSignedIn

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing

    init {
        observeNetwork()
        loadInitialData()
        setupSubscriptions()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                _networkStatus.value = isOnline
                if (isOnline) setupSubscriptions() else repository.cleanup()
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            loadStaffList()
            loadSuggestions()
        }
    }

    private suspend fun loadStaffList() = withContext(Dispatchers.IO) {
        _staffList.value = OfflineRepository.getStaff()
    }

    private suspend fun loadSuggestions() = withContext(Dispatchers.IO) {
        _suggestions.value = OfflineRepository.getSuggestions()
    }

    private fun setupSubscriptions() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                if (isOnline) {
                    try {
                        withContext(Dispatchers.IO) {
                            if (!repository.isAuthStateListenerActive()) {
                                repository.addAuthStateListener(viewModelScope) { isSignedIn ->
                                    _adminSignedIn.value = isSignedIn
                                }
                            }
                            if (!repository.areStaffSubscriptionsActive()) {
                                repository.subscribeToStaffUpdates(viewModelScope) { updatedStaff ->
                                    _staffList.value = updatedStaff
                                    OfflineRepository.saveStaff(updatedStaff)
                                }
                            }
                            if (!repository.areSuggestionsSubscriptionsActive()) {
                                repository.subscribeToSuggestionsUpdates(viewModelScope) { updatedSuggestions ->
                                    _suggestions.value = updatedSuggestions
                                    OfflineRepository.saveSuggestions(updatedSuggestions)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        _errorMessage.value = "Realtime connection failed. Using cached data."
                        loadInitialData()
                    }
                } else {
                    // Optionally update UI to indicate offline status
                    _errorMessage.value = "No network connectivity. Using cached data."
                }
            }
        }
    }

    fun fetchStaffList() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            try {
                val isOnline = networkMonitor.isOnline.first()
                if (isOnline) {
                    try {
                        val result = repository.fetchStaffList()
                        result.fold(
                            onSuccess = {
                                _staffList.value = it
                            },
                            onFailure = {
                                Log.e(
                                    "RosterViewModel",
                                    "Error fetching staff list",
                                    it
                                )
                            }
                        )
                    } catch (e: Exception) {
                        _errorMessage.value = "Realtime connection failed. Using cached data."
                        loadStaffList()
                    }
                } else {
                    _errorMessage.value = "No network connectivity. Using cached data."
                    loadStaffList()
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun fetchSuggestions() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            try {
                val isOnline = networkMonitor.isOnline.first()
                if (isOnline) {
                    try {
                        val result = repository.fetchSuggestions()
                        result.fold(
                            onSuccess = { _suggestions.value = it },
                            onFailure = {
                                Log.e("RosterViewModel", "Error fetching suggestions", it)
                                _errorMessage.value = "Error fetching suggestions"
                            }
                        )
                    } catch (e: Exception) {
                        _errorMessage.value = "Realtime connection failed. Using cached data."
                        loadInitialData()
                    }
                } else {
                    _errorMessage.value = "No network connectivity. Using cached data."
                    loadSuggestions()
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun signInAdmin(username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.signInAdmin(username, password)
            _adminSignedIn.value = result.isSuccess
            withContext(Dispatchers.Main) {
                onResult(result.isSuccess, result.exceptionOrNull()?.message)
            }
        }
    }

    fun signOutAdmin(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.signOut()
            if (result.isSuccess) {
                _adminSignedIn.value = false
            }
            withContext(Dispatchers.Main) {
                onResult(result.isSuccess, result.exceptionOrNull()?.message)
            }

        }
    }

    fun sendPasswordReset(username: String, email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.sendPasswordReset(username, email)
            withContext(Dispatchers.Main) {
                onResult(result.isSuccess, result.exceptionOrNull()?.message)
            }
        }
    }


    fun addStaff(
        staff: StaffMember,
        imageBitmap: Bitmap?,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.addStaff(staff, imageBitmap)
            if (result.isFailure) {
                Log.e(
                    "RosterViewModel",
                    "addStaff failed: ${result.exceptionOrNull()?.localizedMessage}"
                )
            }
            withContext(Dispatchers.Main) {
                onResult(result.isSuccess, result.exceptionOrNull()?.localizedMessage)
            }
            fetchStaffList()
        }
    }

    fun removeStaff(staffId: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.removeStaff(staffId)
            withContext(Dispatchers.Main) {
                onResult(result.isSuccess, result.exceptionOrNull()?.localizedMessage)
            }
            fetchStaffList()
        }
    }

    fun editStaffData(
        staff: StaffMember,
        imageBitmap: Bitmap?,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.editStaffData(
                staff = staff,
                imageBitmap = imageBitmap,
                fieldToEdit = "All"
            )
            if (result.isFailure) {
                Log.e(
                    "RosterViewModel",
                    "editStaff failed: ${result.exceptionOrNull()?.localizedMessage}"
                )
            }
            withContext(Dispatchers.Main) {
                onResult(result.isSuccess, result.exceptionOrNull()?.localizedMessage)
            }
            fetchStaffList()
        }
    }

    fun updateStaffCallDates(
        staffToAddCall: List<StaffMember>,
        staffToRemoveCall: List<StaffMember>,
        callDate: Pair<Int, Int>,
        callType: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val isOnline = networkMonitor.isOnline.first()

            if (!isOnline) {
                withContext(Dispatchers.Main) {
                    onResult(false, "Update failed! No network connectivity.")
                }
                return@launch
            }

            val failedUpdates = mutableListOf<String>()

            // Determine which list to modify based on callType
            val isWardCall = callType == "Ward Call"

            // Process additions
            staffToAddCall.forEach { staff ->
                val updatedCallDates = if (isWardCall) staff.onCallDates.toMutableList() else staff.gymCallDates.toMutableList()
                val existingEntry = updatedCallDates.find { it.first == callDate.first }

                if (existingEntry != null) {
                    // If year exists, update weeks list
                    val updatedWeeks = existingEntry.second.toMutableList().apply {
                        if (!contains(callDate.second)) add(callDate.second)
                    }
                    updatedCallDates[updatedCallDates.indexOf(existingEntry)] = existingEntry.first to updatedWeeks
                } else {
                    // If year doesn't exist, add a new entry
                    updatedCallDates.add(callDate.first to listOf(callDate.second))
                }

                val result = repository.editStaffData(
                    staff = if (isWardCall) staff.copy(onCallDates = updatedCallDates) else staff.copy(gymCallDates = updatedCallDates),
                    imageBitmap = null,
                    fieldToEdit = if (isWardCall) DatabaseField.OnCallDates.name else DatabaseField.GymCallDates.name
                )
                if (result.isFailure) failedUpdates.add("${staff.firstName} ${staff.lastName}")
            }

            // Process removals
            staffToRemoveCall.forEach { staff ->
                val updatedCallDates = if (isWardCall) staff.onCallDates.toMutableList() else staff.gymCallDates.toMutableList()
                val existingEntry = updatedCallDates.find { it.first == callDate.first }

                if (existingEntry != null) {
                    // If the year exists, remove the week
                    val updatedWeeks = existingEntry.second.filterNot { it == callDate.second }

                    if (updatedWeeks.isEmpty()) {
                        // If no weeks remain, remove the entire year entry
                        updatedCallDates.remove(existingEntry)
                    } else {
                        // Otherwise, update the list with the remaining weeks
                        updatedCallDates[updatedCallDates.indexOf(existingEntry)] = existingEntry.first to updatedWeeks
                    }
                }

                val result = repository.editStaffData(
                    staff = if (isWardCall) staff.copy(onCallDates = updatedCallDates) else staff.copy(gymCallDates = updatedCallDates),
                    imageBitmap = null,
                    fieldToEdit = if (isWardCall) DatabaseField.OnCallDates.name else DatabaseField.GymCallDates.name
                )

                if (result.isFailure) failedUpdates.add("${staff.firstName} ${staff.lastName}")
            }

            if (failedUpdates.isEmpty()) {
                withContext(Dispatchers.Main) {
                    onResult(true, null)
                }
                fetchStaffList()
            } else {
                withContext(Dispatchers.Main) {
                    onResult(false, "Failed updates for: ${failedUpdates.joinToString(", ")}")
                }
            }
        }
    }

    fun updateStaffLeaveDates(
        staffToAddLeave: List<StaffMember>,
        staffToRemoveLeave: List<StaffMember>,
        leaveDate: Pair<Int, Int>,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val isOnline = networkMonitor.isOnline.first()

            if (!isOnline) {
                withContext(Dispatchers.Main) {
                    onResult(false, "Update failed! No network connectivity.")
                }
                return@launch
            }

            val failedUpdates = mutableListOf<String>()

            // Process additions
            staffToAddLeave.forEach { staff ->
                val updatedLeaveDates = staff.leaveDates.toMutableList().apply {
                    if (!contains(leaveDate)) add(leaveDate)
                }

                val result = repository.editStaffData(
                    staff = staff.copy(leaveDates = updatedLeaveDates),
                    imageBitmap = null,
                    fieldToEdit = DatabaseField.LeaveDates.name
                )
                if (result.isFailure) failedUpdates.add("${staff.firstName} ${staff.lastName}")
            }

            // Process removals
            staffToRemoveLeave.forEach { staff ->
                val updatedLeaveDates = staff.leaveDates.filterNot { it == leaveDate }

                val result = repository.editStaffData(
                    staff = staff.copy(leaveDates = updatedLeaveDates),
                    imageBitmap = null,
                    fieldToEdit = DatabaseField.LeaveDates.name
                )
                if (result.isFailure) failedUpdates.add("${staff.firstName} ${staff.lastName}")
            }

            if (failedUpdates.isEmpty()) {
                withContext(Dispatchers.Main) {
                    onResult(true, null)
                }
                fetchStaffList()
            } else {
                withContext(Dispatchers.Main) {
                    onResult(false, "Failed updates for: ${failedUpdates.joinToString(", ")}")
                }
            }
        }
    }

    fun addSuggestion(suggestion: Suggestion, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.addSuggestion(suggestion)
            withContext(Dispatchers.Main) {
                onResult(result.isSuccess, result.exceptionOrNull()?.message)
            }
            fetchSuggestions()
        }
    }

    fun resolveSuggestion(
        suggestionId: String,
        adminName: String,
        resolutionNote: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.resolveSuggestion(suggestionId, adminName, resolutionNote)
            withContext(Dispatchers.Main) {
                onResult(result.isSuccess, result.exceptionOrNull()?.message)
            }
            fetchSuggestions()
        }
    }

    fun deleteSuggestion(
        suggestionId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteSuggestion(suggestionId)
            withContext(Dispatchers.Main) {
                onResult(result.isSuccess, result.exceptionOrNull()?.message)
            }
            fetchSuggestions()
        }
    }

    override fun onCleared() {
        repository.cleanup()
        super.onCleared()
    }
}
