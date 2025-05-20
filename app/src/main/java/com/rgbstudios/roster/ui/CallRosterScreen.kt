package com.rgbstudios.roster.ui

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rgbstudios.roster.R
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.navigation.Screen
import com.rgbstudios.roster.ui.components.CallTypeSegmentedButton
import com.rgbstudios.roster.ui.components.EditModeBar
import com.rgbstudios.roster.ui.components.MonthCalendarColumn
import com.rgbstudios.roster.ui.components.QuarterCalendarColumn
import com.rgbstudios.roster.ui.components.LeftStaffColumn
import com.rgbstudios.roster.utils.EditCallDialog
import com.rgbstudios.roster.utils.getCurrentWeekOfYear
import com.rgbstudios.roster.utils.getCurrentYear
import com.rgbstudios.roster.utils.getStaffOnCall
import com.rgbstudios.roster.utils.getMonthForWeek

@Composable
fun CallRosterScreen(rosterViewModel: RosterViewModel) {
    val wardCallText = stringResource(R.string.ward_call)

    val staffList by rosterViewModel.staffList.collectAsState()
    val isSignedIn by rosterViewModel.adminSignedIn.collectAsState()

    // States
    var isInEditMode by remember { mutableStateOf(false) }
    var isLeftExpanded by remember { mutableStateOf(false) }
    var showEditStaffDialog by remember { mutableStateOf(false) }
    var selectedCallType by remember { mutableStateOf(wardCallText) }

    var selectedYear by remember { mutableIntStateOf(getCurrentYear()) }
    var selectedWeek by remember { mutableIntStateOf(getCurrentWeekOfYear()) }
    val selectedMonth = getMonthForWeek(selectedWeek)

    val staffOnCall = getStaffOnCall(staffList, selectedYear, selectedWeek)

    // Gesture detection for swipe
    val swipeableModifier = Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures { change, dragAmount ->
            change.consume() // Consume the gesture to prevent it fr
            // om propagating further
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
                // Edit Mode Indicator + Toggle
                if (isInEditMode) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        EditModeBar { isInEditMode = false }

                        CallTypeSegmentedButton(
                            onSelectionChanged = { selectedCallType = it }
                        )
                    }
                }

                // Main Content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    // Left Column
                    LeftStaffColumn(
                        screen = Screen.CallRoster,
                        staffList = staffOnCall,
                        isExpanded = isLeftExpanded,
                        onExpandToggle = { isLeftExpanded = !isLeftExpanded },
                        modifier = Modifier
                    )

                    // Right Column
                    if (isLeftExpanded) {
                        Spacer(modifier = Modifier.weight(1f))
                        MonthCalendarColumn(
                            selectedMonth = selectedMonth,
                            selectedWeek = selectedWeek,
                            year = getCurrentYear(),
                            onWeekSelected = { selectedWeek = it },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        QuarterCalendarColumn(
                            selectedWeek = selectedYear to selectedWeek,
                            onWeekSelected = { year, week, showDialog ->
                                selectedYear = year
                                selectedWeek = week
                                showEditStaffDialog = showDialog
                            },
                            isInEditMode = isInEditMode,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }


            }
        }
    )

    // Show edit staff dialog when triggered
    if (showEditStaffDialog) {
        val filteredStaffOnCall = if (selectedCallType == wardCallText) {
            staffOnCall.filter { staff ->
                staff.onCallDates.any { (year, weeks) -> year == selectedYear && weeks.contains(selectedWeek) }
            }
        } else {
            staffOnCall.filter { staff ->
                staff.gymCallDates.any { (year, weeks) -> year == selectedYear && weeks.contains(selectedWeek) }
            }
        }

        EditCallDialog(
            rosterViewModel = rosterViewModel,
            staffList = if (selectedCallType == wardCallText) staffList else staffList.filter { it.role == 1 },
            staffOnCall = filteredStaffOnCall,
            selectedWeek = selectedWeek,
            selectedYear = selectedYear,
            callType = selectedCallType,
            onDismissRequest = { showEditStaffDialog = false }
        )
    }
}





