package com.rgbstudios.roster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rgbstudios.roster.R
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.navigation.Screen
import com.rgbstudios.roster.utils.calculateMonthProgress
import com.rgbstudios.roster.utils.getCurrentMonth
import com.rgbstudios.roster.utils.getCurrentYear
import com.rgbstudios.roster.utils.getLeaveStatus
import com.rgbstudios.roster.utils.getMonthInfo
import com.rgbstudios.roster.utils.getUnitName
import com.rgbstudios.roster.utils.getWeekDateRange

@Composable
fun LeftStaffColumn(
    screen: Screen,
    staffList: List<StaffMember>,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onExpandToggle() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isExpanded) {
                IconButton(
                    onClick = { onExpandToggle() },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = stringResource(R.string.close_left_column_button),
                    )
                }
                Text(
                    text = when (screen.route) {
                        "call_roster" -> "PTs on Call"
                        "leave_roster" -> "PTs on Leave"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Text(
                    text = when (screen.route) {
                        "call_roster" -> "PTOC"
                        "leave_roster" -> "PTOL"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        when (screen.route) {
            "call_roster" -> LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(staffList.size) { index ->
                    when (index) {
                        3 -> {
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .width(64.dp),
                                thickness = 1.dp,
                                color = Color.LightGray
                            )
                            Text(
                                text = "GYM",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        4 -> {
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .width(64.dp),
                                thickness = 1.dp,
                                color = Color.LightGray
                            )
                            Text(
                                text = "2nd",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        5 -> Text(
                            text = "3rd",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    // Display the staff item
                    if (isExpanded) {
                        StaffDetailCard(staffList[index])
                    } else {
                        StaffAvatarItem(staffList[index], false)
                    }
                }
            }
            "leave_roster" -> LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(staffList.size) { index ->
                    // Display the staff item
                    if (isExpanded) {
                        StaffDetailCard(staffList[index])
                    } else {
                        StaffAvatarItem(staffList[index], false)
                    }
                }
            }
        }
    }
}

@Composable
fun StaffAvatarItem(staff: StaffMember, isExpanded: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Gray)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = staff.avatarUri,
                    placeholder = painterResource(id = R.drawable.ic_user),
                    error = painterResource(id = R.drawable.ic_user) // Fallback if loading fails
                ),
                contentDescription = staff.firstName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        if (!isExpanded) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = staff.firstName,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun StaffDetailCard(staff: StaffMember) {

    Row(
        modifier = Modifier.clickable {  }, // TODO open dialog showing staff details
        verticalAlignment = Alignment.CenterVertically
    ) {
        StaffAvatarItem(staff, true)
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row {
                Text(text = staff.firstName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.padding(2.dp))
                Text(text = staff.lastName, style = MaterialTheme.typography.titleMedium)
            }

            Text(text = getUnitName(staff.unit))
            Text(text = staff.phone, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun StaffListDetailCard(staff: StaffMember) {
    val isOnLeave = getLeaveStatus(staff)

    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 8.dp),
        thickness = 1.dp,
        color = Color.LightGray
    )

    Row(
        modifier = Modifier.clickable {  }, // TODO open dialog showing staff details
        verticalAlignment = Alignment.CenterVertically
    ) {
        StaffAvatarItem(staff, true)

        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = staff.lastName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.padding(2.dp))
                Text(text = staff.firstName, style = MaterialTheme.typography.titleMedium)
            }
            Text(text = getUnitName(staff.unit))
            if (isOnLeave) {
                Text(text = "On Leave!", style = MaterialTheme.typography.bodyMedium,color = MaterialTheme.colorScheme.error)
            }
            Text(text = staff.phone, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun QuarterCalendarColumn(
    selectedWeek: Pair<Int, Int>,
    onWeekSelected: (Int, Int) -> Unit,
    isEditMode: Boolean,
    modifier: Modifier = Modifier
) {
    val years = (2024..(getCurrentYear() + 1)).toList()

    // Calculate the index for the selected week in the list
    val initialScrollIndex = remember {
        val yearIndex = years.indexOf(selectedWeek.first)
        val quarterIndex = (selectedWeek.second - 1) / 12
        yearIndex * 5 + quarterIndex
    }

    // LazyListState for managing scrolling
    val listState = rememberLazyListState()

    // Track whether the initial scroll has been performed
    var hasScrolledInitially by remember { mutableStateOf(false) }

    // Perform the initial scroll once when the composable is first displayed
    LaunchedEffect(hasScrolledInitially) {
        if (!hasScrolledInitially) {
            listState.scrollToItem(initialScrollIndex)
            hasScrolledInitially = true
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        years.forEach { year ->
            item {
                Text(
                    text = "$year",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.End
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }

            // Add 4 quarters for this year
            items(4) { quarter ->
                QuarterItem(
                    quarterNumber = quarter + 1,
                    year = year,
                    selectedWeek = selectedWeek,
                    onWeekSelected = onWeekSelected,
                    isEditMode = isEditMode
                )
            }
        }
    }
}

@Composable
fun QuarterItem(
    quarterNumber: Int,
    year: Int,
    selectedWeek: Pair<Int, Int>,
    onWeekSelected: (Int, Int) -> Unit,
    isEditMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Q$quarterNumber",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .align(Alignment.End),
            textAlign = TextAlign.End
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) { month ->
                MonthColumnItem(
                    monthNumber = (quarterNumber - 1) * 3 + month + 1,
                    year = year,
                    quarterNumber = quarterNumber,
                    selectedWeek = selectedWeek,
                    onWeekSelected = onWeekSelected,
                    isEditMode = isEditMode
                )
            }
        }
    }
}

@Composable
fun MonthColumnItem(
    monthNumber: Int,
    year: Int,
    quarterNumber: Int,
    selectedWeek: Pair<Int, Int>,
    onWeekSelected: (Int, Int) -> Unit,
    isEditMode: Boolean
) {
    val monthAbbreviation = getMonthInfo(monthNumber).first

    Column(modifier = Modifier.padding(4.dp)) {
        Text(
            text = monthAbbreviation,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.End)
        )
        repeat(4) { week ->
            val weekNumber = (monthNumber - 1) * 4 + week + 1
            val isSelected = selectedWeek.first == year && selectedWeek.second == weekNumber
            val dateRange = getWeekDateRange(year = year, weekNumber = weekNumber)
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onWeekSelected(year, weekNumber) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = if (isSelected) BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.primary
                ) else null
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Week ${week + 1}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
                        )
                        Text(
                            text = dateRange,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthCalendarColumn(
    selectedMonth: Int,
    selectedWeek: Int,
    year: Int,
    onWeekSelected: (Int) -> Unit,
    isEditMode: Boolean,
    modifier: Modifier = Modifier
) {
    val monthAbbreviation = getMonthInfo(selectedMonth).first

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        Text(
            text = "$monthAbbreviation $year",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.End)
        )
        LazyColumn {
            items(4) { week ->
                val weekNumber = (selectedMonth - 1) * 4 + week + 1
                val isSelected = weekNumber == selectedWeek
                val dateRange = getWeekDateRange(year = year, weekNumber = weekNumber)

                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { onWeekSelected(weekNumber) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = if (isSelected) BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.primary
                    ) else null
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Week ${week + 1}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
                            )
                            Text(
                                text = dateRange,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthListColumn(
    selectedYear: Int,
    selectedMonth: Int,
    isLeftExpanded: Boolean,
    onMonthSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val years = (2024..(getCurrentYear() + 1)).toList()
    val currentYear = getCurrentYear()
    val currentMonth = getCurrentMonth()

    // LazyListState for managing scrolling
    val listState = rememberLazyListState()

    // Track whether the initial scroll has been performed
    var hasScrolledInitially by remember { mutableStateOf(false) }

    // Perform the initial scroll when the screen loads
    LaunchedEffect(selectedYear, selectedMonth) {
        if (!hasScrolledInitially) {
            val yearIndex = years.indexOf(selectedYear)
            if (yearIndex != -1) {
                val scrollIndex = yearIndex * 13 + selectedMonth
                listState.scrollToItem(scrollIndex)
            }
            hasScrolledInitially = true
        }

    }

    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        state = listState
    ) {
        years.forEach { year ->
            item {
                Text(
                    text = "$year",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.End
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }

            items(12) { monthIndex ->
                val monthName = getMonthInfo(monthIndex + 1)
                val isSelected = selectedYear == year && selectedMonth == monthIndex + 1

                // Calculate progress for the selected month
                val progress = when {
                    year < currentYear || (year == currentYear && monthIndex + 1 < currentMonth) -> 1f
                    year > currentYear || monthIndex + 1 > currentMonth -> 0f
                    else -> calculateMonthProgress(currentYear, currentMonth)
                }

                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { onMonthSelected(year, monthIndex + 1) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isLeftExpanded) monthName.first else monthName.second,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            textAlign = TextAlign.End
                        )

                        // Progress Indicator Bar
                        if(isSelected){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(50)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .height(4.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(50)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

