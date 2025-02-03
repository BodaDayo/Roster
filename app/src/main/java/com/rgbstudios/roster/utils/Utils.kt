package com.rgbstudios.roster.utils

import androidx.compose.ui.graphics.painter.Painter
import com.rgbstudios.roster.data.model.StaffMember
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale

fun getCurrentYear(): Int {
    return Calendar.getInstance().get(Calendar.YEAR)
}

fun getCurrentMonth(): Int {
    return Calendar.getInstance().get(Calendar.MONTH)
}

fun getCurrentWeekOfYear(): Int {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY // Set Monday as the first day of the week
    calendar.minimalDaysInFirstWeek = 4 // ISO 8601: Weeks start with at least 4 days in the first week
    return calendar.get(Calendar.WEEK_OF_YEAR)
}

fun getMonthInfo(selectedMonth: Int): Pair<String, String> {
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    return Pair(months[selectedMonth - 1], monthNames[selectedMonth - 1])
}

fun getUnitName(unit: Int): String {
    return when (unit) {
        1 -> "Neurology"
        2 -> "Orthopedics"
        3 -> "Paediatrics"
        else -> "Unknown Unit"
    }
}

fun getRoleName(role: Int): String {
    return when (role) {
        1 -> "Intern Physiotherapist"
        2 -> "NYSC Physiotherapist"
        3 -> "Physiotherapist"
        4 -> "Senior Physiotherapist"
        5 -> "Principal Physiotherapist"
        6 -> "Deputy Director"
        7 -> "Director"
        else -> "N/A"
    }
}

fun getCallStats(onCallDates: List<Int>): String {
    val currentWeek = getCurrentWeekOfYear()
    val callsDone = onCallDates.count { it < currentWeek }
    val totalCalls = onCallDates.size
    return "Calls Done: $callsDone/$totalCalls"
}

fun getLeaveStatus(staff: StaffMember): Boolean {
    val currentYear = getCurrentYear()
    val currentMonth = getCurrentMonth()

    // Check if there's an entry in leaveDates matching the current year and month
    return staff.leaveDates.any { (year, month) -> year == currentYear && month == currentMonth }
}

fun getWeekDateRange(year: Int, weekNumber: Int): String {
    val calendar = Calendar.getInstance()

    // Set the year and week number
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.WEEK_OF_YEAR, weekNumber)

    // Set to the first day of the week (Monday)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val startDay = calendar.get(Calendar.DAY_OF_MONTH)
    val startMonth =
        calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault())

    // Move to the last day of the week (Sunday)
    calendar.add(Calendar.DAY_OF_MONTH, 6)
    val endDay = calendar.get(Calendar.DAY_OF_MONTH)
    val endMonth =
        calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault())

    // Return formatted date range
    return "${formatDayWithSuffix(startDay)} $startMonth\n${formatDayWithSuffix(endDay)} $endMonth"

}

private fun formatDayWithSuffix(day: Int): String {
    return when {
        day in 11..13 -> "${day}th"
        day % 10 == 1 -> "${day}st"
        day % 10 == 2 -> "${day}nd"
        day % 10 == 3 -> "${day}rd"
        else -> "${day}th"
    }
}

fun getStaffOnCall(
    staffList: List<StaffMember>,
    selectedYear: Int,
    selectedWeek: Int
): List<StaffMember> {
    // Filter staff who are on call for the selected week of the selected year
    val filteredStaff = staffList.filter { staff ->
        staff.onCallDates.any { (year, weeks) -> year == selectedYear && weeks.contains(selectedWeek)} ||
                staff.gymCallDates.any { (year, weeks) -> year == selectedYear && weeks.contains(selectedWeek) }
    }

    val placeholder = StaffMember(
        id = -1,
        firstName = "N/A",
        lastName = "",
        role = -1,
        unit = -1,
        avatarUri = "",
        onCallDates = emptyList(),
        gymCallDates = emptyList(),
        leaveDates = emptyList(),
        phone = ""
    )

    // Ward call staffs, sorted by unit (1, 2, 3)
    val wardCallStaff =
        filteredStaff.filter { staff ->
            staff.role == 1 && !staff.gymCallDates
                .any { (year, weeks) -> year == selectedYear && weeks.contains(selectedWeek) }
        }.sortedBy { it.unit }
            .take(3)

    // Gym call staff
    val gymCallStaff = filteredStaff.find { staff ->
        staff.gymCallDates.any { (year, weeks) -> year == selectedYear && weeks.contains(selectedWeek) }
    }

    // 2nd on Call
    val secondOnCall = filteredStaff.find { it.role in 2..5 }

    // 3rd on Call
    val thirdOnCall = filteredStaff.find { it.role in 6..7 }

    // Assemble the final list of 6 items, filling with placeholders as needed
    return listOf(
        wardCallStaff.getOrNull(0) ?: placeholder,
        wardCallStaff.getOrNull(1) ?: placeholder,
        wardCallStaff.getOrNull(2) ?: placeholder,
        gymCallStaff ?: placeholder,
        secondOnCall ?: placeholder,
        thirdOnCall ?: placeholder
    )
}

fun getStaffOnLeave(
    staffList: List<StaffMember>,
    selectedYear: Int,
    selectedMonth: Int
): List<StaffMember> {
    // Filter staff on leave for the given year and week
    val onLeaveStaff = staffList.filter { staff ->
        staff.leaveDates.any { (year, month) ->
            year == selectedYear && month == selectedMonth
        }
    }

    val sortedStaffList = onLeaveStaff.sortedBy { it.role }

    return sortedStaffList.ifEmpty {
        listOf(StaffMember(
        id = -1,
        firstName = "N/A",
        lastName = "",
        role = -1,
        unit = -1,
        avatarUri = "",
        onCallDates = emptyList(),
        gymCallDates = emptyList(),
        leaveDates = emptyList(),
        phone = ""
    ))
    }
}

fun getMonthForWeek(selectedWeek: Int): Int {
    // Weeks per month
    val weeksInMonth = 4 // Assuming 4 weeks per month

    // Calculate the month by dividing the selected week number
    return ((selectedWeek - 1) / weeksInMonth) + 1
}

fun getWeekForMonth(month: Int, year: Int): Int {
    // Create a Calendar instance and set it to the first day of the given month and year
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1) // Calendar months are 0-based (January is 0)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    // Get the week number of the first day of the month
    return calendar.get(Calendar.WEEK_OF_YEAR)
}


fun calculateMonthProgress(year: Int, month: Int): Float {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1) // Set to the first day of the month

    val totalDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    // Ensure we don't exceed the total days in the month
    return (currentDayOfMonth.toFloat() / totalDaysInMonth.toFloat()).coerceIn(0f, 1f)
}
