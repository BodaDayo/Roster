package com.rgbstudios.roster.navigation

import com.rgbstudios.roster.R

sealed class Screen(val route: String, val iconRes: Int, val name: String) {
    data object CallRoster : Screen("call_roster", R.drawable.ic_user_plus, "Call Roster")
    data object LeaveRoster : Screen("leave_roster", R.drawable.ic_beach, "Leave Roster")
    data object StaffList : Screen("staff_list", R.drawable.ic_people, "Staff List")
    data object Settings : Screen("settings", R.drawable.ic_settings, "Settings")
}
