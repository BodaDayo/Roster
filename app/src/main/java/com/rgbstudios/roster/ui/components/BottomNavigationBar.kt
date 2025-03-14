package com.rgbstudios.roster.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rgbstudios.roster.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val items = listOf(
        Screen.CallRoster,
        Screen.LeaveRoster,
        Screen.StaffList,
        Screen.Resources
    )

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Image(
                        painter = painterResource(id = screen.iconRes),
                        contentDescription = screen.route,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant) // Auto-adjusts color
                    )
                },
                label = { Text(screen.name) }
            )
        }
    }
}
