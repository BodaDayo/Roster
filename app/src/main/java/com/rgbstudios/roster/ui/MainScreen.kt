package com.rgbstudios.roster.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.utils.getCallStats
import com.rgbstudios.roster.utils.getCurrentQuarter
import com.rgbstudios.roster.utils.getCurrentWeekOfYear
import com.rgbstudios.roster.utils.getLeaveStatus
import com.rgbstudios.roster.utils.getMonthAbbreviation
import com.rgbstudios.roster.utils.getRoleName
import com.rgbstudios.roster.utils.getUnitName

@Composable
fun MainScreen(staffList: List<StaffMember>) {
    // States
    var isLeftExpanded by remember { mutableStateOf(false) }
    var selectedQuarter by remember { mutableStateOf(getCurrentQuarter()) }
    var selectedWeek by remember { mutableStateOf(getCurrentWeekOfYear()) }

    // Layout
    Row(modifier = Modifier.fillMaxSize()) {
        // Left Column
        StaffOnCallColumn(
            staffList = staffList,
            isExpanded = isLeftExpanded,
            onExpandToggle = { isLeftExpanded = !isLeftExpanded },
            modifier = Modifier.weight(if (isLeftExpanded) 0.7f else 0.3f)

        )

        // Right Column
        if (!isLeftExpanded) {
            QuarterCalendarColumn(
                selectedQuarter = selectedQuarter,
                selectedWeek = selectedWeek,
                onQuarterSelected = { selectedQuarter = it },
                onWeekSelected = { selectedWeek = it },
                modifier = Modifier.weight(0.7f)
            )
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
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        Text(
            text = if (isExpanded) "Staff On Call (Expanded)" else "Staff On Call",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(staffList) { staff ->
                if (isExpanded) {
                    StaffDetailCard(staff)
                } else {
                    StaffAvatarItem(staff)
                }
            }
        }
        Button(
            onClick = onExpandToggle,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(if (isExpanded) "Collapse" else "Expand")
        }
    }
}

@Composable
fun StaffAvatarItem(staff: StaffMember) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Gray)
    ) {
        Image(
            painter = rememberAsyncImagePainter(staff.avatarUri),
            contentDescription = staff.name,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun StaffDetailCard(staff: StaffMember) {

    val callsDoneRatio = getCallStats(staff.onCallDates)
    val leaveStatus = getLeaveStatus(staff.leaveDates)
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        // elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = staff.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Role: ${getRoleName(staff.role)}")
            Text(text = "Unit: ${getUnitName(staff.unit)}")
            Text(text = callsDoneRatio, style = MaterialTheme.typography.bodyMedium)
            Text(text = leaveStatus, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Phone: ${staff.phone}")
        }
    }
}

@Composable
fun QuarterCalendarColumn(
    selectedQuarter: Int,
    selectedWeek: Int,
    onQuarterSelected: (Int) -> Unit,
    onWeekSelected: (Int) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        items(4) { quarter ->
            QuarterItem(
                quarterNumber = quarter + 1,
                selectedQuarter = selectedQuarter,
                selectedWeek = selectedWeek,
                onQuarterSelected = { onQuarterSelected(it) },
                onWeekSelected = { onWeekSelected(it) }
            )
        }
    }
}

@Composable
fun QuarterItem(
    quarterNumber: Int,
    selectedQuarter: Int,
    selectedWeek: Int,
    onQuarterSelected: (Int) -> Unit,
    onWeekSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Q$quarterNumber",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { month ->
                MonthColumn(
                    monthNumber = (quarterNumber - 1) * 3 + month + 1,
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
    quarterNumber: Int,
    selectedWeek: Int,
    onWeekSelected: (Int) -> Unit
) {
    val monthIndexInQuarter = (monthNumber - 1) % 3
    val monthAbbreviation = getMonthAbbreviation(quarterNumber, monthIndexInQuarter)

    Column {
        Text(text = monthAbbreviation, style = MaterialTheme.typography.bodyMedium)
        repeat(4) { week ->
            val weekNumber = (monthNumber - 1) * 4 + week + 1
            val isSelected = weekNumber == selectedWeek
            Text(
                text = "Week ${week + 1}",
                color = if (isSelected) Color.Blue else Color.Black,
                modifier = Modifier
                    .clickable { onWeekSelected(weekNumber) }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Preview()
@Composable
fun sAIPreview() {
    StaffAvatarItem(
        staff = StaffMember(
            id = 2,
            name = "Jane Smith",
            role = 2,
            unit = 1,
            avatarUri = "",
            onCallDates = listOf(1, 5, 6),
            leaveDates = listOf(7, 8, 9, 10),
            phone = "+2348176224324"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun sDCPreview() {
    StaffDetailCard(
        staff = StaffMember(
            id = 2,
            name = "Jane Smith",
            role = 2,
            unit = 1,
            avatarUri = "",
            onCallDates = listOf(1,5,6),
            leaveDates = listOf(7,8,9,10),
            phone = "+2348176224324"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun qCCPreview() {
    QuarterCalendarColumn(
        selectedQuarter = 3,
        selectedWeek = 3,
        onQuarterSelected = {},
        onWeekSelected = {},
        modifier = Modifier.padding(4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun qIPreview() {
    QuarterItem(
        quarterNumber = 4,
        selectedQuarter = 1,
        selectedWeek = 2,
        onQuarterSelected = {},
        onWeekSelected = { })
}

@Preview(showBackground = true)
@Composable
fun mPreview() {
    MonthColumn(
        monthNumber = 1, quarterNumber = 3, selectedWeek = 1, onWeekSelected = {})
}


@Preview(showBackground = true)
@Composable
fun PreviewRoster() {
    val sampleData = listOf(
        StaffMember(
            id = 1,
            name = "John Doe",
            role = 4,
            unit = 1,
            avatarUri = "",
            onCallDates = listOf(1,2,3,),
            leaveDates = listOf(4,5,6,7),
            phone = "123"
        ),
        StaffMember(
            id = 2,
            name = "Jane Smith",
            role = 2,
            unit = 1,
            avatarUri = "",
            onCallDates = listOf(1,3,8,),
            leaveDates = listOf(4,5,6,7),
            phone = "876"
        ),
        StaffMember(
            id = 1,
            name = "John Doe",
            role = 3,
            unit = 1,
            avatarUri = "",
            onCallDates = listOf(4,5,6,),
            leaveDates = listOf(6,7,8,9),
            phone = "123"
        ),
        StaffMember(
            id = 2,
            name = "Jane Smith",
            role = 1,
            unit = 1,
            avatarUri = "",
            onCallDates = listOf(1,2,3,),
            leaveDates = listOf(4,5,6,7),
            phone = "876"
        ),
        StaffMember(
            id = 1,
            name = "John Doe",
            role = 4,
            unit = 1,
            avatarUri = "",
            onCallDates = listOf(4,5,6,),
            leaveDates = listOf(6,7,8,9),
            phone = "123"
        ),
        StaffMember(
            id = 2,
            name = "Jane Smith",
            role = 3,
            unit = 1,
            avatarUri = "",
            onCallDates = listOf(1,2,3,),
            leaveDates = listOf(4,5,6,7),
            phone = "876"
        )
    )
    MainScreen(staffList = sampleData)
}






