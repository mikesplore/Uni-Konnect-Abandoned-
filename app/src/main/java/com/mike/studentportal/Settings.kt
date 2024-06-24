package com.mike.studentportal

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.mike.studentportal.MyDatabase.fetchUserDataByEmail
import com.mike.studentportal.MyDatabase.getRating
import com.mike.studentportal.MyDatabase.saveRating
import com.mike.studentportal.MyDatabase.updatePassword
import com.mike.studentportal.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, context: Context) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = CC.titleTextStyle(context)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor
                )
            )
        }, containerColor = GlobalColors.primaryColor
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .fillMaxSize()
                .background(GlobalColors.primaryColor)
                .padding(16.dp), // Added padding to the column
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionTitle(context, Icons.Default.AccountCircle,"Profile")
            ProfileCard(context = context)
            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing

            SectionWithRow(
                title = "Appearance",
                description = "Change the appearance of the app",
                navController = navController,
                route = "colors",
                context = context
            )
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before system settings section
            SectionTitle(context, Icons.Default.SafetyCheck,"System")
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before system settings section
            SystemSettings(context)
            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
            SectionTitle(context, Icons.Default.Security,"Security")
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before password section
            Text("Change Password", style = CC.descriptionTextStyle(context))
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before password section
            PasswordUpdateSection(context)
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before feedback section
            SectionTitle(context, Icons.Default.Feedback,"We value your Feedback")
            RatingAndFeedbackScreen(context)

        }
    }
}

@Composable
fun SectionTitle(context: Context,icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
            )
            .background(Color(0xFF2E2E2E), RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = "Icon", tint = GlobalColors.textColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = CC.titleTextStyle(context), fontSize = 20.sp)
    }
}

@Composable
fun SectionWithRow(
    title: String,
    description: String,
    navController: NavController,
    route: String,
    context: Context
) {
    Row(
        modifier = Modifier
            .background(Color(0xFF2E2E2E), RoundedCornerShape(10.dp))
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)

            )
            .fillMaxWidth()
            .padding(6.dp)
            .padding(start = 20.dp, end = 20.dp), // Added vertical padding
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(title, style = CC.titleTextStyle(context), fontSize = 20.sp)
            Text(description, style = CC.descriptionTextStyle(context))
        }

        IconButton(onClick = {
            navController.navigate(route)
        }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Arrow",
                tint = GlobalColors.textColor
            )
        }
    }
}

@Composable
fun SystemSettings(context: Context) {
    var checked by remember { mutableStateOf(false) }
    var edge by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        SettingSwitch(context, "Disable EdgeToEdge", edge) {
            Global.edgeToEdge.value = !Global.edgeToEdge.value
            edge = !edge
        }
        SettingSwitch(context, "Enable System Notifications", checked) {
            Global.showAlert.value = !Global.showAlert.value
            checked = !checked
        }
    }
}

@Composable
fun SettingSwitch(
    context: Context,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit // Callback for checked state changes
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = CC.descriptionTextStyle(context))
        Switch(
            checked = checked, onCheckedChange = { isChecked ->
                onCheckedChange(isChecked) // Invoke the callback with the new state
            }, colors = SwitchDefaults.colors(
                checkedThumbColor = GlobalColors.primaryColor,
                checkedTrackColor = GlobalColors.secondaryColor,
                uncheckedThumbColor = GlobalColors.primaryColor,
                uncheckedTrackColor = GlobalColors.secondaryColor
            )
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCard(
    context: Context
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var user by remember { mutableStateOf(User()) }
    var isEditing by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    var currentName by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }
    var currentAdmissionNumber by remember { mutableStateOf("") }

    // Fetch user data when the composable is launched
    LaunchedEffect(currentUser?.email) {
        currentUser?.email?.let { email ->
            fetchUserDataByEmail(email) { fetchedUser ->
                fetchedUser?.let {
                    user = it
                    currentName = it.name
                    currentEmail = it.email
                    currentAdmissionNumber = it.id
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
            )
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlobalColors.primaryColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            8.dp
        ),
    ) {
        Column(
            modifier = Modifier
                .background(GlobalColors.primaryColor)
                .padding(10.dp)
        ) {
            // Only show the name field initially
            ProfileTextField(
                value = currentName,
                isEditing = isEditing,
                onValueChange = { currentName = it },
                context = context
            )

            // Animated visibility for other fields
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    ProfileTextField(
                        value = currentEmail,
                        isEditing = false,
                        onValueChange = { currentEmail = it },
                        context = context
                    )

                    ProfileTextField(
                        value = currentAdmissionNumber,
                        isEditing = false,
                        onValueChange = { currentAdmissionNumber = it },
                        context = context
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Button(
                            onClick = { isEditing = !isEditing },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEditing) GlobalColors.secondaryColor else Color(
                                    0xFF007BFF
                                ), contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                if (isEditing) "Cancel" else "Edit",
                                style = CC.descriptionTextStyle(context)
                            )
                        }

                        // AnimatedVisibility for the "Save" button
                        AnimatedVisibility(
                            visible = isEditing,
                            enter = fadeIn() + slideInHorizontally(), // Enter animation
                            exit = fadeOut() + slideOutHorizontally()  // Exit animation
                        ) {
                            Button(
                                onClick = {
                                    MyDatabase.updateUserNameById(
                                        currentAdmissionNumber,
                                        currentName,
                                        callback = {
                                            Toast.makeText(
                                                context,
                                                "Profile updated successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Details.name.value = currentName
                                        })
                                    isEditing = false

                                }, colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF007BFF),
                                    contentColor = GlobalColors.textColor
                                ), shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save", style = CC.descriptionTextStyle(context))
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileTextField(
    value: String, isEditing: Boolean, onValueChange: (String) -> Unit, context: Context
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        enabled = isEditing,
        textStyle = CC.descriptionTextStyle(context),
        colors = TextFieldDefaults.colors(
            focusedTextColor = CC.textColor,
            disabledContainerColor = Color.DarkGray,
            focusedContainerColor = GlobalColors.primaryColor,
            unfocusedContainerColor = GlobalColors.primaryColor,
            disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
            disabledLabelColor = LocalContentColor.current.copy(ContentAlpha.medium)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun PasswordTextField(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    context: Context
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = CC.descriptionTextStyle(context)) },
        enabled = isEditing,
        textStyle = CC.descriptionTextStyle(context),
        colors = TextFieldDefaults.colors(
            focusedTextColor = CC.textColor,
            disabledContainerColor = Color.DarkGray,
            focusedContainerColor = GlobalColors.primaryColor,
            unfocusedContainerColor = GlobalColors.primaryColor,
            disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current),
            disabledLabelColor = LocalContentColor.current.copy(ContentAlpha.medium)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun PasswordUpdateSection(context: Context) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PasswordTextField(
            label = "Current Password",
            value = currentPassword,
            isEditing = true,
            onValueChange = { currentPassword = it },
            context = context
        )
        PasswordTextField(
            label = "New Password",
            value = newPassword,
            isEditing = true,
            onValueChange = { newPassword = it },
            context = context
        )
        PasswordTextField(
            label = "Confirm Password",
            value = confirmPassword,
            isEditing = true,
            onValueChange = { confirmPassword = it },
            context = context
        )

        Button(
            onClick = {
                loading = true
                if (newPassword == confirmPassword && newPassword.isNotEmpty() && currentPassword.isNotEmpty()) {
                    currentUser?.let { user ->
                        val credential =
                            EmailAuthProvider.getCredential(user.email!!, currentPassword)
                        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                            if (reauthTask.isSuccessful) {
                                updatePassword(newPassword, onSuccess = {
                                    // Handle success (e.g., show a success message)
                                    loading = false
                                    Toast.makeText(
                                        context,
                                        "Password updated successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                }, onFailure = { exception ->
                                    // Handle failure (e.g., show an error message)
                                    loading = false
                                    Toast.makeText(
                                        context,
                                        "Failed to Change password: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                })
                            } else {
                                // Handle reauthentication failure
                                loading = false
                                Toast.makeText(
                                    context,
                                    "Authentication failed: ${reauthTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    // Handle password mismatch
                    loading = false
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.padding(top = 16.dp), colors = ButtonDefaults.buttonColors(
                containerColor = GlobalColors.secondaryColor, contentColor = Color.White
            ), shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = GlobalColors.primaryColor,
                        trackColor = GlobalColors.tertiaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Change Password", style = CC.descriptionTextStyle(context))
                }
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingAndFeedbackScreen(context: Context) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var currentRating by remember { mutableStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }
    var admissionNumber by remember { mutableStateOf("") }
    var user by remember { mutableStateOf(User()) }
    var showFeedbackForm by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser?.email) {
        currentUser?.email?.let { email ->
            fetchUserDataByEmail(email) { fetchedUser ->
                fetchedUser?.let {
                    user = it
                    admissionNumber = it.id
                    getRating(admissionNumber,
                        onRatingFetched = { rating -> currentRating = rating },
                        onFailure = {
                            Toast.makeText(
                                context,
                                "Failed to fetch rating: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Rate Us",
            style = CC.descriptionTextStyle(context),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        StarRating(
            currentRating = currentRating, onRatingChanged = { rating ->
                currentRating = rating
                showFeedbackForm = true
            }, context = context
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = showFeedbackForm) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = {
                        Text(
                            "Enter your feedback",
                            style = CC.descriptionTextStyle(context)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    textStyle = CC.descriptionTextStyle(context),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        loading = true
                        currentUser?.uid?.let { userId ->
                            saveRating(userId, currentRating, onSuccess = {
                                MyDatabase.writeFeedback(Feedback(
                                    sender = user.name,
                                    message = feedbackText,
                                    admissionNumber = user.id
                                ), onSuccess = {
                                    loading = false
                                    Toast.makeText(
                                        context,
                                        "Thanks for your feedback",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    feedbackText = ""
                                    showFeedbackForm = false
                                }, onFailure = {
                                    loading = false
                                    Toast.makeText(
                                        context,
                                        "Failed to send feedback: ${it?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            }, onFailure = {
                                loading = false
                                Toast.makeText(
                                    context,
                                    "Failed to save rating: ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007BFF), contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color = GlobalColors.primaryColor,
                                trackColor = GlobalColors.tertiaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Submit Feedback", style = CC.descriptionTextStyle(context))
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun StarRating(
    currentRating: Int, onRatingChanged: (Int) -> Unit, maxRating: Int = 5, context: Context
) {
    var selectedRating by remember { mutableStateOf(currentRating) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..maxRating) {
            Icon(imageVector = if (i <= selectedRating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = if (i <= selectedRating) "Selected Star" else "Unselected Star",
                tint = if (i <= selectedRating) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp)
                    .clickable {
                        selectedRating = i
                        onRatingChanged(selectedRating)
                    })
        }
    }
}


@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(rememberNavController(), LocalContext.current)
}

