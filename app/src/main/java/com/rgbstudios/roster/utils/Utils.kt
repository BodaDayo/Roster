package com.rgbstudios.roster.utils

import java.util.Calendar


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
        4 -> "Principal Physiotherapist"
        5 -> "Senior Physiotherapist"
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
