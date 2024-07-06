package com.mike.studentportal

import android.content.Context
import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import com.mike.studentportal.MyDatabase.getAllScreenTimes
import com.mike.studentportal.MyDatabase.updatePassword
import kotlinx.coroutines.delay
import com.mike.studentportal.CommonComponents as CC
data class SectionState(val title: String, val icon: ImageVector, var isExpanded: Boolean)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSettingsScreen(navController: NavController, context: Context) {
    val startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timeSpent by remember { mutableLongStateOf(0L) }
    val screenID = "SC8"

    val sections = remember {
        mutableStateListOf(
            SectionState("Profile", Icons.Default.AccountCircle, false),
            SectionState("System", Icons.Default.SafetyCheck, false),
            SectionState("Statistics", Icons.Default.BarChart, false),
            SectionState("Security", Icons.Default.Security, false),
            SectionState("Feedback", Icons.AutoMirrored.Filled.Message, false)
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            timeSpent = System.currentTimeMillis() - startTime
            delay(1000) // Update every second (adjust as needed)
        }
    }

    DisposableEffect(Unit) {
        GlobalColors.loadColorScheme(context)
        onDispose {
            // Fetch the screen details
            MyDatabase.getScreenDetails(screenID) { screenDetails ->
                if (screenDetails != null) {
                    MyDatabase.writeScren(courseScreen = screenDetails) {}
                    // Fetch existing screen time
                    MyDatabase.getScreenTime(screenID) { existingScreenTime ->
                        val totalScreenTime = if (existingScreenTime != null) {
                            Log.d("Screen Time", "Retrieved Screen time: $existingScreenTime")
                            existingScreenTime.time + timeSpent
                        } else {
                            timeSpent
                        }

                        // Create a new ScreenTime object
                        val screentime = ScreenTime(
                            id = screenID,
                            screenName = screenDetails.screenName,
                            time = totalScreenTime
                        )

                        // Save the updated screen time
                        MyDatabase.saveScreenTime(screenTime = screentime, onSuccess = {
                            Log.d("Screen Time", "Saved $totalScreenTime to the database")
                        }, onFailure = {
                            Log.d("Screen Time", "Failed to save $totalScreenTime to the database")
                        })
                    }

                } else {
                    Log.d("Screen Time", "Screen details not found for ID: $screenID")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = CC.titleTextStyle(context)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back",
                            tint = GlobalColors.textColor
                        )
                    }
                }
            )
        },
        containerColor = GlobalColors.primaryColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Background(context)
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(it)
                    .fillMaxSize()
                    .background(GlobalColors.primaryColor)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                sections.forEachIndexed { index, section ->
                    SectionTitle(context, section.icon, section.title, onClick = {
                        sections[index] = section.copy(isExpanded = !section.isExpanded)
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedVisibility(
                        visible = section.isExpanded,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 300))
                    ) {
                        when (section.title) {
                            "Profile" -> ProfileCard(context)
                            "System" -> SystemSettings(context)
                            "Statistics" -> {
                                Text(
                                    "Most used section of this app (Across all users)",
                                    style = CC.descriptionTextStyle(context)
                                )
                                ScreenWithMostTimeSpent(navController, context)
                            }
                            "Security" -> {
                                Text("Change Password", style = CC.descriptionTextStyle(context))
                                PasswordUpdateSection(context)
                            }
                            "Feedback" -> RatingAndFeedbackScreen(context)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                MyAbout(context)
            }
        }
    }
}

@Composable
fun SectionTitle(context: Context, icon: ImageVector, title: String, onClick: () -> Unit) {
    LaunchedEffect(Unit) {
        GlobalColors.loadColorScheme(context)
    }
    Row(
        modifier = Modifier
            .clickable { onClick() }
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


@Composable
fun ProfileCard(
    context: Context
) {
    var user by remember { mutableStateOf(User()) }
    var isExpanded by remember { mutableStateOf(false) }
    var currentName by remember { mutableStateOf("") }
    var auth = FirebaseAuth.getInstance()
    var currentEmail by remember { mutableStateOf("") }
    var currentAdmissionNumber by remember { mutableStateOf("") }

    // Fetch user data when the composable is launched
    LaunchedEffect(auth.currentUser?.email) {
        auth.currentUser?.email?.let {
            fetchUserDataByEmail(it) { fetchedUser ->
                fetchedUser?.let {
                    user = it
                    currentName = it.firstName +" "+ it.lastName
                    currentEmail = it.email
                    currentAdmissionNumber = it.id
                }
                Log.e("ProfileCard", "Fetched user: $user")
            }
        }
    }


    Card(
        modifier = Modifier
            .border(
                    1.dp,GlobalColors.secondaryColor,RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlobalColors.primaryColor
        ),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(GlobalColors.primaryColor)
                .padding(16.dp)
        ) {
            // Only show the name field initially
            Row(modifier = Modifier
                .padding(vertical = 8.dp)
                .border(
                    1.dp,
                    GlobalColors.secondaryColor,
                    RoundedCornerShape(10.dp)
                )
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start) {
                Text(
                    text = currentName,
                    style = CC.descriptionTextStyle(context).copy(
                        fontSize = 14.sp,
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                        .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                )
            }
            // Animated visibility for other fields
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Row(modifier = Modifier
                        .padding(vertical = 8.dp)
                        .border(
                            1.dp,
                            GlobalColors.secondaryColor,
                            RoundedCornerShape(10.dp)
                        )
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start) {
                        Text(
                            text = "Email: $currentEmail",
                            style = CC.descriptionTextStyle(context).copy(
                                fontSize = 14.sp,
                            ),
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        )
                    }
                    Row(modifier = Modifier
                        .padding(vertical = 8.dp)
                        .border(
                            1.dp,
                            GlobalColors.secondaryColor,
                            RoundedCornerShape(10.dp)
                        )
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = "Admission Number: $currentAdmissionNumber",
                        style = CC.descriptionTextStyle(context).copy(
                            fontSize = 14.sp,
                        ),
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                            .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    )
                    }
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
                        MyDatabase.generateFeedbackID { feedbackId ->
                            val feedback = Feedback(
                                id = feedbackId,
                                rating = currentRating,
                                sender = user.firstName+" "+user.lastName,
                                message = feedbackText,
                                admissionNumber = user.id
                            )
                        MyDatabase.writeFeedback(feedback, onSuccess = {
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
                        )}
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


@Composable
fun ScreenWithMostTimeSpent(navController: NavController, context: Context) {
    val screenTimes = remember { mutableStateListOf<ScreenTime>() }
    var screenWithMaxTime by remember { mutableStateOf<ScreenTime?>(null) }
    var loading by remember { mutableStateOf(true) }

    // Fetch screen times when the composable enters the composition
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            getAllScreenTimes { fetchedScreenTimes ->
                Log.d("fetched Screen time", "The screen times are: $fetchedScreenTimes")
                screenTimes.clear()
                screenTimes.addAll(fetchedScreenTimes)

                // Find screen with maximum time
                screenWithMaxTime = fetchedScreenTimes.maxByOrNull { it.time }
            }
        }
    }

    // Display screen with maximum time (if available)
    screenWithMaxTime?.let {
        Column(
            modifier = Modifier
                .background(GlobalColors.extraColor2, RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = GlobalColors.textColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(it.screenName, style = CC.titleTextStyle(context))
            Text(convertToHoursMinutesSeconds(it.time), style = CC.descriptionTextStyle(context))

            // Place the IconButton inside the Column
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Button(onClick = {navController.navigate("statistics")},
                    modifier = Modifier.fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlobalColors.secondaryColor
                    )
                ) {
                    Text("View more statistics", style = CC.descriptionTextStyle(context))
                }
                }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    NewSettingsScreen(rememberNavController(), LocalContext.current)
}
