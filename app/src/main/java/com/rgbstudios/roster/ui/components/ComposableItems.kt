package com.rgbstudios.roster.ui.components

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import com.rgbstudios.roster.R
import com.rgbstudios.roster.data.cache.DataStoreManager
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.data.model.Suggestion
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.navigation.Screen
import com.rgbstudios.roster.utils.ConfirmClearRemindersDialog
import com.rgbstudios.roster.utils.ConfirmDeleteDialog
import com.rgbstudios.roster.utils.EditStaffDialog
import com.rgbstudios.roster.utils.InfoDialog
import com.rgbstudios.roster.utils.ResourceItem
import com.rgbstudios.roster.utils.StaffDetailDialog
import com.rgbstudios.roster.utils.StaffListItemMenuDialog
import com.rgbstudios.roster.utils.SuggestionDeleteDialog
import com.rgbstudios.roster.utils.SuggestionResolveDialog
import com.rgbstudios.roster.utils.calculateMonthProgress
import com.rgbstudios.roster.utils.getCallStatus
import com.rgbstudios.roster.utils.getCurrentMonth
import com.rgbstudios.roster.utils.getCurrentYear
import com.rgbstudios.roster.utils.getLeaveStatus
import com.rgbstudios.roster.utils.getLicenseText
import com.rgbstudios.roster.utils.getMonthInfo
import com.rgbstudios.roster.utils.getTermsAndPrivacyText
import com.rgbstudios.roster.utils.getUnitName
import com.rgbstudios.roster.utils.getWeekDateRange
import com.rgbstudios.roster.utils.showShortToast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditModeBar(onCloseClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.align(Alignment.Center)) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Edit Mode Enabled",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        IconButton(
            onClick = { onCloseClick() },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Disable Edit Mode"
            )
        }
    }
}

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
    val context = LocalContext.current

    // Create an ImageLoader with disk caching enabled
    val imageLoader = ImageLoader(context).newBuilder()
        .diskCachePolicy(CachePolicy.ENABLED) // Enable disk caching
        .memoryCachePolicy(CachePolicy.ENABLED) // Enable memory caching
        .build()

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
                    model = staff.imageUrl,
                    imageLoader = imageLoader,
                    placeholder = painterResource(id = R.drawable.ic_user),
                    error = painterResource(id = R.drawable.ic_user)
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
    val context = LocalContext.current
    var showMenuDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.clickable { showMenuDialog = true },
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

    // Menu Dialog
    if (showMenuDialog) {
        StaffListItemMenuDialog(
            context = context,
            staff = staff,
            isSignedIn = false,
            isInEditMode = false,
            onMenuDismissRequest = { showMenuDialog = false },
            onEditClicked = {},
            onDeleteClicked = {}
        )
    }
}

@Composable
fun StaffListDetailRow(
    staff: StaffMember,
    isSignedIn: Boolean,
    isInEditMode: Boolean,
    rosterViewModel: RosterViewModel
) {

    val context = LocalContext.current
    var showMenuDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    val isOnLeave = getLeaveStatus(staff)
    val isOnCall = getCallStatus(staff)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showMenuDialog = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        StaffAvatarItem(staff, true)

        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = "${staff.lastName} ${staff.firstName}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(text = getUnitName(staff.unit))

            if (isOnLeave) {
                Text(
                    text = "On Leave!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (isOnCall) {
                Text(
                    text = "On Call",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(text = staff.phone, style = MaterialTheme.typography.titleMedium)
        }
    }

    // Menu Dialog
    if (showMenuDialog) {
        StaffListItemMenuDialog(
            context = context,
            staff = staff,
            isSignedIn = isSignedIn,
            isInEditMode = isInEditMode,
            onMenuDismissRequest = { showMenuDialog = false },
            onEditClicked = {
                showEditDialog = true
                showMenuDialog = false
            },
            onDeleteClicked = {
                showConfirmDeleteDialog = true
                showMenuDialog = false
            }
        )
    }

    // Edit Staff Dialog
    if (showEditDialog) {
        EditStaffDialog(staff, rosterViewModel) {
            showEditDialog = false
        }
    }

    // Confirm Delete Dialog
    if (showConfirmDeleteDialog) {
        ConfirmDeleteDialog(
            staff = staff,
            rosterViewModel = rosterViewModel,
            onDismissRequest = { showConfirmDeleteDialog = false }
        )
    }
}

@Composable
fun QuarterCalendarColumn(
    selectedWeek: Pair<Int, Int>,
    onWeekSelected: (Int, Int, Boolean) -> Unit,
    isInEditMode: Boolean,
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
                    isInEditMode = isInEditMode
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
    onWeekSelected: (Int, Int, Boolean) -> Unit,
    isInEditMode: Boolean
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
                    selectedWeek = selectedWeek,
                    onWeekSelected = onWeekSelected,
                    isInEditMode = isInEditMode
                )
            }
        }
    }
}

@Composable
fun MonthColumnItem(
    monthNumber: Int,
    year: Int,
    selectedWeek: Pair<Int, Int>,
    onWeekSelected: (Int, Int, Boolean) -> Unit,
    isInEditMode: Boolean
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
                    .clickable { onWeekSelected(year, weekNumber, isInEditMode) },
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
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified
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
    modifier: Modifier = Modifier
) {
    val monthAbbreviation = getMonthInfo(selectedMonth).first

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
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
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified
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
    isInEditMode: Boolean,
    isLeftExpanded: Boolean,
    onMonthSelected: (Int, Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val years = (2024..(getCurrentYear() + 1)).toList()
    val currentYear = getCurrentYear()
    val currentMonthIndex = getCurrentMonth() - 1

    // LazyListState for managing scrolling
    val listState = rememberLazyListState()

    // Track whether the initial scroll has been performed
    var hasScrolledInitially by remember { mutableStateOf(false) }

    // Perform the initial scroll when the screen loads
    LaunchedEffect(selectedYear, selectedMonth) {
        if (!hasScrolledInitially) {
            val yearIndex = years.indexOf(selectedYear)
            if (yearIndex != -1) {
                val scrollIndex = yearIndex * 13 + selectedMonth - 2
                listState.scrollToItem(scrollIndex)
            }
            hasScrolledInitially = true
        }

    }

    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp),
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
                    year < currentYear || (year == currentYear && monthIndex < currentMonthIndex) -> 1f
                    year > currentYear || monthIndex > currentMonthIndex -> 0f
                    else -> calculateMonthProgress(currentYear, currentMonthIndex)
                }

                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { onMonthSelected(year, monthIndex + 1, isInEditMode) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = if (isSelected) BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.primary
                    ) else null
                ) {
                    Row {
                        // Show Edit Icon when in Edit Mode
                        if (isInEditMode) {
                            Icon(
                                painter = painterResource(R.drawable.ic_back),
                                contentDescription = "Edit Month",
                                tint = Color.LightGray,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(8.dp)
                                    .size(16.dp)
                            )

                        }

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
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallTypeSegmentedButton(
    onSelectionChanged: (String) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Ward Call", "Gym Call")

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    selectedIndex = index
                    onSelectionChanged(label)
                },
                selected = index == selectedIndex,
                label = { Text(label) }
            )
        }
    }
}

// -------- Resource Screen Composable --------
@Composable
fun MainResourceScreen(isSignedIn: Boolean, onItemClick: (ResourceItem) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(
            ResourceItem.Organogram,
            ResourceItem.Clerking,
            ResourceItem.Notifications,
            ResourceItem.Suggestions
        ).forEach { item ->
            item {
                ResourceRow(item, onItemClick)
            }
        }

        // login/logout items
        item {
            ResourceRow(
                if (isSignedIn) ResourceItem.Logout else ResourceItem.Login,
                onItemClick
            )
        }

        // About item
        item { ResourceRow(ResourceItem.About, onItemClick) }
    }
}

@Composable
fun ResourceRow(item: ResourceItem, onItemClick: (ResourceItem) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }
    ) {
        Icon(
            painter = painterResource(
                id = item.iconRes
            ),
            contentDescription = item.name,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            item.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun DetailScreen(title: String, rosterViewModel: RosterViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 8.dp)
        ) {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier.clickable { onBack() }
            )
        }

        when (title) {

            ResourceItem.Organogram.name -> {
                OrganogramScreen()
            }

            ResourceItem.Clerking.name -> {
                ClerkingScreen()
            }

            ResourceItem.Notifications.name -> {
                NotificationsScreen(rosterViewModel)
            }

            ResourceItem.Suggestions.name -> {
                SuggestionsScreen(rosterViewModel)
            }

            ResourceItem.About.name -> {
                AboutScreen()
            }
        }
    }
}

// ----------------------------

@Composable
fun OrganogramScreen() {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val maxScale = 8f
    val minScale = 1f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Apply zoom within limits
                    val newScale = (scale * zoom).coerceIn(minScale, maxScale)

                    // Calculate max pan limits based on scale
                    val maxPanX = (newScale - 1f) * 700f
                    val maxPanY = (newScale - 1f) * 700f

                    // Apply pan within limits
                    offsetX = (offsetX + pan.x).coerceIn(-maxPanX, maxPanX)
                    offsetY = (offsetY + pan.y).coerceIn(-maxPanY, maxPanY)

                    scale = newScale
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.organogram),
            contentDescription = "Organogram",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
        )
    }
}

// ----------------------------

@Composable
fun ClerkingScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var clinicalNotes by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        clinicalNotes = TextFieldValue(DataStoreManager.loadNotes(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        var isSystemicExpanded by remember { mutableStateOf(false) }
        var isSegmentalExpanded by remember { mutableStateOf(false) }
        var isPediatricExpanded by remember { mutableStateOf(false) }
        val clipboardManager = LocalClipboardManager.current

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isSystemicExpanded = !isSystemicExpanded }
        ) {
            Text(text = "Systemic Clerking System", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { isSystemicExpanded = !isSystemicExpanded }) {
                Icon(
                    painter = painterResource(
                        id = if (isSystemicExpanded) R.drawable.ic_up else R.drawable.ic_down
                    ),
                    contentDescription = "Systemic clerking expand toggle",
                    modifier = Modifier.padding(8.dp),
                    tint = Color.LightGray,
                )
            }
        }
        if (isSystemicExpanded) {
            SystemicCG()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isSegmentalExpanded = !isSegmentalExpanded }
        ) {
            Text(text = "Segmental Clerking System", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { isSegmentalExpanded = !isSegmentalExpanded }) {
                Icon(
                    painter = painterResource(
                        id = if (isSegmentalExpanded) R.drawable.ic_up else R.drawable.ic_down
                    ),
                    contentDescription = "Segmental clerking expand toggle",
                    modifier = Modifier.padding(8.dp),
                    tint = Color.LightGray,
                )
            }
        }
        if (isSegmentalExpanded) {
            SegmentalCG()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isPediatricExpanded = !isPediatricExpanded }
        ) {
            Text(text = "Pediatrics Clerking System", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { isPediatricExpanded = !isPediatricExpanded }) {
                Icon(
                    painter = painterResource(
                        id = if (isPediatricExpanded) R.drawable.ic_up else R.drawable.ic_down
                    ),
                    contentDescription = "Pediatrics clerking expand toggle",
                    modifier = Modifier.padding(8.dp),
                    tint = Color.LightGray,
                )
            }
        }
        if (isPediatricExpanded) {
            PediatricsCG()
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )

        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(text = "Quick Clinic Notes:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Clear",
                color = Color.Gray,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable {
                    if (clinicalNotes.text.isNotBlank()) {
                        scope.launch {
                            DataStoreManager.saveNotes(context, "")
                            clinicalNotes = TextFieldValue("")
                            showShortToast(context, "Notes content cleared!")
                        }
                    }
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(Color.LightGray.copy(alpha = 0.2f))
                .padding(8.dp)
        ) {
            BasicTextField(
                value = clinicalNotes,
                onValueChange = {
                    clinicalNotes = it
                    scope.launch { DataStoreManager.saveNotes(context, it.text) }
                },
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(clinicalNotes.text))
                    showShortToast(context, "Notes content copied!")
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_copy),
                    contentDescription = "Copy Notes",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun GuideSection(
    title: String,
    items: List<String>,
    subSections: List<Pair<String, List<String>>> = emptyList()
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // List main items with a bullet indicator (you can customize the bullet)
        items.forEach { item ->
            Row(verticalAlignment = Alignment.Top) {
                Text("👉 ", style = MaterialTheme.typography.bodyLarge)
                Text(text = item, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // If you have sub-sections (like detailed notes), display them here
        subSections.forEach { (subTitle, subItems) ->
            Text(text = subTitle, style = MaterialTheme.typography.titleSmall)
            subItems.forEach { subItem ->
                Row(verticalAlignment = Alignment.Top) {
                    Text("   - ", style = MaterialTheme.typography.bodyLarge)
                    Text(text = subItem, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SystemicCG() {
    // Patients' Biodata Section
    GuideSection(
        title = "Patients' Biodata",
        items = listOf(
            "Name",
            "Age",
            "Gender",
            "Occupation",
            "Address"
        )
    )

    // Presenting Complaint & History Section
    GuideSection(
        title = "Presenting Complaint (PC)",
        items = listOf("Describe the complaint, onset, duration, and intensity")
    )

    GuideSection(
        title = "History Source",
        items = listOf("Where/Whom the history is gotten from")
    )

    GuideSection(
        title = "History of Presenting Complaint",
        items = listOf(
            "Progression of the Condition",
            "Date of Onset of Signs & Symptoms",
            "Medical Management",
            "Medical Observations",
            "Other management",
            "Previous Therapy",
            "Results of Specific Investigations (X-rays, CT Scans, Blood Tests.....etc",
        )
    )

    GuideSection(
        title = "History continued",
        items = listOf(
            "Medical Hx",
            "Surgical Hx",
            "Drug Hx",
            "O&G Hx",
            "ImmHx",
            "DevHx",
            "NutritionalHx",
            "FSHx",
        )
    )

    // Examination Section
    GuideSection(
        title = "Objective Examination",
        items = listOf(
            "O/E (General observation)",
            "Vitals"
        )
    )

    GuideSection(
        title = "CNS",
        items = listOf(
            "Level of consciousness",
            "Orientation (Person, Place & Time)",
            "Communication: Aphasia (Broca's, Wernicke's, Global)",
            "Perception: Body Scheme/ Body Imaging, Neglect, Agnosia, Apraxia",
            "Sensation: Superficial, Deep, Cortical",
            "Tone: Decreased, Flaccid, Increased (Spasticity (Clasp-knife/Rigidity (Cogwheel or Lead Pipe)",
            "Reflexes: Deep Tendon Reflexes, Biceps (C5/6), Triceps (C7/8), Knee (L3/4), Ankle (S1/2), Plantar Response",
            "Balance & Coordination"
        )
    )

    GuideSection(
        title = "CVS",
        items = listOf(
            "Blood Pressure",
            "Pulse Rate",
            "Temperature",
            "PCV",
        )
    )

    GuideSection(
        title = "Respiratory System",
        items = listOf(
            "Breath Sounds",
            "chest expansion",
            "IE ratio",
            "Respiratory Rate",
            "Chest wall/Thoracic spine deformity",
            "Oxygen Saturation",
        )
    )

    GuideSection(
        title = "MSK System",
        items = listOf(
            "Muscle bulk: Atrophied, Preserved, Hypertrophied",
            "Range of Motion: AROM, PROM",
            "Patella Mobility",
            "TA Tightness",
            "GMP"
        )
    )

    GuideSection(
        title = "Integumentary system",
        items = listOf(
            "Skin Status: oedema, discoloration, bruising",
            "Pressure Sores"
        )
    )

    GuideSection(
        title = "Bowel and bladder function",
        items = listOf(
            "Urinary Incontinence",
            "Fecal Incontinence"
        )
    )

    // Functional Assessment
    GuideSection(
        title = "Functional Assessment",
        items = listOf(
            "Side Turning/Rolling",
            "Sitting",
            "Standing",
            "Walking",
            "ADLs"
        )
    )

    // Investigations Section
    GuideSection(
        title = "Investigations",
        items = listOf(
            "Laboratory studies",
            "Radiological investigations"
        )
    )

    // Analysis of Findings
    GuideSection(
        title = "Analysis of Findings",
        items = listOf(
            "Motor functions",
            "Sensory Impairment",
            "Activity Limitations",
            "Participation Restrictions"
        )
    )

    // Plan of Treatment Section with Subsection Details
    GuideSection(
        title = "Plan of Treatment",
        items = listOf(),
        subSections = listOf(
            "Goals" to listOf("Short-term and long-term objectives"),
            "Means" to listOf("Treatment modalities, exercises, manual therapy, etc.")
        )
    )
}

@Composable
fun SegmentalCG() {
    // Patients' Biodata Section
    GuideSection(
        title = "Patients' Biodata",
        items = listOf(
            "Name",
            "Age",
            "Gender",
            "Occupation",
            "Address"
        )
    )

    // Presenting Complaint & History Section
    GuideSection(
        title = "Presenting Complaint (PC)",
        items = listOf("Describe the complaint, onset, duration, and intensity")
    )

    GuideSection(
        title = "History Source",
        items = listOf("Where/Whom the history is gotten from")
    )

    GuideSection(
        title = "History of Presenting Complaint",
        items = listOf(
            "Progression of the Condition",
            "Date of Onset of Signs & Symptoms",
            " Area of Pain",
            " Severity of Pain",
            " Duration of Pain",
            " Aggravating Factors of Pain",
            " Easing Factors of Pain",
            " Time Bound Factors of Pain - 24hour Pattern",
            " Nature of Pain",
            " Irritability of Pain:",
            "Medical Management",
            "Other management received",
            "Previous Therapy",
            "Results of Specific Investigations (X-rays, CT Scans, Blood Tests.....etc",
        )
    )

    GuideSection(
        title = "History continued",
        items = listOf(
            "Medical Hx",
            "Surgical Hx",
            "Drug Hx",
            "O&G Hx",
            "ImmHx",
            "DevHx",
            "NutritionalHx",
            "FSHx",
        )
    )

    // Examination Section
    GuideSection(
        title = "Physical Examination",
        items = listOf(
            "O/E (General observation)",
            "Vitals"
        )
    )

    // Segmental Examination Section
    GuideSection(
        title = "Objective Examination",
        items = listOf(
            "Head/Neck (H/N)",
            "Thorax & Abdomen (T&A)",
            "Upper Limbs (ULs)",
            "Lower Limbs (LLs)"
        )
    )

    // Functional Assessment
    GuideSection(
        title = "Functional Assessment",
        items = listOf(
            "Neck control",
            "Rolling",
            "Sitting",
            "Standing",
            "Walking",
            "ADLs"
        )
    )

    // Investigations Section
    GuideSection(
        title = "Investigations",
        items = listOf(
            "Laboratory studies",
            "Radiological investigations"
        )
    )

    // Analysis of Findings
    GuideSection(
        title = "Analysis of Findings",
        items = listOf(
            "Motor functions",
            "Sensory Impairment",
            "Activity Limitations",
            "Participation Restrictions"
        )
    )

    // Plan of Treatment Section with Subsection Details
    GuideSection(
        title = "Plan of Treatment",
        items = listOf(),
        subSections = listOf(
            "Goals" to listOf("Short-term and long-term objectives"),
            "Means" to listOf("Treatment modalities, exercises, manual therapy, etc.")
        )
    )
}

@Composable
fun PediatricsCG() {
    // Patients' Biodata Section
    GuideSection(
        title = "Patients' Biodata",
        items = listOf(
            "Name",
            "Age",
            "Gender",
            "Handedness"
        )
    )

    // Presenting Complaint & History Section
    GuideSection(
        title = "Presenting Complaint (PC)",
        items = listOf("Describe the complaint, onset, duration, and intensity")
    )

    GuideSection(
        title = "History Source",
        items = listOf("Where/Whom the history is gotten from")
    )

    GuideSection(
        title = "History of Presenting Complaint",
        items = listOf(
            "Birth history",
            "Full term or preterm",
            "Place of birth",
            "Mode of delivery (SVD, CS, etc.)",
            "Mechanism of injury",
            "Signs following injury",
            "Previous interventions"
        )
    )

    GuideSection(
        title = "History continued",
        items = listOf(
            "Medical Hx",
            "Surgical Hx",
            "Drug Hx",
            "O&G Hx",
            "ImmHx",
            "DevHx",
            "NutritionalHx",
            "FSHx",
        )
    )

    // Examination Section
    GuideSection(
        title = "Physical Examination",
        items = listOf(
            "O/E (General observation)",
            "Vitals"
        )
    )

    // Segmental Examination Section
    GuideSection(
        title = "Objective Examination",
        items = listOf(
            "Head/Neck (H/N)",
            "Thorax & Abdomen (T&A)",
            "Upper Limbs (ULs)",
            "Lower Limbs (LLs)"
        )
    )

    // Functional Assessment
    GuideSection(
        title = "Functional Assessment",
        items = listOf(
            "Neck control",
            "Rolling",
            "Sitting",
            "Standing",
            "Walking",
            "ADLs"
        )
    )

    // Investigations Section
    GuideSection(
        title = "Investigations",
        items = listOf(
            "Laboratory studies",
            "Radiological investigations"
        )
    )

    // Analysis of Findings
    GuideSection(
        title = "Analysis of Findings",
        items = listOf(
            "Motor functions",
            "Sensory Impairment",
            "Activity Limitations",
            "Participation Restrictions"
        )
    )

    // Plan of Treatment Section with Subsection Details
    GuideSection(
        title = "Plan of Treatment",
        items = listOf(),
        subSections = listOf(
            "Goals" to listOf("Short-term and long-term objectives"),
            "Means" to listOf("Treatment modalities, exercises, manual therapy, etc.")
        )
    )
}

// ----------------------------

@Composable
fun NotificationsScreen(rosterViewModel: RosterViewModel) {
    val context = LocalContext.current

    var hasPermission by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isActiveRemindersExpanded by remember { mutableStateOf(false) }
    var showConfirmClearRemindersDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            hasPermission = true // No need for permission on older versions
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true
        } else {
            hasPermission = false
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.POST_NOTIFICATIONS)) {
                // User denied permission but not permanently, request again
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Permission permanently denied, show a dialog to take them to settings
                showSettingsDialog(context)
            }
        }
    }

    LaunchedEffect(hasPermission) {
        if (!hasPermission) {
            showShortToast(context, "Grant notification permission to continue")
        }
    }

    val staffList by rosterViewModel.staffList.collectAsState()
    var selectedStaff by remember { mutableStateOf<StaffMember?>(null) }
    val reminders by DataStoreManager.remindersFlow.collectAsState()

    LaunchedEffect(Unit) { coroutineScope.launch { DataStoreManager.loadReminders(context) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {

        Text(
            "Set Up Notifications",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Center
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clickable { isActiveRemindersExpanded = !isActiveRemindersExpanded }
        ) {
            Text("Active Reminders", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { isActiveRemindersExpanded = !isActiveRemindersExpanded }) {
                Icon(
                    painter = painterResource(
                        id = if (isActiveRemindersExpanded) R.drawable.ic_up else R.drawable.ic_down
                    ),
                    contentDescription = "Systemic clerking expand toggle",
                    modifier = Modifier.padding(8.dp),
                    tint = Color.Gray,
                )
            }
        }

        if (isActiveRemindersExpanded) {
            // Display existing reminders
            if (reminders.isNotEmpty()) {
                Row{
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Cancel All",
                        color = Color.Gray,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                showConfirmClearRemindersDialog = true
                            }
                    )
                }

                LazyColumn(
                    modifier = Modifier.padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(reminders) { reminder ->
                        ReminderItem(
                            title = reminder.title,
                            message = reminder.message,
                            onCancel = {
                                coroutineScope.launch {
                                   WorkManager.getInstance(context).cancelAllWorkByTag("reminder_${reminder.staffId}")

                                    DataStoreManager.removeReminder(context, reminder)
                                }
                            }
                        )
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {Text("No reminders set yet.",fontWeight = FontWeight.Bold)}
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(staffList.sortedBy { it.lastName }) { staff ->
                NotificationsStaffItem(staff) { selectedStaff = staff }
            }
        }
    }

    selectedStaff?.let { staff ->
        StaffDetailDialog(staff, rosterViewModel, onDismiss = { selectedStaff = null })
    }

    if (showConfirmClearRemindersDialog) {
        ConfirmClearRemindersDialog(
            onDismissRequest = { showConfirmClearRemindersDialog = false }
        )
    }
}

fun showSettingsDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Enable Notifications")
        .setMessage("Notifications are disabled. Please enable them in settings to receive reminders.")
        .setPositiveButton("Open Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
        .setNegativeButton("Cancel", null)
        .show()
}


@Composable
fun NotificationsStaffItem(staff: StaffMember, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        StaffAvatarItem(staff, true)

        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = "${staff.lastName} ${staff.firstName}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = getUnitName(staff.unit))
        }

    }
}

@Composable
fun ReminderItem(title: String, message: String, onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(
            onClick = onCancel,
            modifier = Modifier.size(24.dp) // Ensure a fixed size for the icon
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel Reminder",
                tint = Color.Red
            )
        }
    }
}

// ----------------------------

@Composable
fun SuggestionsScreen(rosterViewModel: RosterViewModel) {
    val context = LocalContext.current
    val isSignedIn by rosterViewModel.adminSignedIn.collectAsState()
    val suggestions by rosterViewModel.suggestions.collectAsState()

    var showResolveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedSuggestion by remember { mutableStateOf<Suggestion?>(null) }
    var suggestionAuthor by remember { mutableStateOf("") }
    var suggestionText by remember { mutableStateOf("") }
    val loadingState = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isSignedIn) {

            if (suggestions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No suggestions to display")
                }
            } else {
                LazyColumn {
                    items(suggestions) { suggestion ->
                        SuggestionItem(suggestion) {
                            selectedSuggestion = suggestion
                            if (suggestion.resolved) showDeleteDialog =
                                true else showResolveDialog = true
                        }
                    }
                }
            }
        } else {

            Text(
                text = "Send your suggestions, comments and opinions",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = suggestionAuthor,
                onValueChange = { suggestionAuthor = it },
                label = { Text("Name (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = suggestionText,
                onValueChange = { suggestionText = it },
                label = { Text("Enter your suggestion") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6
            )
            Button(
                onClick = {
                    if (suggestionText.isNotBlank()) {
                        loadingState.value = true

                        val suggestionObject = Suggestion(text = suggestionText)
                        val newSuggestion =
                            if (suggestionAuthor.isNotBlank()) suggestionObject.copy(author = suggestionAuthor) else suggestionObject
                        rosterViewModel.addSuggestion(newSuggestion) { success, _ ->
                            loadingState.value = false
                            if (success) {
                                suggestionAuthor = ""
                                suggestionText = ""
                                showShortToast(context, "Suggestion Sent Successfully!")
                            } else {
                                showShortToast(context, "Something went wrong, try again later!")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                enabled = !loadingState.value
            ) {
                if (loadingState.value) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Submit Suggestion")
                }
            }
        }
    }

    if (showResolveDialog && selectedSuggestion != null) {
        SuggestionResolveDialog(rosterViewModel,
            selectedSuggestion!!, onDismissRequest = { showResolveDialog = false })
    }

    if (showDeleteDialog && selectedSuggestion != null) {
        SuggestionDeleteDialog(rosterViewModel,
            selectedSuggestion!!, onDismissRequest = { showDeleteDialog = false })
    }
}

@Composable
fun SuggestionItem(suggestion: Suggestion, onClick: () -> Unit) {
    val formattedDate = remember {
        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(Date(suggestion.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
    ) {
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = "Sent on: $formattedDate",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "By: ${suggestion.author}",
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .align(Alignment.End),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = suggestion.text
                )
            }

            if (suggestion.resolved) {

                // Resolved Icon Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Edit Image",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ----------------------------

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(text = "The Roster App!", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        // App description
        Text(
            text = "Thank you for using the Roster App!\nThis app is designed for the Physiotherapy Department at Federal Medical Centre, Owo\nIt helps users manage and access the department's roster efficiently.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Features section
        Text(text = "Key Features:", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        val features = listOf(
            "\uD83D\uDD38 View the weekly on-call roster and contact staff via call, text, or WhatsApp.",
            "\uD83D\uDD38 Check the leave roster to see which staff members are on leave each month.",
            "\uD83D\uDD38 Browse a list of all department staff members.",
            "\uD83D\uDD38 Access the department's organogram and hierarchy.",
            "\uD83D\uDD38 Use the clerking guide for quick reference and clinical note-taking.",
            "\uD83D\uDD38 Set up notifications for important updates (e.g., call or leave reminders).",
            "\uD83D\uDD38 Submit and resolve suggestions to improve workflow.",
        )

        features.forEach { feature ->
            Text(
                text = feature,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legal Details
        Text(
            text = "Legal & Licensing:",
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "© ${getCurrentYear()} RGBStudios. All rights reserved.\n" +
                    "This app is provided as-is, without any warranties of any kind.",
            style = MaterialTheme.typography.bodySmall,
        )
        Row {
            Text(
                text = "By using this app, you agree to our ",
                style = MaterialTheme.typography.bodySmall,
            )
            ClickableTextItem("Terms & Privacy Policies.") { showDialog = "Terms" }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "This project is licensed under the MIT ",
                style = MaterialTheme.typography.bodySmall,
            )
            ClickableTextItem("LICENSE") { showDialog = "License" }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contact section
        Text(
            text = "Contact Information:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "For any inquiries, suggestions or support, please use the suggestion box or reach out to the Developer:",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(4.dp))

        ContactInfo()

        Spacer(modifier = Modifier.height(24.dp))

        // Signature
        Text(
            text = "With ❤️ from RGBStudios 🎨",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    // show Dialog
    showDialog?.let { dialogType ->
        InfoDialog(
            title = dialogType,
            content = when (dialogType) {
                "License" -> getLicenseText(context)
                "Terms" -> getTermsAndPrivacyText(context)
                else -> ""
            },
            onDismiss = { showDialog = null }
        )
    }
}

@Composable
fun ClickableTextItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clickable { onClick() },
        style = MaterialTheme.typography.bodySmall,
        fontStyle = FontStyle.Italic
    )
}

// Contact Information Component
@Composable
fun ContactInfo() {
    Column {
        ContactItem(
            R.drawable.ic_mail,
            "rgb.mobile.studios@gmail.com",
            "mailto:rgb.mobile.studios@gmail.com"
        )
        ContactItem(
            R.drawable.ic_linkedin,
            "Oladayo Babalola (LinkedIn)",
            "https://linkedin.com/in/oladayo-babalola-spt/"
        )
        ContactItem(R.drawable.ic_github, "GitHub Profile", "https://github.com/bodadayo/")
    }
}

// Contact Item (with Clickable Links)
@Composable
fun ContactItem(resourceId: Int, text: String, url: String) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            painterResource(id = resourceId),
            contentDescription = text,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            },
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic
        )
    }
}


