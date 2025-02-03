package com.rgbstudios.roster.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.ui.components.StaffListDetailCard
import com.rgbstudios.roster.utils.getRoleName

@Composable
fun StaffListScreen(rosterViewModel: RosterViewModel) {
    val staffList by rosterViewModel.staffList // Observe staffList
    val isLoggedIn by rosterViewModel.isLoggedIn // Observe isLoggedIn

    // States
    var isInEditMode by remember { mutableStateOf(false) }

    // Group staff by role and sort roles numerically
    val groupedStaff = staffList
        .groupBy { it.role }
        .toSortedMap()

    Scaffold(
        floatingActionButton = {
            if (isLoggedIn) {
                FloatingActionButton(
                    onClick = { isInEditMode = !isInEditMode },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = if (isInEditMode) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = if (isInEditMode) "Disable Edit Mode" else "Enable Edit Mode"
                    )
                }
            }
        },
        content = { paddingValues ->

            // Edit Mode Indicator
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isInEditMode) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Edit Mode Enabled",
                            color = Color.Red,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = { isInEditMode = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Disable Edit Mode",
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupedStaff.forEach { (role, staffList) ->
                        val roleName = getRoleName(role)

                        // Sort by unit first, then by last name
                        val sortedStaffList = staffList.sortedWith(
                            compareBy({ it.unit }, { it.lastName })
                        )

                        // Role Header
                        item {
                            Text(
                                text = "$roleName (${sortedStaffList.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Staff Items
                        items(sortedStaffList) { staffMember ->
                            StaffListDetailCard(staffMember)
                        }
                    }
                }
            }
        }
    )
}
