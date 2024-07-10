package com.mike.unikonnect

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.mike.unikonnect.MyDatabase.fetchUserDataByEmail
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mike.unikonnect.CommonComponents as CC
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun SignAttendanceScreen(navController: NavController, context: Context) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val configuration = LocalConfiguration.current
    val auth = FirebaseAuth.getInstance()
    val screenWidth = configuration.screenWidthDp.dp
    var loading by remember { mutableStateOf(true) }
    val courses = remember { mutableStateListOf<Course>() }
    val attendanceStates = remember { mutableStateListOf<AttendanceState>() }
    val attendanceRecordsMap = remember { mutableStateMapOf<String, List<Attendance>>() }
    val coroutineScope = rememberCoroutineScope()
    var user by remember { mutableStateOf(User()) }
    var currentName by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }
    var currentAdmissionNumber by remember { mutableStateOf("") }
    val startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timeSpent by remember { mutableLongStateOf(0L) }
    val screenID = "SC9"

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
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Fetch courses and attendance states initially
        LaunchedEffect(loading) {
            MyDatabase.fetchCourses { fetchedCourses ->
                courses.clear()
                courses.addAll(fetchedCourses)

                // Fetch attendance states and records for each course
                coroutineScope.launch {
                    fetchedCourses.forEach { course ->
                        MyDatabase.fetchAttendanceState(course.courseCode) { fetchedState ->
                            if (fetchedState != null) {
                                attendanceStates.add(fetchedState)
                            }
                        }

                        MyDatabase.fetchAttendances(currentAdmissionNumber, course.courseCode) { fetchedAttendances ->
                            attendanceRecordsMap[course.courseCode] = fetchedAttendances
                        }
                    }
                    loading = false
                }
            }
        }

        // Fetch user data when the composable is launched
        LaunchedEffect(auth.currentUser) {
            auth.currentUser?.email?.let {
                fetchUserDataByEmail(it) { fetchedUser ->
                    fetchedUser?.let {
                        user = it
                        currentName = it.firstName + " " + it.lastName
                        currentEmail = it.email
                        currentAdmissionNumber = it.id
                    }
                }
            }
        }

        val indicator = @Composable { tabPositions: List<TabPosition> ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .height(4.dp)
                    .width(screenWidth / (courses.size.coerceAtLeast(1))) // Avoid division by zero
                    .background(CC.secondary(), CircleShape)
            )
        }

        if (loading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                ColorProgressIndicator(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Loading Courses and Attendances", style = CC.descriptionTextStyle(context))
            }
        } else {
            if (courses.isEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("No courses found", style = CC.descriptionTextStyle(context))
                }
            } else {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.background(Color.LightGray),
                    contentColor = Color.Black,
                    indicator = indicator,
                    edgePadding = 0.dp,
                    containerColor = CC.primary()
                ) {
                    courses.forEachIndexed { index, course ->
                        Tab(selected = selectedTabIndex == index, onClick = {
                            selectedTabIndex = index
                        }, text = {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTabIndex == index) CC.secondary() else CC.primary(),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp), contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = course.courseName,
                                    color = if (selectedTabIndex == index) CC.textColor() else CC.tertiary(),
                                )
                            }
                        }, modifier = Modifier.background(CC.primary())
                        )
                    }
                }

                if (selectedTabIndex in courses.indices && selectedTabIndex in attendanceStates.indices) {
                    AttendanceList(
                        studentID = currentAdmissionNumber,
                        courseCode = courses[selectedTabIndex].courseCode,
                        context,
                        attendanceRecordsMap[courses[selectedTabIndex].courseCode] ?: emptyList(),
                        attendanceStates[selectedTabIndex], // Pass the attendance state for the selected tab


                    )
                }
            }
        }
    }
}



@Composable
fun AttendanceList(
    studentID: String,
    courseCode: String,
    context: Context,
    attendanceRecords: List<Attendance>,
    attendanceState: AttendanceState,
) {
    var loading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

    // Check if attendance has been signed today for the current course
    val hasSignedToday = remember {
        mutableStateOf(attendanceRecords.any { it.date == today })
    }

    // Calculate the counts of present and absent attendances
    val presentCount = attendanceRecords.count { it.status == "Present" }
    val absentCount = attendanceRecords.count { it.status == "Absent" }

    // Function to fetch attendance state continuously
    LaunchedEffect(Unit) {
        val interval = 10000L // Fetch every 10 seconds
        while (true) {
            MyDatabase.fetchAttendanceState(courseCode) { fetchedState ->
                if (fetchedState?.state != null) {
                    attendanceState.state = fetchedState.state
                }
            }
            delay(interval)
        }
    }

    Column(
        modifier = Modifier

            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (attendanceRecords.isEmpty()) {
            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                Text("No attendance records found", style = CC.descriptionTextStyle(context))
            }
        } else {

            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(attendanceRecords, key = { _, record -> record.id }) { index, record ->
                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            animationSpec = tween(
                                500,
                                easing = LinearOutSlowInEasing
                            )
                        ) { fullHeight -> -fullHeight },
                        exit = slideOutVertically(
                            animationSpec = tween(
                                500,
                                easing = FastOutLinearInEasing
                            )
                        ) { fullHeight -> fullHeight }
                    ) {
                        AttendanceCard(record, context, index)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to sign attendance
        Button(
            onClick = {
                coroutineScope.launch {
                    loading = true
                    MyDatabase.signAttendance(studentID, courseCode, "Present") { success ->
                        if (success) {
                            // Update attendance state after signing
                            MyDatabase.fetchAttendances(studentID, courseCode) { fetchedAttendance ->
                                attendanceState.state = fetchedAttendance.any { it.date == today }
                                Log.d("Comparison result","$attendanceState")
                                Log.d("Comparison result","Signing today for $courseCode")
                                // Reset hasSignedToday to true after successfully signing attendance
                                hasSignedToday.value = true
                                Toast.makeText(context, "Attendance signed successfully", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Already signed attendance", Toast.LENGTH_SHORT).show()
                        }
                        loading = false
                    }
                }

            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CC.secondary()),
            enabled = attendanceState.state,
            shape = RoundedCornerShape(10.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CC.textColor())
            } else if (attendanceState.state) {
                Text("Sign Attendance", style = CC.descriptionTextStyle(context))
            } else {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = CC.tertiary()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display total present and absent counts
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = CC.secondary(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .width(300.dp)
                    .padding(16.dp)
                    .background(CC.secondary().copy(alpha = 0.1f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Present:",
                    style = CC.descriptionTextStyle(context).copy(color = CC.textColor())
                )
                Text(
                    text = "$presentCount",
                    style = CC.descriptionTextStyle(context).copy(color = CC.textColor())
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = CC.secondary(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .width(300.dp)
                    .padding(16.dp)
                    .background(CC.primary().copy(alpha = 0.1f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Absent:",
                    style = CC.descriptionTextStyle(context).copy(color = CC.textColor())
                )
                Text(
                    text = "$absentCount",
                    style = CC.descriptionTextStyle(context).copy(color = CC.textColor())
                )
            }
        }

    }
}

@Composable
fun AttendanceCard(attendance: Attendance, context: Context, weekIndex: Int) {
    val cardColor = if (attendance.status == "Present") CC.extraColor1() else CC.extraColor2()
    val icon = if (attendance.status == "Present") Icons.Filled.Check else Icons.Filled.Close // Use Filled.Close for Absent
    val tint = if (attendance.status == "Present") Color.Green else Color.Red

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier
                .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally) { // Allow text to take up more space
                Text(
                    text = "Week ${weekIndex + 1}",
                    style = CC.titleTextStyle(context).copy(fontWeight = FontWeight.Bold) // Bolder week text
                )
                Text(
                    text = attendance.status, // Display the actual status (Present/Absent)
                    style = CC.descriptionTextStyle(context).copy(fontSize = 14.sp) // Smaller font for status
                )
            }

            IconButton(onClick = { /* Your click logic here */ }) {
                Icon(icon, contentDescription = attendance.status, tint = tint)
            }
        }
    }
}

