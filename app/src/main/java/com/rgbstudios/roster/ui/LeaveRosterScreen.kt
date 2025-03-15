package com.rgbstudios.roster.ui

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rgbstudios.roster.R
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.navigation.Screen
import com.rgbstudios.roster.ui.components.EditModeBar
import com.rgbstudios.roster.ui.components.LeftStaffColumn
import com.rgbstudios.roster.ui.components.MonthListColumn
import com.rgbstudios.roster.utils.EditLeaveDialog
import com.rgbstudios.roster.utils.getCurrentMonth
import com.rgbstudios.roster.utils.getCurrentYear
import com.rgbstudios.roster.utils.getStaffOnLeave

@Composable
fun LeaveRosterScreen(rosterViewModel: RosterViewModel) {
    val staffList by rosterViewModel.staffList.collectAsState()
    val isSignedIn by rosterViewModel.adminSignedIn.collectAsState()

    // States
    var isInEditMode by remember { mutableStateOf(false) }

    var isLeftExpanded by remember { mutableStateOf(false) }

    var showEditStaffDialog by remember { mutableStateOf(false) }

    var selectedYear by remember { mutableIntStateOf(getCurrentYear()) }
    var selectedMonth by remember { mutableIntStateOf(getCurrentMonth()) }

    val staffOnLeave = getStaffOnLeave(staffList, selectedYear, selectedMonth)

    // Gesture detection for swipe
    val swipeableModifier = Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures { change, dragAmount ->
            change.consume() // Consume the gesture to prevent it from propagating further
            if (dragAmount > 0) {
                // Swipe right
                isLeftExpanded = true
            } else if (dragAmount < 0) {
                // Swipe left
                isLeftExpanded = false
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (isSignedIn) {
                if (!isInEditMode) {
                    FloatingActionButton(
                        onClick = { isInEditMode = !isInEditMode },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.enable_edit_mode)
                        )
                    }
                }
            }
        },
        content = { paddingValues ->
            // Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .then(swipeableModifier)
            ) {
                // Edit Mode Indicator
                if (isInEditMode) {
                    EditModeBar { isInEditMode = false }
                }

                // Main Content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    // Left Column
                    LeftStaffColumn(
                        screen = Screen.LeaveRoster,
                        staffList = staffOnLeave,
                        isExpanded = isLeftExpanded,
                        onExpandToggle = { isLeftExpanded = !isLeftExpanded },
                        modifier = Modifier
                    )

                    // Right Column
                    MonthListColumn(
                        selectedYear = selectedYear,
                        selectedMonth = selectedMonth,
                        isInEditMode = isInEditMode,
                        isLeftExpanded = isLeftExpanded,
                        onMonthSelected = { year, month, showDialog ->
                            selectedYear = year
                            selectedMonth = month
                            showEditStaffDialog = showDialog
                        },
                        modifier = Modifier.weight(1f)
                    )
                }


            }
        }
    )

    // Show edit staff dialog when triggered
    if (showEditStaffDialog) {
        EditLeaveDialog(
            rosterViewModel = rosterViewModel,
            staffList = staffList,
            staffOnLeave = staffOnLeave.filterNot { it.firstName == stringResource(R.string.n_a) },
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            onDismissRequest = { showEditStaffDialog = false }
        )
    }
}