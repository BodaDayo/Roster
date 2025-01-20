package com.rgbstudios.roster.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun StaffListScreen(navigator: DestinationsNavigator) {
    Text("Staff List Screen")
}