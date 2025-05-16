package com.rgbstudios.roster.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.ui.components.EditModeBar
import com.rgbstudios.roster.ui.components.StaffListDetailRow
import com.rgbstudios.roster.utils.AddStaffDialog
import com.rgbstudios.roster.utils.getRoleName

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StaffListScreen(rosterViewModel: RosterViewModel) {
    val staffList by rosterViewModel.staffList.collectAsState()
    val isSignedIn by rosterViewModel.adminSignedIn.collectAsState()

    // States
    var isInEditMode by remember { mutableStateOf(false) }
    var showAddUserDialog by remember { mutableStateOf(false) }
    val isRefreshing by rosterViewModel.isRefreshing.collectAsState()

    // Group staff by role and sort roles numerically
    val groupedStaff = staffList
        .groupBy { it.role }
        .toSortedMap()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { rosterViewModel.fetchStaffList() }
    )

    Scaffold(
        floatingActionButton = {
            if (isSignedIn) {
                FloatingActionButton(
                    onClick = {
                        if (isInEditMode) {
                            showAddUserDialog = true
                        } else {
                            isInEditMode = true
                        }
                    },
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pullRefresh(pullRefreshState)
            ) {
                Column {
                    // Edit Mode Indicator
                    if (isInEditMode) {
                        EditModeBar { isInEditMode = false }
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
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }

                            // Staff Items
                            items(sortedStaffList) { staffMember ->
                                StaffListDetailRow(staffMember, isSignedIn, isInEditMode, rosterViewModel)
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    )

    // Show add user dialog when triggered
    if (showAddUserDialog) {
        AddStaffDialog(
            rosterViewModel = rosterViewModel,
            onDismissRequest = { showAddUserDialog = false },
        )
    }
}