package com.rgbstudios.roster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rgbstudios.roster.R
import com.rgbstudios.roster.navigation.Screen

@Composable
fun CustomTopBar(navController: NavController) {

    // Observe the current destination
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Find the current screen
    val currentScreen = when (currentRoute) {
        Screen.CallRoster.route -> Screen.CallRoster
        Screen.LeaveRoster.route -> Screen.LeaveRoster
        Screen.StaffList.route -> Screen.StaffList
        Screen.Resources.route -> Screen.Resources
        else -> null
    }

    // Top Bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row{
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = currentScreen?.name ?: stringResource(R.string.roster_app),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.roster_icon),
                contentDescription = stringResource(R.string.app_icon),
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        }

    }

}
