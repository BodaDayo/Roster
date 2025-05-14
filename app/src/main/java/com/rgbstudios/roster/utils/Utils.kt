package com.rgbstudios.roster.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.rgbstudios.roster.R
import com.rgbstudios.roster.data.cache.DataStoreManager
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.worker.ReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun getCurrentYear(): Int {
    return Calendar.getInstance().get(Calendar.YEAR)
}

fun getCurrentMonth(): Int {
    return Calendar.getInstance().get(Calendar.MONTH) + 1
}

fun getCurrentWeek(): Int {
    return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
}

fun getCurrentWeekOfYear(): Int {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY // Set Monday as the first day of the week
    calendar.minimalDaysInFirstWeek = 4 // ISO 8601 Weeks start with at least 4 days in the 1st week
    return calendar.get(Calendar.WEEK_OF_YEAR)
}

fun getFixedWeeks(): List<Int> {
    return listOf(
        5, // Jan
        4, // Feb
        4, // Mar
        5, // Apr
        4, // May
        4, // Jun
        5, // Jul
        4, // Aug
        4, // Sep
        5, // Oct
        4, // Nov
        4  // Dec
    )
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

fun getCallStatus(staff: StaffMember): Boolean {
    val currentYear = getCurrentYear()
    val currentWeek = getCurrentWeek()

    // Check if there's an entry in leaveDates matching the current year and month
    return staff.onCallDates.any { (year, weeks) ->
        year == currentYear && weeks.contains(
            currentWeek
        )
    } ||
            staff.gymCallDates.any { (year, weeks) ->
                year == currentYear && weeks.contains(
                    currentWeek
                )
            }
}

fun getLeaveStatus(staff: StaffMember): Boolean {
    val currentYear = getCurrentYear()
    val currentMonth = getCurrentMonth()

    // Check if there's an entry in leaveDates matching the current year and month
    return staff.leaveDates.any { (year, month) -> year == currentYear && month == currentMonth }
}

fun getRoleOptions(): Map<Int, String> {
    return mapOf(
        1 to "Intern Physiotherapist",
        2 to "NYSC Physiotherapist",
        3 to "Physiotherapist",
        4 to "Senior Physiotherapist",
        5 to "Principal Physiotherapist",
        6 to "Deputy Director",
        7 to "Director"
    )
}

fun getUnitOptions(): Map<Int, String> {
    return mapOf(
        1 to "Neurology",
        2 to "Orthopedics",
        3 to "Paediatrics"
    )
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
        staff.onCallDates.any { (year, weeks) -> year == selectedYear && weeks.contains(selectedWeek) } ||
                staff.gymCallDates.any { (year, weeks) ->
                    year == selectedYear && weeks.contains(
                        selectedWeek
                    )
                }
    }

    val placeholder = StaffMember(
        id = "",
        firstName = "N/A",
        lastName = "",
        role = -1,
        unit = -1,
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
        staff.gymCallDates.any { (year, weeks) ->
            year == selectedYear && weeks.contains(
                selectedWeek
            )
        }
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
        listOf(
            StaffMember(
                id = "",
                firstName = "N/A",
                lastName = "",
                role = -1,
                unit = -1,
                onCallDates = emptyList(),
                gymCallDates = emptyList(),
                leaveDates = emptyList(),
                phone = ""
            )
        )
    }
}

fun getMonthForWeek(selectedWeek: Int): Int {
    // Weeks per month
    val weeksInMonth = 4 // Assuming 4 weeks per month

    // Calculate the month by dividing the selected week number
    return ((selectedWeek - 1) / weeksInMonth) + 1
}

fun calculateMonthProgress(year: Int, monthIndex: Int): Float {
    val calendar = Calendar.getInstance()
    calendar.set(year, monthIndex, 1) // Set to the first day of the month

    val totalDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    // Ensure we don't exceed the total days in the month
    return (currentDayOfMonth.toFloat() / totalDaysInMonth.toFloat()).coerceIn(0f, 1f)
}

// Validation function
fun validateFields(
    firstName: String,
    lastName: String,
    phoneNumber: String,
    selectedRole: Int?,
    selectedUnit: Int?
): String {
    return when {
        firstName.isBlank() -> "First name is required"
        lastName.isBlank() -> "Last name is required"
        phoneNumber.length != 10 -> "Phone number must be exactly 10 digits (e.g., 8012345678)"
        selectedRole == null -> "Please select a role"
        selectedUnit == null -> "Please select a unit"
        else -> ""
    }
}

// Validation function for email and password
fun validateCredentials(username: String, password: String): Boolean {
    return username.isNotBlank() && password.isNotBlank()
}

// ---------------------------------

fun showLongToast(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

fun showShortToast(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

// ---------------------------------

// License Text
fun getLicenseText(context: Context): String {
    return context.getString(R.string.mit_license, getCurrentYear().toString()).trimIndent()
}

// Terms of Service
fun getTermsAndPrivacyText(context: Context): String {
    return context.getString(R.string.terms_of_service).trimIndent()
}

// -----------------------

fun scheduleReminder(
    context: Context,
    staffId: String,
    delayOption: String,
    timeInMillis: Long,
    title: String,
    message: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("Permission", "Notification permission not granted, skipping reminder scheduling.")
        return
    }

    val delay = maxOf(0L, timeInMillis - System.currentTimeMillis())


    val data = workDataOf(
        "title" to title,
        "message" to message,
        "staffId" to staffId,
        "delayOption" to delayOption
    )

    val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .setConstraints(Constraints.NONE)
        .addTag("reminder_$staffId")
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)

    val reminder = DataStoreManager.Reminder(staffId, title, message, delayOption, workRequest.id.toString())
    CoroutineScope(Dispatchers.IO).launch {
        DataStoreManager.saveReminder(context, reminder)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun  showNotification(context: Context, title: String, message: String) {
    val channelId = "reminder_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminder Notifications"
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    // Build Notification
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.roster_icon)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    // Show Notification
    val notificationManager = NotificationManagerCompat.from(context)

    // Check permission before notifying
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("Permission", "Notification permission not granted, skipping notification.")
        return
    }

    notificationManager.notify(1001, builder.build())
}
