package com.rgbstudios.roster.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rgbstudios.roster.R
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.utils.getCallStats
import com.rgbstudios.roster.utils.getCurrentQuarter
import com.rgbstudios.roster.utils.getCurrentWeekOfYear
import com.rgbstudios.roster.utils.getCurrentYear
import com.rgbstudios.roster.utils.getFilteredAndSortedStaff
import com.rgbstudios.roster.utils.getLeaveStatus
import com.rgbstudios.roster.utils.getMonthAbbreviation
import com.rgbstudios.roster.utils.getMonthForWeek
import com.rgbstudios.roster.utils.getUnitName
import com.rgbstudios.roster.utils.getWeekDateRange

@Composable
fun MainScreen(modifier: Modifier) {

    val staffList = listOf(
        StaffMember(
            id = 1,
            firstName = "Essential",
            lastName = "Ogunmilua",
            role = 1,
            unit = 1,
            avatarUri = "",
            onCallDates = listOf(1, 2, 3, 4),
            gymCallDates = emptyList(),
            leaveDates = listOf(4, 5, 6, 7),
            phone = "123 456 984"
        ),
        StaffMember(
            id = 2,
            firstName = "Kehinde",
            lastName = "Nomiye",
            role = 1,
            unit = 2,
            avatarUri = "",
            onCallDates = listOf(1, 3, 8),
            gymCallDates = emptyList(),
            leaveDates = listOf(4, 5, 6, 7),
            phone = "876 645 999"
        ),
        StaffMember(
            id = 1,
            firstName = "Sope",
            lastName = "Adesida",
            role = 1,
            unit = 3,
            avatarUri = "",
            onCallDates = listOf(3, 5, 6),
            gymCallDates = emptyList(),
            leaveDates = listOf(6, 7, 8, 9),
            phone = "123 544 5665"
        ),
        StaffMember(
            id = 1,
            firstName = "Olagoke",
            lastName = "Adegoke",
            role = 1,
            unit = 1,
            avatarUri = "",
            onCallDates = listOf(2, 5, 6),
            gymCallDates = listOf(3),
            leaveDates = listOf(6, 7, 8, 9),
            phone = "123 444 563"
        ),
        StaffMember(
            id = 2,
            firstName = "Adeboye",
            lastName = "John",
            role = 3,
            unit = 3,
            avatarUri = "",
            onCallDates = listOf(1, 2, 3),
            gymCallDates = emptyList(),
            leaveDates = listOf(4, 5, 6, 7),
            phone = "876 343 664"
        ),
        StaffMember(
            id = 1,
            firstName = "Bolarinde",
            lastName = "Mr",
            role = 7,
            unit = 2,
            avatarUri = "",
            onCallDates = listOf(3, 5, 6),
            gymCallDates = emptyList(),
            leaveDates = listOf(6, 7, 8, 9),
            phone = "123 345 743"
        ),
        StaffMember(
            id = 2,
            firstName = "Paul",
            lastName = "Onimowo",
            role = 1,
            unit = 2,
            avatarUri = "",
            onCallDates = listOf(1, 2, 5),
            gymCallDates = emptyList(),
            leaveDates = listOf(4, 5, 6, 7),
            phone = "876 444 754"
        )
    )

    // States
    var isLeftExpanded by remember { mutableStateOf(false) }
    var selectedQuarter by remember { mutableIntStateOf(getCurrentQuarter()) }
    var selectedWeek by remember { mutableIntStateOf(getCurrentWeekOfYear()) }
    val selectedMonth = getMonthForWeek(selectedWeek)

    val staffOnCall = getFilteredAndSortedStaff(staffList, selectedWeek)

    // Layout
    Column(modifier = modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLeftExpanded) {
                IconButton(
                    onClick = { isLeftExpanded = false },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = stringResource(R.string.close_left_column_button),
                    )
                }
            } else {
                IconButton(
                    onClick = { },
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu Icon"
                    )

                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Call Roster",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Gray, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_roster),
                    contentDescription = "App Icon",
                    tint = Color.Unspecified
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
            StaffOnCallColumn(
                staffList = staffOnCall,
                isExpanded = isLeftExpanded,
                onExpandToggle = { isLeftExpanded = !isLeftExpanded },
                modifier = Modifier //.weight(if (isLeftExpanded) 0.7f else 0.3f)
            )

            // Right Column
            if (isLeftExpanded) {
                MonthCalendarColumn(
                    selectedMonth = selectedMonth,
                    selectedWeek = selectedWeek,
                    year = getCurrentYear(),
                    onWeekSelected = { selectedWeek = it },
                    modifier = Modifier.weight(1f)
                )
            } else {
                QuarterCalendarColumn(
                    selectedQuarter = selectedQuarter,
                    selectedWeek = selectedWeek,
                    onQuarterSelected = { selectedQuarter = it },
                    onWeekSelected = { selectedWeek = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StaffOnCallColumn(
    staffList: List<StaffMember>,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 8.dp)
            .clickable { onExpandToggle() }
    ) {
        Text(
            text = "PTOC",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    placeholder = painterResource(id = R.drawable.user_filled),
                    error = painterResource(id = R.drawable.user_filled) // Fallback if loading fails
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

    val callsDoneRatio = getCallStats(staff.onCallDates)
    val leaveStatus = getLeaveStatus(staff.leaveDates)

    Row(verticalAlignment = Alignment.CenterVertically) {
        StaffAvatarItem(staff, true)
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row {
                Text(text = staff.firstName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.padding(2.dp))
                Text(text = staff.lastName, style = MaterialTheme.typography.titleMedium)
            }
            // Text(text = "Role: ${getRoleName(staff.role)}")
            Text(text = getUnitName(staff.unit))
            // Text(text = callsDoneRatio, style = MaterialTheme.typography.bodyMedium)
            // Text(text = leaveStatus, style = MaterialTheme.typography.bodyMedium)
            Text(text = staff.phone, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun QuarterCalendarColumn(
    selectedQuarter: Int,
    selectedWeek: Int,
    onQuarterSelected: (Int) -> Unit,
    onWeekSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentYear = getCurrentYear()
    val years = (0 until 5).map { currentYear + it } // Generate a list of 5 years

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
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

            // Add 4 quarters for this year
            items(4) { quarter ->
                QuarterItem(
                    quarterNumber = quarter + 1,
                    selectedQuarter = selectedQuarter,
                    selectedWeek = selectedWeek,
                    year = year,
                    onQuarterSelected = { onQuarterSelected(it) },
                    onWeekSelected = { onWeekSelected(it) }
                )
            }
        }
    }
}

@Composable
fun QuarterItem(
    quarterNumber: Int,
    selectedQuarter: Int,
    selectedWeek: Int,
    year: Int,
    onQuarterSelected: (Int) -> Unit,
    onWeekSelected: (Int) -> Unit
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            repeat(3) { month ->
                MonthColumn(
                    monthNumber = (quarterNumber - 1) * 3 + month + 1,
                    year = year,
                    quarterNumber = quarterNumber,
                    selectedWeek = selectedWeek,
                    onWeekSelected = onWeekSelected
                )
            }
        }
    }
}

@Composable
fun MonthColumn(
    monthNumber: Int,
    year: Int,
    quarterNumber: Int,
    selectedWeek: Int,
    onWeekSelected: (Int) -> Unit
) {
    val monthIndexInQuarter = (monthNumber - 1) % 3
    val monthAbbreviation = getMonthAbbreviation(quarterNumber, monthIndexInQuarter)

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
            val isSelected = weekNumber == selectedWeek
            val dateRange = getWeekDateRange(year = year, weekNumber = weekNumber)
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onWeekSelected(weekNumber) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color.LightGray else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = if (isSelected) BorderStroke(2.dp, Color.Blue) else null
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
                            color = if (isSelected) Color.Blue else Color.Black
                        )
                        Text(
                            text = dateRange,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) Color.Blue else Color.Gray,
                            textAlign = TextAlign.Center
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
    modifier: Modifier = Modifier
) {
    val monthAbbreviation =
        getMonthAbbreviation((selectedMonth - 1) / 3 + 1, (selectedMonth - 1) % 3)

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
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
        repeat(4) { week ->
            val weekNumber = (selectedMonth - 1) * 4 + week + 1
            val isSelected = weekNumber == selectedWeek
            val dateRange = getWeekDateRange(year = year, weekNumber = weekNumber)

            Card(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clickable { onWeekSelected(weekNumber) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color.LightGray else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = if (isSelected) BorderStroke(2.dp, Color.Blue) else null
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Week ${week + 1}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) Color.Blue else Color.Black
                        )
                        Text(
                            text = dateRange,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) Color.Blue else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoster() {
    MainScreen(Modifier)
}






