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
import androidx.compose.material.icons.automirrored.filled.Message
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
        Box(modifier = Modifier.fillMaxSize()){
            Background(context)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .fillMaxSize()
                .background(GlobalColors.primaryColor)
                .padding(16.dp), // Added padding to the column
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionTitle(context, Icons.Default.AccountCircle, "Profile")
            ProfileCard(context = context)
            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing

            SectionWithRow(
                title = "Appearance",
                description = "Change the appearance of the app",
                navController = navController,
                route = "appearance",
                context = context
            )
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before system settings section
            SectionTitle(context, Icons.Default.SafetyCheck, "System")
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before system settings section
            SystemSettings(context)
            Spacer(modifier = Modifier.height(8.dp)) // Increased spacing
            SectionTitle(context, Icons.Default.Security, "Security")
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before password section
            Text("Change Password", style = CC.descriptionTextStyle(context))
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before password section
            PasswordUpdateSection(context)
            Spacer(modifier = Modifier.height(8.dp)) // Small spacing before feedback section
            SectionTitle(context, Icons.AutoMirrored.Filled.Message, "We value your Feedback")
            RatingAndFeedbackScreen(context)

        }
        }
    }
}

@Composable
fun SectionTitle(context: Context, icon: ImageVector, title: String) {
    LaunchedEffect(Unit) {
        GlobalColors.loadColorScheme(context)
    }
    Row(
        modifier = Modifier
            .height(40.dp)
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
            )
            .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
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
            .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
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
                                containerColor = if (isEditing) GlobalColors.secondaryColor else GlobalColors.extraColor1,
                                contentColor = Color.White
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
                                    MyDatabase.updateUserNameById(currentAdmissionNumber,
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
                                    containerColor = GlobalColors.extraColor1,
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
            focusedTextColor = GlobalColors.textColor,
            disabledContainerColor = GlobalColors.tertiaryColor,
            focusedContainerColor = GlobalColors.tertiaryColor,
            unfocusedContainerColor = GlobalColors.tertiaryColor,
            disabledTextColor = GlobalColors.textColor,
            disabledLabelColor = GlobalColors.textColor
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
            focusedTextColor = GlobalColors.textColor,
            disabledContainerColor = GlobalColors.secondaryColor,
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
                                        context, "Password updated successfully", Toast.LENGTH_SHORT
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
                containerColor = GlobalColors.tertiaryColor, contentColor = Color.White
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
fun StarRating(
    currentRating: Int,
    onRatingChanged: (Int) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val color = when {
                i <= currentRating -> when (i) {
                    in 1..2 -> Color.Red
                    3 -> GlobalColors.extraColor2
                    else -> Color.Green
                }

                else -> GlobalColors.secondaryColor
            }
            val animatedScale by animateFloatAsState(
                targetValue = if (i <= currentRating) 1.2f else 1.0f,
                animationSpec = tween(durationMillis = 300),
                label = ""
            )
            Star(filled = i <= currentRating,
                color = color,
                scale = animatedScale,
                onClick = { onRatingChanged(i) })
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun Star(
    filled: Boolean, color: Color, scale: Float, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val path = Path().apply {
        moveTo(50f, 0f)
        lineTo(61f, 35f)
        lineTo(98f, 35f)
        lineTo(68f, 57f)
        lineTo(79f, 91f)
        lineTo(50f, 70f)
        lineTo(21f, 91f)
        lineTo(32f, 57f)
        lineTo(2f, 35f)
        lineTo(39f, 35f)
        close()
    }

    Canvas(
        modifier = modifier
            .size((40 * scale).dp)
            .clickable(onClick = onClick)
    ) {
        drawPath(
            path = path,
            color = if (filled) color else Color.Gray,
            style = if (filled) Stroke(width = 8f) else Stroke(
                width = 8f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun RatingAndFeedbackScreen(context: Context) {
    var currentRating by remember { mutableIntStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }
    var averageRatings by remember { mutableStateOf("") }
    val user by remember { mutableStateOf(User()) }
    var showFeedbackForm by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        MyDatabase.fetchAverageRating { averageRating ->
            averageRatings = averageRating
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
            text = if (averageRatings.isEmpty()) "No ratings yet" else "Average Rating: $averageRatings",
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
                            "Enter your feedback (optional)", style = CC.descriptionTextStyle(context)
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
                        MyDatabase.writeFeedback(Feedback(
                            rating = currentRating,
                            sender = user.name,
                            message = feedbackText,
                            admissionNumber = user.id
                        ), onSuccess = {
                            loading = false
                            Toast.makeText(
                                context, "Thanks for your feedback", Toast.LENGTH_SHORT
                            ).show()
                            feedbackText = ""
                            MyDatabase.fetchAverageRating { averageRating ->
                                averageRatings = averageRating
                            }
                            showFeedbackForm = false
                        }, onFailure = {
                            loading = false
                            Toast.makeText(
                                context,
                                "Failed to send feedback: ${it?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlobalColors.extraColor1,
                        contentColor = GlobalColors.secondaryColor
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


@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(rememberNavController(), LocalContext.current)
}

