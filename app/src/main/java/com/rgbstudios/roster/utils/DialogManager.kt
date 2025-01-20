package com.rgbstudios.roster.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.rgbstudios.roster.R
import com.rgbstudios.roster.ui.destinations.CallRosterScreenDestination
import com.rgbstudios.roster.ui.destinations.LeaveRosterScreenDestination
import com.rgbstudios.roster.ui.destinations.NotificationsScreenDestination
import com.rgbstudios.roster.ui.destinations.StaffListScreenDestination

@Composable
fun NavigationDialog(
    navigator: DestinationsNavigator,
    onDismissRequest: () -> Unit,
    currentScreen: String,
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
        Dialog(onDismissRequest = onDismissRequest) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Menu",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleMedium
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Main Screen Icon
                        item {
                            NavigationGridItem(
                                icon = painterResource(id = R.drawable.ic_user_plus),
                                label = "Call Roster",
                                isDisabled = currentScreen == "CallRosterScreen",
                                onClick = {
                                    onDismissRequest()
                                    navigator.navigate(CallRosterScreenDestination)
                                }
                            )
                        }

                        // Leave Roster Screen Icon
                        item {
                            NavigationGridItem(
                                icon = painterResource(id = R.drawable.ic_beach),
                                label = "Leave Roster",
                                isDisabled = currentScreen == "LeaveRosterScreen",
                                onClick = {
                                    onDismissRequest()
                                    navigator.navigate(LeaveRosterScreenDestination)
                                }
                            )
                        }

                        // Staff List Screen Icon
                        item {
                            NavigationGridItem(
                                icon = painterResource(id = R.drawable.ic_people),
                                label = "Staff List",
                                isDisabled = currentScreen == "StaffListScreen",
                                onClick = {
                                    onDismissRequest()
                                    navigator.navigate(StaffListScreenDestination)
                                }
                            )
                        }

                        // Placeholder or additional screen
                        item {
                            NavigationGridItem(
                                icon = painterResource(id = R.drawable.ic_notification),
                                label = "Notifications",
                                isDisabled = currentScreen == "NotificationsScreen",
                                onClick = {
                                    navigator.navigate(NotificationsScreenDestination)
                                    onDismissRequest()
                                }
                            )
                        }
                    }

                    Text(
                        text = if (isLoggedIn) "Logout" else "Admin Login",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.End)
                            .clickable {
                                if (isLoggedIn) {
                                    onLogoutClick()
                                } else {
                                    onLoginClick()
                                }
                                onDismissRequest()
                            }
                    )
                }
            }
        }
}


@Composable
fun NavigationGridItem(
    icon: Painter,
    label: String,
    isDisabled: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .let {
                if (isDisabled) it else it.clickable { onClick() }
            }
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            modifier = Modifier.size(48.dp),
            tint = if (isDisabled) Color.Gray else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDisabled) Color.Gray else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun UserLoginDialog(
    onDismissRequest: () -> Unit
) {
    // States to store email, password, and error messages
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = "Admin Login",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                // Email Input Field
                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                // Password Input Field
                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )

                // Forgot Credentials Link
                Text(
                    text = "Forgot Credentials?",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp)
                        .clickable {
                            // Handle "Forgot Credentials" action
                        }
                )

                // Error Message Display
                if (errorMessage.value.isNotEmpty()) {
                    Text(
                        text = errorMessage.value,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Login Button
                Button(
                    onClick = {
                        if (validateCredentials(emailState.value, passwordState.value)) {
                            // Perform login action (e.g., API call)
                            onDismissRequest() // Close the dialog after successful login
                        } else {
                            errorMessage.value = "Invalid email or password"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                ) {
                    Text("Log In")
                }

            }
        }
    }
}

// Validation function for email and password
fun validateCredentials(email: String, password: String): Boolean {
    return email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.isNotBlank()
}

/**
@Preview(showBackground = true)
@Composable
fun PreviewNavigationDialog() {
    NavigationDialog(
        onDismissRequest = {  },
        currentScreen = "NotificationsScreen",
        isLoggedIn = true,
        onLoginClick = {  },
        onLogoutClick = {
        }
    )
}
 */
