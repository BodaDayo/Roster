package com.rgbstudios.roster.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.work.WorkManager
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import com.image.cropview.CropType
import com.image.cropview.ImageCrop
import com.rgbstudios.roster.R
import com.rgbstudios.roster.data.cache.DataStoreManager
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.data.model.Suggestion
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.ui.components.StaffAvatarItem
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun UserLoginDialog(
    rosterViewModel: RosterViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    var passwordVisible by remember { mutableStateOf(false) }
    val usernameState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val loadingState = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val showResetDialog = remember { mutableStateOf(false) }
    val resetUsername = remember { mutableStateOf("") }
    val resetEmail = remember { mutableStateOf("") }
    val resetError = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(modifier = Modifier.verticalScroll(scrollState)) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
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

                    // Username Input
                    OutlinedTextField(
                        value = usernameState.value,
                        onValueChange = { usernameState.value = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    // Password Input
                    OutlinedTextField(
                        value = passwordState.value,
                        onValueChange = { passwordState.value = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val text = if (passwordVisible) "Hide" else "Show"
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                    // Forgot Credentials Link
                    Text(
                        text = "Forgot Login Credentials?",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 8.dp)
                            .clickable {
                                resetUsername.value = ""
                                resetEmail.value = ""
                                resetError.value = ""
                                showResetDialog.value = true
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
                            if (validateCredentials(usernameState.value, passwordState.value)) {
                                loadingState.value = true

                                rosterViewModel.signInAdmin(
                                    username = usernameState.value,
                                    password = passwordState.value
                                ) { success, message ->
                                    loadingState.value = false

                                    if (success) {
                                        showLongToast(context, "Login Successful!")
                                        onDismissRequest()
                                    } else {
                                        errorMessage.value =
                                            message ?: "Something went wrong, try again later!"
                                    }
                                }
                            } else {
                                errorMessage.value = "Invalid username or password"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        enabled = !loadingState.value
                    ) {
                        if (loadingState.value) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Log In")
                        }
                    }
                }
            }
        }
    }

    if (showResetDialog.value) {
        PasswordResetDialog(
            resetUsername = resetUsername,
            resetEmail = resetEmail,
            resetError = resetError,
            onDismiss = { showResetDialog.value = false },
            onSendReset = { username, email ->
                loadingState.value = true
                rosterViewModel.sendPasswordReset(username, email) { success, message ->
                    loadingState.value = false
                    if (success) {
                        showLongToast(context, "Password reset email sent!")
                        showResetDialog.value = false
                    } else {
                        resetError.value = message ?: "Something went wrong."
                    }
                }
            }
        )
    }


}

@Composable
fun UserLogoutDialog(
    rosterViewModel: RosterViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var loadingState by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Confirm Logout",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Are you sure you want to log out?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Logout Button
                Button(
                    onClick = {
                        loadingState = true
                        rosterViewModel.signOutAdmin { success, errorMessage ->
                            loadingState = false
                            showLongToast(
                                context,
                                if (success) "Logout Successful!" else errorMessage
                                    ?: "Logout Failed"
                            )
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loadingState
                ) {
                    if (loadingState) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Log Out")
                    }
                }

                // Cancel Button
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun PasswordResetDialog(
    resetUsername: MutableState<String>,
    resetEmail: MutableState<String>,
    resetError: MutableState<String>,
    onDismiss: () -> Unit,
    onSendReset: (username: String, email: String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = resetUsername.value,
                    onValueChange = {
                        resetUsername.value = it
                        resetError.value = ""
                    },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = resetEmail.value,
                    onValueChange = {
                        resetEmail.value = it
                        resetError.value = ""
                    },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (resetError.value.isNotEmpty()) {
                    Text(
                        text = resetError.value,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Button(onClick = {
                        val username = resetUsername.value.trim()
                        val email = resetEmail.value.trim()
                        if (username.isEmpty() && email.isEmpty()) {
                            resetError.value = "Please enter username or email."
                            return@Button
                        }
                        onSendReset(username, email)
                    }) {
                        Text("Send Email")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffDialog(
    rosterViewModel: RosterViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<Int?>(null) }
    var selectedUnit by remember { mutableStateOf<Int?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // New state to hold the cropped bitmap
    var croppedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    // Flag to control showing the cropper
    var showCropper by remember { mutableStateOf(false) }

    val errorMessage = remember { mutableStateOf("") }
    val addStaffFailed = remember { mutableStateOf(false) }
    val loadingState = remember { mutableStateOf(false) }

    var roleExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    val roleOptions by remember { mutableStateOf(getRoleOptions()) }
    val unitOptions by remember { mutableStateOf(getUnitOptions()) }

    // Image Picker
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            showCropper = true
        }
    }

    // Load bitmap from the imageUri for cropping (if available)
    val cropBitmap: Bitmap? = imageUri?.let { uri ->
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_user_plus),
                            contentDescription = "User Icon",
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        Text(
                            text = "Add Staff",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Center)
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
                }

                // Image Selection
                item {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            if (croppedBitmap != null) {
                                Image(
                                    bitmap = croppedBitmap!!.asImageBitmap(),
                                    contentDescription = "Cropped Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Placeholder",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        // Edit Icon Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .background(MaterialTheme.colorScheme.background, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Image",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // First Name
                item {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Last Name
                item {
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Phone Number
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Read-only "+234" prefix
                        OutlinedTextField(
                            value = "+234",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .width(80.dp),
                            textStyle = LocalTextStyle.current.copy(color = Color.Gray),
                            singleLine = true,
                            label = { Text("") }
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // User input for phone number
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                    phoneNumber = it
                                }
                            },
                            label = { Text("Phone Number") },
                            placeholder = { Text("8012345678") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .weight(1f),
                            singleLine = true
                        )
                    }
                }

                // Role Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = roleExpanded,
                        onExpandedChange = { roleExpanded = !roleExpanded }
                    ) {
                        OutlinedTextField(
                            value = roleOptions[selectedRole] ?: "Select Role",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Role") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { roleExpanded = true }
                        )
                        ExposedDropdownMenu(
                            expanded = roleExpanded,
                            onDismissRequest = { roleExpanded = false }
                        ) {
                            roleOptions.forEach { (key, value) ->
                                DropdownMenuItem(
                                    text = { Text(value) },
                                    onClick = {
                                        selectedRole = key
                                        roleExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Unit Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = unitExpanded,
                        onExpandedChange = { unitExpanded = !unitExpanded }
                    ) {
                        OutlinedTextField(
                            value = unitOptions[selectedUnit] ?: "Select Unit",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { unitExpanded = true }
                        )
                        ExposedDropdownMenu(
                            expanded = unitExpanded,
                            onDismissRequest = { unitExpanded = false }
                        ) {
                            unitOptions.forEach { (key, value) ->
                                DropdownMenuItem(
                                    text = { Text(value) },
                                    onClick = {
                                        selectedUnit = key
                                        unitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Error Message Display
                if (errorMessage.value.isNotEmpty()) {
                    item {
                        Text(
                            text = errorMessage.value,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Add Staff Button
                item {
                    Button(
                        onClick = {
                            errorMessage.value = validateFields(
                                firstName,
                                lastName,
                                phoneNumber,
                                selectedRole,
                                selectedUnit
                            )

                            if (errorMessage.value.isEmpty()) {
                                val staff = StaffMember(
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim(),
                                    phone = "+234$phoneNumber",
                                    role = selectedRole!!,
                                    unit = selectedUnit!!
                                )

                                loadingState.value = true

                                rosterViewModel.addStaff(
                                    staff = staff,
                                    imageBitmap = croppedBitmap
                                ) { success, _ ->
                                    if (success) {
                                        onDismissRequest()
                                    } else {
                                        addStaffFailed.value = true
                                    }
                                    loadingState.value = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        enabled = !loadingState.value
                    ) {
                        if (loadingState.value) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Add Staff")
                        }
                    }
                }
            }
        }
    }

    if (showCropper && cropBitmap != null) {

        val aspectRatio = cropBitmap.width.toFloat() / cropBitmap.height.toFloat()

        val imageCrop = remember { ImageCrop(cropBitmap) }

        Dialog(onDismissRequest = { showCropper = false }) {
            Card(
                // Black container color
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    imageCrop.ImageCropView(
                        modifier = Modifier
                            .height(300.dp)
                            .aspectRatio(aspectRatio)
                            .clip(RectangleShape),
                        cropType = CropType.SQUARE
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                croppedBitmap = imageCrop.onCrop()
                                showCropper = false
                            }
                        ) {
                            Text("Crop Image")
                        }
                    }
                }
            }
        }
    }

    // Show Toast when add staff fails
    if (addStaffFailed.value) {
        LaunchedEffect(Unit) {
            showLongToast(context, "Failed to add staff, try again!")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStaffDialog(
    staff: StaffMember,
    rosterViewModel: RosterViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf(staff.firstName) }
    var lastName by remember { mutableStateOf(staff.lastName) }
    var phoneNumber by remember { mutableStateOf(staff.phone.removePrefix("+234")) }
    var selectedRole by remember { mutableStateOf<Int?>(staff.role) }
    var selectedUnit by remember { mutableStateOf<Int?>(staff.unit) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var croppedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showCropper by remember { mutableStateOf(false) }

    val errorMessage = remember { mutableStateOf("") }
    val editStaffFailed = remember { mutableStateOf<String?>(null) }
    val loadingState = remember { mutableStateOf(false) }

    var roleExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    val isChanged =
        remember(firstName, lastName, phoneNumber, selectedRole, selectedUnit, croppedBitmap) {
            firstName != staff.firstName ||
                    lastName != staff.lastName ||
                    phoneNumber != staff.phone.removePrefix("+234") ||
                    selectedRole != staff.role ||
                    selectedUnit != staff.unit ||
                    croppedBitmap != null
        }

    val roleOptions by remember { mutableStateOf(getRoleOptions()) }
    val unitOptions by remember { mutableStateOf(getUnitOptions()) }

    // Image Picker
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            showCropper = true
        }
    }

    // Create an ImageLoader with disk caching enabled
    val imageLoader = ImageLoader(context).newBuilder()
        .diskCachePolicy(CachePolicy.ENABLED) // Enable disk caching
        .memoryCachePolicy(CachePolicy.ENABLED) // Enable memory caching
        .build()

    // Load bitmap from the imageUri for cropping (if available)
    val cropBitmap: Bitmap? = imageUri?.let { uri ->
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit_user),
                            contentDescription = "User Icon",
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        Text(
                            text = "Edit Staff Data",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Center)
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
                }

                // Image Selection
                item {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            if (croppedBitmap != null) {
                                Image(
                                    bitmap = croppedBitmap!!.asImageBitmap(),
                                    contentDescription = "Cropped Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (staff.imageUrl.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = staff.imageUrl,
                                        imageLoader = imageLoader,
                                        placeholder = painterResource(id = R.drawable.ic_user),
                                        error = painterResource(id = R.drawable.ic_user)
                                    ),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Placeholder",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        // Edit Icon Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .background(Color.DarkGray, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Image",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // First Name
                item {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Last Name
                item {
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Phone Number
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Read-only "+234" prefix
                        OutlinedTextField(
                            value = "+234",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .width(80.dp),
                            textStyle = LocalTextStyle.current.copy(color = Color.Gray),
                            singleLine = true,
                            label = { Text("") }
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // User input for phone number
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                    phoneNumber = it
                                }
                            },
                            label = { Text("Phone Number") },
                            placeholder = { Text("8012345678") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .weight(1f),
                            singleLine = true
                        )
                    }
                }

                // Role Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = roleExpanded,
                        onExpandedChange = { roleExpanded = !roleExpanded }
                    ) {
                        OutlinedTextField(
                            value = roleOptions[selectedRole] ?: "Select Role",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Role") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { roleExpanded = true }
                        )
                        ExposedDropdownMenu(
                            expanded = roleExpanded,
                            onDismissRequest = { roleExpanded = false }
                        ) {
                            roleOptions.forEach { (key, value) ->
                                DropdownMenuItem(
                                    text = { Text(value) },
                                    onClick = {
                                        selectedRole = key
                                        roleExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Unit Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = unitExpanded,
                        onExpandedChange = { unitExpanded = !unitExpanded }
                    ) {
                        OutlinedTextField(
                            value = unitOptions[selectedUnit] ?: "Select Unit",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { unitExpanded = true }
                        )
                        ExposedDropdownMenu(
                            expanded = unitExpanded,
                            onDismissRequest = { unitExpanded = false }
                        ) {
                            unitOptions.forEach { (key, value) ->
                                DropdownMenuItem(
                                    text = { Text(value) },
                                    onClick = {
                                        selectedUnit = key
                                        unitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Error Message Display
                if (errorMessage.value.isNotEmpty()) {
                    item {
                        Text(
                            text = errorMessage.value,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Edit Staff Button
                item {
                    Button(
                        onClick = {
                            errorMessage.value = validateFields(
                                firstName,
                                lastName,
                                phoneNumber,
                                selectedRole,
                                selectedUnit
                            )

                            if (errorMessage.value.isEmpty()) {
                                val staffData = StaffMember(
                                    id = staff.id,
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim(),
                                    phone = "+234$phoneNumber",
                                    role = selectedRole!!,
                                    unit = selectedUnit!!
                                )

                                loadingState.value = true

                                rosterViewModel.editStaffData(
                                    staff = staffData,
                                    imageBitmap = croppedBitmap
                                ) { success, errorMessage ->
                                    if (success) {
                                        onDismissRequest()
                                    } else {
                                        editStaffFailed.value = errorMessage
                                    }
                                    loadingState.value = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        enabled = isChanged && !loadingState.value
                    ) {
                        if (loadingState.value) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Edit Staff Data")
                        }
                    }
                }
            }
        }
    }

    if (showCropper && cropBitmap != null) {

        val aspectRatio = cropBitmap.width.toFloat() / cropBitmap.height.toFloat()

        val imageCrop = remember { ImageCrop(cropBitmap) }

        Dialog(onDismissRequest = { showCropper = false }) {
            Card(
                // Black container color
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    imageCrop.ImageCropView(
                        modifier = Modifier
                            .height(300.dp)
                            .aspectRatio(aspectRatio)
                            .clip(RectangleShape),
                        cropType = CropType.SQUARE
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                croppedBitmap = imageCrop.onCrop()
                                showCropper = false
                                imageUri = null
                            }
                        ) {
                            Text("Crop Image")
                        }
                    }
                }
            }
        }
    }

    // Show Toast when edit staff fails
    if (editStaffFailed.value != null) {
        LaunchedEffect(Unit) {
            showLongToast(context, editStaffFailed.value!!)
            editStaffFailed.value = null
        }
    }
}

@Composable
fun EditCallDialog(
    rosterViewModel: RosterViewModel,
    staffList: List<StaffMember>,
    staffOnCall: List<StaffMember>,
    selectedWeek: Int,
    selectedYear: Int,
    callType: String,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var loadingState by remember { mutableStateOf(false) }

    // A mutable list holding the selected staff members
    val selectedStaff =
        remember { mutableStateListOf<StaffMember>().apply { addAll(staffOnCall) } }

    // Group staff by role and sort roles numerically
    val groupedStaff = staffList.groupBy { it.role }.toSortedMap()

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Staff on $callType\nFor this Week",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    groupedStaff.forEach { (role, staffForRole) ->
                        val roleName = getRoleName(role)
                        val sortedStaff =
                            staffForRole.sortedWith(compareBy({ it.unit }, { it.lastName }))

                        // Role Header
                        item {
                            Text(
                                text = roleName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Staff Items with checkboxes
                        items(sortedStaff) { staff ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (callType == "Ward Call") {
                                            // Multiple selections allowed
                                            if (selectedStaff.contains(staff)) {
                                                selectedStaff.remove(staff)
                                            } else {
                                                selectedStaff.add(staff)
                                            }
                                        } else {
                                            // Gym Call: Only one staff can be selected
                                            if (selectedStaff.contains(staff)) {
                                                selectedStaff.clear()
                                            } else {
                                                selectedStaff.clear()
                                                selectedStaff.add(staff)
                                            }
                                        }
                                    }
                            ) {
                                Checkbox(
                                    checked = selectedStaff.contains(staff),
                                    onCheckedChange = { isChecked ->
                                        if (callType == "Ward Call") {
                                            if (isChecked) {
                                                selectedStaff.add(staff)
                                            } else {
                                                selectedStaff.remove(staff)
                                            }
                                        } else {
                                            if (isChecked) {
                                                selectedStaff.clear()
                                                selectedStaff.add(staff)
                                            } else {
                                                selectedStaff.clear()
                                            }
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${staff.firstName} ${staff.lastName}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                    color = if (selectedStaff.contains(staff)) MaterialTheme.colorScheme.primary else Color.Unspecified
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        loadingState = true

                        val callDate = Pair(selectedYear, selectedWeek)

                        val staffToAddCall = selectedStaff.filter { it !in staffOnCall }
                        val staffToRemoveCall = staffOnCall.filter { it !in selectedStaff }

                        rosterViewModel.updateStaffCallDates(
                            staffToAddCall = staffToAddCall,
                            staffToRemoveCall = staffToRemoveCall,
                            callDate = callDate,
                            callType = callType
                        ) { success, message ->
                            loadingState = false
                            if (success) {
                                showLongToast(context, "Updated Successfully!")
                                onDismissRequest()
                            } else {
                                showLongToast(context, message ?: "Update failed.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loadingState
                ) {
                    if (loadingState) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}

@Composable
fun EditLeaveDialog(
    rosterViewModel: RosterViewModel,
    staffList: List<StaffMember>,
    staffOnLeave: List<StaffMember>,
    selectedMonth: Int,
    selectedYear: Int,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var loadingState by remember { mutableStateOf(false) }

    // A mutable list holding the selected staff members
    val selectedStaff =
        remember { mutableStateListOf<StaffMember>().apply { addAll(staffOnLeave) } }

    // Group staff by role and sort roles numerically
    val groupedStaff = staffList.groupBy { it.role }.toSortedMap()

    // Get the full month name
    val (_, fullMonthName) = getMonthInfo(selectedMonth)

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Staff on Leave for\n$fullMonthName $selectedYear",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    groupedStaff.forEach { (role, staffForRole) ->
                        // Use your helper to get the role name.
                        val roleName = getRoleName(role)
                        // Sort staff by unit and then by last name.
                        val sortedStaff =
                            staffForRole.sortedWith(compareBy({ it.unit }, { it.lastName }))

                        // Role Header
                        item {
                            Text(
                                text = roleName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Staff Items with checkboxes
                        items(sortedStaff) { staff ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (selectedStaff.contains(staff)) {
                                            selectedStaff.remove(staff)
                                        } else {
                                            selectedStaff.add(staff)
                                        }
                                    }
                            ) {
                                Checkbox(
                                    checked = selectedStaff.contains(staff),
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            selectedStaff.add(staff)
                                        } else {
                                            selectedStaff.remove(staff)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${staff.firstName} ${staff.lastName}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                    color = if (selectedStaff.contains(staff)) MaterialTheme.colorScheme.primary else Color.Unspecified
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        loadingState = true

                        val leaveDate = Pair(selectedYear, selectedMonth)

                        val staffToAddLeave = selectedStaff.filter { it !in staffOnLeave }
                        val staffToRemoveLeave = staffOnLeave.filter { it !in selectedStaff }

                        rosterViewModel.updateStaffLeaveDates(
                            staffToAddLeave = staffToAddLeave,
                            staffToRemoveLeave = staffToRemoveLeave,
                            leaveDate = leaveDate
                        ) { success, message ->
                            loadingState = false
                            if (success) {
                                showLongToast(context, "Updated Successfully!")
                                onDismissRequest()
                            } else {
                                showLongToast(context, message ?: "Update failed.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loadingState
                ) {
                    if (loadingState) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StaffListItemMenuDialog(
    context: Context,
    staff: StaffMember,
    isSignedIn: Boolean,
    isInEditMode: Boolean,
    onMenuDismissRequest: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = { onMenuDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StaffAvatarItem(staff, true)

                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text(
                            text = "${staff.lastName} ${staff.firstName}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(text = getUnitName(staff.unit))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val phoneNumber = staff.phone
                            Text(
                                text = phoneNumber,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .weight(1f)
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = {
                                            clipboardManager.setText(AnnotatedString(phoneNumber))
                                            showLongToast(context, "Phone number copied!")
                                        }
                                    )
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.ic_copy),
                                contentDescription = "Copy phone number",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(phoneNumber))
                                        showLongToast(context, "Phone number copied!")
                                    },
                                tint = Color.Gray
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${staff.phone}"))
                            context.startActivity(intent)
                            onMenuDismissRequest()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_phone),
                        contentDescription = "Call"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call Staff")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val url = "https://wa.me/${staff.phone}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                setPackage("com.whatsapp")
                            }

                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                showLongToast(context, "WhatsApp is not installed on your device.")
                            }

                            onMenuDismissRequest()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_whatsapp),
                        contentDescription = "WhatsApp"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Message on WhatsApp")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:${staff.phone}")
                                putExtra("sms_body", "Hello PT ${staff.firstName},")
                            }
                            context.startActivity(intent)
                            onMenuDismissRequest()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_messages),
                        contentDescription = "Text"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Text Message")
                }

                if (isSignedIn && isInEditMode) {
                    Divider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEditClicked()
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Edit"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Staff Data")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDeleteClicked()
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remove Staff", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun StaffDetailDialog(staff: StaffMember, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showReminderDialog by remember { mutableStateOf(false) }
    var isWeekSelected by remember { mutableStateOf(true) }
    var callType by remember { mutableIntStateOf(1) }
    var selectedDate by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    // Get current year, week, and month
    val currentYear = getCurrentYear()
    val currentWeek = getCurrentWeek()
    val currentMonth = getCurrentMonth()

    val noUpcomingEvent =
        staff.onCallDates.isEmpty() && staff.gymCallDates.isEmpty() && staff.leaveDates.isEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "${staff.firstName} ${staff.lastName}",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                if (noUpcomingEvent) {
                    Text(
                        "No Upcoming Calls or Leaves",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Text(
                        "Select a date to set a reminder",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Upcoming Calls
                if (staff.onCallDates.isNotEmpty()) {
                    Text("Ward Calls:", style = MaterialTheme.typography.titleMedium)
                    staff.onCallDates.forEach { (year, weeks) ->
                        Text("Year: $year", fontWeight = FontWeight.Bold)
                        weeks.forEach { week ->
                            val isPastOrCurrentWeek =
                                year < currentYear || (year == currentYear && week <= currentWeek)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        if (isPastOrCurrentWeek) Color.LightGray else MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = !isPastOrCurrentWeek) {
                                        isWeekSelected = true
                                        callType = 1
                                        selectedDate = year to week
                                        showReminderDialog = true
                                    }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    "◯ Week $week",
                                    color = if (isPastOrCurrentWeek) Color.LightGray else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Gym Calls
                if (staff.gymCallDates.isNotEmpty()) {
                    Text("Gym Calls:", style = MaterialTheme.typography.titleMedium)
                    staff.gymCallDates.forEach { (year, weeks) ->
                        Text("Year: $year", fontWeight = FontWeight.Bold)
                        weeks.forEach { week ->
                            val isPastOrCurrentWeek =
                                year < currentYear || (year == currentYear && week <= currentWeek)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        if (isPastOrCurrentWeek) Color.LightGray else MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = !isPastOrCurrentWeek) {
                                        isWeekSelected = true
                                        callType = 2
                                        selectedDate = year to week
                                        showReminderDialog = true
                                    }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    "◯ Week $week",
                                    color = if (isPastOrCurrentWeek) Color.LightGray else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Leave Months
                if (staff.leaveDates.isNotEmpty()) {
                    Text("Leave Months:", style = MaterialTheme.typography.titleMedium)
                    staff.leaveDates.forEach { (year, month) ->
                        val monthName = getMonthInfo(month).second
                        val isPastOrCurrentMonth =
                            year < currentYear || (year == currentYear && month <= currentMonth)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isPastOrCurrentMonth) Color.LightGray else MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = !isPastOrCurrentMonth) {
                                    isWeekSelected = false
                                    callType = 3
                                    selectedDate = year to month
                                    showReminderDialog = true
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                "◯ Year: $year, Month: $monthName",
                                color = if (isPastOrCurrentMonth) Color.LightGray else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        },
        confirmButton = {
            if (!noUpcomingEvent) {
                Button(onClick = {
                    coroutineScope.launch {
                        WorkManager.getInstance(context).cancelAllWorkByTag("reminder_${staff.id}")

                        DataStoreManager.removeAllRemindersForStaff(context, staff.id)
                        showLongToast(context, "All reminders canceled for ${staff.firstName}")
                    }
                }) {
                    Text("Cancel All Reminders")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )

    if (showReminderDialog && selectedDate != null) {
        ReminderSetupDialog(staff, callType, selectedDate!!, isWeekSelected) {
            showReminderDialog = false
        }
    }
}

@Composable
fun ReminderSetupDialog(
    staff: StaffMember,
    callType: Int,
    selectedDate: Pair<Int, Int>,
    isWeekSelected: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val reminderOptions = listOf("A Month Before", "A Week Before", "A Day Before")
    var selectedOption by remember { mutableStateOf(reminderOptions.first()) }
    val coroutineScope = rememberCoroutineScope()

    val (year, timeUnit) = selectedDate

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Reminder") },
        text = {
            Column {
                Text(
                    "Set a reminder for ${
                        if (isWeekSelected) "Week $timeUnit" else getMonthInfo(
                            timeUnit
                        ).second
                    } in Year $year",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                reminderOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { selectedOption = option }
                        )
                        Text(option, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val calendar = Calendar.getInstance()

                // Determine the first day of the selected date
                if (isWeekSelected) {
                    // Set the calendar to the first day (Monday) of the given week
                    calendar.clear()
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.WEEK_OF_YEAR, timeUnit)
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                } else {
                    // Set the calendar to the first day of the selected month
                    calendar.clear()
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, timeUnit - 1) // Months are 0-based
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                }

                // Set reminder time to 9 AM on that day
                calendar.set(Calendar.HOUR_OF_DAY, 9)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // Determine how many days before the event the reminder should go off
                val delayMillis = when (selectedOption) {
                    "A Month Before" -> TimeUnit.DAYS.toMillis(30)
                    "A Week Before" -> TimeUnit.DAYS.toMillis(7)
                    "A Day Before" -> TimeUnit.DAYS.toMillis(1)
                    else -> 0L
                }

                // Calculate the final reminder time
                val reminderTimeInMillis = calendar.timeInMillis - delayMillis
                val currentTime = System.currentTimeMillis()

                if (reminderTimeInMillis <= currentTime) {
                    showLongToast(
                        context,
                        "The reminder date selected is in the past! Please select a future time. ❌"
                    )
                    return@Button
                }

                val callTypeText = when (callType) {
                    1 -> "Ward call"
                    2 -> "Gym call"
                    3 -> "Leave"
                    else -> "Event"
                }

                val selectedOptionFormatted = selectedOption.removeSuffix(" Before")

                val title = "$selectedOptionFormatted's $callTypeText Reminder"
                val message =
                    "For ${staff.firstName} ${staff.lastName}'s $callTypeText coming up in ${
                        if (isWeekSelected) "Week $timeUnit" else getMonthInfo(
                            timeUnit
                        ).second
                    }, $year."

                coroutineScope.launch {

                    val existingReminders = DataStoreManager.getReminders(context)
                    val isDuplicate =
                        existingReminders.any { it.staffId == staff.id && it.title == title && it.message == message && it.delayOption == selectedOption }

                    if (isDuplicate) {
                        showShortToast(context, "Reminder for this has already been set! 🚫")
                        return@launch
                    }

                    scheduleReminder(
                        context,
                        staff.id,
                        selectedOption,
                        reminderTimeInMillis,
                        title,
                        message
                    )

                    showShortToast(context, "Reminder set successfully")
                    onDismiss()
                }
            }) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ------------------------------------

@Composable
fun SuggestionResolveDialog(
    viewModel: RosterViewModel,
    selectedSuggestion: Suggestion,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var resolutionNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Resolve Suggestion") },
        text = {
            Column {
                Text("Mark this suggestion as resolved?")
                OutlinedTextField(
                    value = resolutionNote,
                    onValueChange = { resolutionNote = it },
                    label = { Text("Add a note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.resolveSuggestion(
                    selectedSuggestion.id,
                    "Admin",
                    resolutionNote
                ) { success, errorMessage ->
                    showShortToast(
                        context,
                        if (success) "Suggestion Successfully Resolved!" else errorMessage
                            ?: "Suggestion Resolve Failed"
                    )
                    onDismissRequest()
                }
            }) {
                Text("Resolve")
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SuggestionDeleteDialog(
    viewModel: RosterViewModel,
    selectedSuggestion: Suggestion,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Delete Suggestion") },
        text = { Text("Delete Suggestion from database?") },
        confirmButton = {
            Button(onClick = {
                viewModel.deleteSuggestion(selectedSuggestion.id) { _, _ ->
                    onDismissRequest()
                }
            }) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun InfoDialog(title: String, content: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = { Text(title) },
        text = {
            Box(
                modifier = Modifier
                    .heightIn(min = 100.dp, max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(content, style = MaterialTheme.typography.bodyMedium)
            }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(
    staff: StaffMember,
    rosterViewModel: RosterViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Confirm Removal") },
        text = { Text("Are you sure you want to remove ${staff.firstName} ${staff.lastName} from the database? This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = {
                    rosterViewModel.removeStaff(staff.id) { success, errorMessage ->
                        showLongToast(
                            context,
                            if (success) "Removal Successful!" else errorMessage
                                ?: "Removal Failed"
                        )
                        onDismissRequest()
                    }
                }
            ) {
                Text("Remove", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfirmClearRemindersDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to clear all reminders? This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        WorkManager.getInstance(context).cancelAllWork()

                        DataStoreManager.removeAllReminders(context)
                        showShortToast(context, "Reminders cleared")

                        onDismissRequest()
                    }
                }
            ) {
                Text("Clear", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PasteTemplateDialog(
    onTemplateClicked: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val templates = mapOf(
        "Systemic Clerking Template" to context.getString(R.string.systemic_clerking_template),
        "Segmental Clerking Template" to context.getString(R.string.segmental_clerking_template),
        "Pediatrics Clerking Template" to context.getString(R.string.pediatrics_clerking_template)
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Select Template", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                templates.forEach { (name, template) ->
                    Text(
                        text = name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                onTemplateClicked(template)
                            }
                    )
                }
            }
        }
    }
}
