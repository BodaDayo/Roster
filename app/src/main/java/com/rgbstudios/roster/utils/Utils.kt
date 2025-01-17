package com.rgbstudios.roster.utils

import com.rgbstudios.roster.data.model.StaffMember
import java.util.Calendar

fun getCurrentYear(): Int {
    return Calendar.getInstance().get(Calendar.YEAR)
}

fun getCurrentQuarter(): Int {
    val month = Calendar.getInstance().get(Calendar.MONTH) + 1
    return (month - 1) / 3 + 1
}

fun getCurrentWeekOfYear(): Int {
    return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
}

fun getMonthAbbreviation(quarterNumber: Int, monthIndex: Int): String {
    val months = listOf(
        "Jan", "Feb", "Mar",  // Q1
        "Apr", "May", "Jun",  // Q2
        "Jul", "Aug", "Sep",  // Q3
        "Oct", "Nov", "Dec"   // Q4
    )
    val startMonthIndex = (quarterNumber - 1) * 3 // Index for the first month of the quarter
    return months[startMonthIndex + monthIndex]
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

fun getLeaveStatus(leaveDates: List<Int>): String {
    val currentWeek = getCurrentWeekOfYear()
    if (leaveDates.isEmpty()) return "No Leave Scheduled"

    val upcomingLeave = leaveDates.filter { it >= currentWeek }.sorted()
    val currentLeaveStart = leaveDates.find { it <= currentWeek && currentWeek < it + 4 }

    return when {
        currentLeaveStart != null -> "On Leave"
        upcomingLeave.isNotEmpty() -> "Next Leave: Week ${upcomingLeave.first()} - Week ${upcomingLeave.last()}"
        else -> "Leave Taken"
    }
}

fun getWeekDateRange(year: Int, weekNumber: Int): String {
    val calendar = Calendar.getInstance()

    // Set the year and week number
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.WEEK_OF_YEAR, weekNumber)

    // Set to the first day of the week (Monday)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val startDay = calendar.get(Calendar.DAY_OF_MONTH)
    val startMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault())

    // Move to the last day of the week (Sunday)
    calendar.add(Calendar.DAY_OF_MONTH, 6)
    val endDay = calendar.get(Calendar.DAY_OF_MONTH)
    val endMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault())

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

fun getFilteredAndSortedStaff(
    staffList: List<StaffMember>,
    selectedWeek: Int
): List<StaffMember> {
    // Filter staff who are on call for the selected week
    val filteredStaff = staffList.filter {
        it.onCallDates.contains(selectedWeek) || it.gymCallDates.contains(selectedWeek)
    }

    // Placeholder for "N/A" staff
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

    // Interns, sorted by unit (1, 2, 3)
    val role1Staff = filteredStaff.filter { it.role == 1 && !it.gymCallDates.contains(selectedWeek)}
        .sortedBy { it.unit }
        .take(3)

    // Gym call staff
    val gymCallStaff = filteredStaff.find { it.gymCallDates.contains(selectedWeek) }

    // 2nd on Call
    val role2To5Staff = filteredStaff.find { it.role in 2..5 }

    // 3rd on Call
    val role6Or7Staff = filteredStaff.find { it.role in 6..7 }

    // Assemble the final list of 6 items, filling with placeholders as needed
    return listOf(
        role1Staff.getOrNull(0) ?: placeholder,
        role1Staff.getOrNull(1) ?: placeholder,
        role1Staff.getOrNull(2) ?: placeholder,
        gymCallStaff ?: placeholder,
        role2To5Staff ?: placeholder,
        role6Or7Staff ?: placeholder
    )
}

fun getMonthForWeek(selectedWeek: Int): Int {
    // Weeks per month
    val weeksInMonth = 4 // Assuming 4 weeks per month

    // Calculate the month by dividing the selected week number
    return ((selectedWeek - 1) / weeksInMonth) + 1
}






