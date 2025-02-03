package com.rgbstudios.roster.data.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.rgbstudios.roster.data.model.StaffMember

class RosterViewModel : ViewModel() {
    val isLoggedIn = mutableStateOf(false)

    val staffList = mutableStateOf<List<StaffMember>>(emptyList())

    init {
        fetchRosterData()
        login()
    }

    fun login() {
        isLoggedIn.value = true
    }

    fun logout() {
        isLoggedIn.value = false
    }

    // Example function to fetch data from Firebase
    fun fetchRosterData() {
        // Simulate Firebase fetch
        staffList.value = listOf(
            StaffMember(
                id = 1,
                firstName = "Essential",
                lastName = "Ogunmilua",
                role = 1,
                unit = 1,
                avatarUri = "",
                onCallDates = listOf(
                    2024 to listOf(1, 2, 3),
                    2025 to listOf(3, 6, 7)
                ),
                gymCallDates = listOf(
                    2025 to listOf(8, 9)
                ),
                leaveDates = listOf(
                    2024 to 2,
                    2025 to 1,
                ),
                phone = "123 456 984"
            ),
            StaffMember(
                id = 2,
                firstName = "Kehinde",
                lastName = "Nomiye",
                role = 1,
                unit = 2,
                avatarUri = "",
                onCallDates = listOf(
                    2024 to listOf(1, 2, 3),
                    2025 to listOf(3, 6, 7)
                ),
                gymCallDates = listOf(
                    2025 to listOf(8, 9)
                ),
                leaveDates = listOf(
                    2024 to 5,
                    2025 to 2,
                ),
                phone = "876 645 999"
            ),
            StaffMember(
                id = 3,
                firstName = "Sope",
                lastName = "Adesida",
                role = 1,
                unit = 3,
                avatarUri = "",
                onCallDates = listOf(
                    2024 to listOf(1, 2, 3),
                    2025 to listOf(3, 6, 7)
                ),
                gymCallDates = listOf(
                    2025 to listOf(8, 9)
                ),
                leaveDates = listOf(
                    2024 to 2,
                    2025 to 1,
                ),
                phone = "123 544 5665"
            ),
            StaffMember(
                id = 4,
                firstName = "Olagoke",
                lastName = "Adegoke",
                role = 1,
                unit = 1,
                avatarUri = "",
                onCallDates = listOf(
                    2024 to listOf(1, 2, 3),
                    2025 to listOf(5, 6, 7)
                ),
                gymCallDates = listOf(
                    2025 to listOf(3, 8, 9)
                ),
                leaveDates = listOf(
                    2024 to 2,
                    2025 to 1,
                ),
                phone = "123 444 563"
            ),
            StaffMember(
                id = 5,
                firstName = "Adeboye",
                lastName = "John",
                role = 3,
                unit = 3,
                avatarUri = "",
                onCallDates = listOf(
                    2024 to listOf(1, 2, 3),
                    2025 to listOf(3, 6, 7)
                ),
                gymCallDates = listOf(
                    2025 to listOf(8, 9)
                ),
                leaveDates = listOf(
                    2024 to 5,
                    2025 to 1,
                ),
                phone = "876 343 664"
            ),
            StaffMember(
                id = 6,
                firstName = "Bolarinde",
                lastName = "Mr",
                role = 7,
                unit = 2,
                avatarUri = "",
                onCallDates = listOf(
                    2024 to listOf(1, 2, 3),
                    2025 to listOf(3, 6, 7)
                ),
                gymCallDates = listOf(
                    2025 to listOf(8, 9)
                ),
                leaveDates = listOf(
                    2024 to 4,
                    2025 to 3
                ),
                phone = "123 345 743"
            ),
            StaffMember(
                id = 7,
                firstName = "Paul",
                lastName = "Onimowo",
                role = 1,
                unit = 2,
                avatarUri = "",
                onCallDates = listOf(
                    2024 to listOf(1, 2, 3),
                    2025 to listOf(5, 6, 7)
                ),
                gymCallDates = listOf(
                    2025 to listOf(8, 9)
                ),
                leaveDates = listOf(
                    2024 to 8,
                    2025 to 5
                ),
                phone = "876 444 754"
            )
        )
    }
}
