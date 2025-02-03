package com.rgbstudios.roster.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rgbstudios.roster.R
import com.rgbstudios.roster.data.viewmodel.RosterViewModel


@Composable
fun SettingsScreen(rosterViewModel: RosterViewModel) {
    // Observe the login status
    val isLoggedIn = rosterViewModel.isLoggedIn.value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_organogram),
                    contentDescription = "Organogram Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Department Organogram",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable {
                            // Handle click for Organogram
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "Important Department Info Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Important Department Info",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable {
                            // Handle click for Important Department Info
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = "Notifications Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Notifications",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable {
                            // Handle click for Notifications
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_box),
                    contentDescription = "Suggestion Box Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Suggestion Box",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable {
                            // Handle click
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }

        // Login/Logout Button
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = if (isLoggedIn) painterResource(id = R.drawable.ic_logout) else painterResource(id = R.drawable.ic_login),
                    contentDescription = "Login/Logout Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (isLoggedIn) "Logout Admin" else "Login as Admin",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable {
                            // Toggle login state
                            if (isLoggedIn) rosterViewModel.logout() else rosterViewModel.login()
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }

        // Show "Enter Edit Mode" if admin is logged in
        if (isLoggedIn) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_changes),
                        contentDescription = "View Changes Log Icon",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "View Changes Log",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .clickable {
                                // Handle click for View Changes Log
                            }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_about),
                    contentDescription = "About Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "About",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable {
                            // Handle click for About
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}
