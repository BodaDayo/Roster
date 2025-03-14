package com.rgbstudios.roster.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.ui.CallRosterScreen
import com.rgbstudios.roster.ui.LeaveRosterScreen
import com.rgbstudios.roster.ui.ResourcesScreen
import com.rgbstudios.roster.ui.StaffListScreen

@Composable
fun AppNavigation(navController: NavHostController, rosterViewModel: RosterViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.CallRoster.route
    ) {
        composable(Screen.CallRoster.route) { CallRosterScreen(rosterViewModel) }
        composable(Screen.LeaveRoster.route) { LeaveRosterScreen(rosterViewModel) }
        composable(Screen.StaffList.route) { StaffListScreen( rosterViewModel) }
        composable(Screen.Resources.route) { ResourcesScreen(rosterViewModel) }
    }
}
