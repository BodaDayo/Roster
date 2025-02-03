package com.rgbstudios.roster.ui

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.navigation.Screen
import com.rgbstudios.roster.ui.components.LeftStaffColumn
import com.rgbstudios.roster.ui.components.MonthListColumn
import com.rgbstudios.roster.utils.getCurrentMonth
import com.rgbstudios.roster.utils.getCurrentYear
import com.rgbstudios.roster.utils.getStaffOnLeave

@Composable
fun LeaveRosterScreen(rosterViewModel: RosterViewModel) {
    val staffList by rosterViewModel.staffList // Observe staffList
    val isLoggedIn by rosterViewModel.isLoggedIn // Observe isLoggedIn

    // States
    var isInEditMode by remember { mutableStateOf(false) }

    var isLeftExpanded by remember { mutableStateOf(false) }

    var selectedYear by remember { mutableIntStateOf(getCurrentYear()) }
    var selectedMonth by remember { mutableIntStateOf(getCurrentMonth()) }

    val staffOnLeave = getStaffOnLeave(staffList, selectedYear, selectedMonth)

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
            if (isLoggedIn) {
                if (!isInEditMode) {
                    FloatingActionButton(
                        onClick = { isInEditMode = !isInEditMode },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Enable Edit Mode"
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
                        selectedMonth,
                        isLeftExpanded = isLeftExpanded,
                        onMonthSelected = { year, month ->
                            // Handle month click, e.g., update selectedWeek and selectedYear
                            selectedYear = year
                            selectedMonth = month
                        },
                        modifier = Modifier.weight(1f)
                    )
                }


            }
        }
    )


}

@Preview(showBackground = true)
@Composable
fun PreviewLeaveRoster() {
    val rosterViewModel: RosterViewModel = viewModel()
    LeaveRosterScreen(rosterViewModel)
}