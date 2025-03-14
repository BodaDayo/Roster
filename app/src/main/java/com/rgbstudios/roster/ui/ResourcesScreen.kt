package com.rgbstudios.roster.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.ui.components.DetailScreen
import com.rgbstudios.roster.ui.components.MainResourceScreen
import com.rgbstudios.roster.utils.ResourceItem
import com.rgbstudios.roster.utils.UserLoginDialog
import com.rgbstudios.roster.utils.UserLogoutDialog
import androidx.activity.compose.BackHandler

@Composable
fun ResourcesScreen(rosterViewModel: RosterViewModel) {

    var selectedScreen by remember { mutableStateOf<ResourceItem>(ResourceItem.MainScreen) }
    val isSignedIn by rosterViewModel.adminSignedIn.collectAsState()

    // Handle back press
    BackHandler(enabled = selectedScreen != ResourceItem.MainScreen) {
        selectedScreen = ResourceItem.MainScreen
    }

    when (selectedScreen) {
        ResourceItem.MainScreen -> MainResourceScreen(isSignedIn) {
            selectedScreen = it
        }

        ResourceItem.Organogram -> DetailScreen(ResourceItem.Organogram.name, rosterViewModel) {
            selectedScreen = ResourceItem.MainScreen
        }

        ResourceItem.Clerking -> DetailScreen(ResourceItem.Clerking.name, rosterViewModel) {
            selectedScreen = ResourceItem.MainScreen
        }

        ResourceItem.Notifications -> DetailScreen(ResourceItem.Notifications.name, rosterViewModel) {
            selectedScreen = ResourceItem.MainScreen
        }

        ResourceItem.Suggestions -> DetailScreen(ResourceItem.Suggestions.name, rosterViewModel) {
            selectedScreen = ResourceItem.MainScreen
        }

        ResourceItem.Login -> UserLoginDialog(rosterViewModel) {
            selectedScreen = ResourceItem.MainScreen
        }

        ResourceItem.Logout -> UserLogoutDialog(rosterViewModel) {
            selectedScreen = ResourceItem.MainScreen
        }

        ResourceItem.About -> DetailScreen(ResourceItem.About.name, rosterViewModel) {
            selectedScreen = ResourceItem.MainScreen
        }

    }
}
