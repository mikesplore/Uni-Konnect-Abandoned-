package com.mike.studentportal

import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.mike.studentportal.MyDatabase.fetchUserDataByEmail
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.mike.studentportal.CommonComponents as CC
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SignAttendanceScreen(navController: NavController, context: Context) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var loading by remember { mutableStateOf(true) }
    val courses = remember { mutableStateListOf<Course>() }
    val attendanceRecords = remember { mutableStateListOf<Attendance>() }
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var user by remember { mutableStateOf(User()) }
    var currentName by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }
    var currentAdmissionNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        LaunchedEffect(Unit) {
            MyDatabase.fetchCourses { fetchedCourses ->
                courses.clear()
                courses.addAll(fetchedCourses)
                loading = false
            }
        }

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

        val indicator = @Composable { tabPositions: List<TabPosition> ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .height(4.dp)
                    .width(screenWidth / (courses.size.coerceAtLeast(1))) // Avoid division by zero
                    .background(GlobalColors.secondaryColor, CircleShape)
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
                Text("Loading Courses", style = CC.descriptionTextStyle(context))
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
                    containerColor = GlobalColors.primaryColor
                ) {
                    courses.forEachIndexed { index, course ->
                        Tab(selected = selectedTabIndex == index, onClick = {
                            selectedTabIndex = index
                            coroutineScope.launch {
                                MyDatabase.fetchAttendances(currentAdmissionNumber, course.courseCode) { fetchedAttendances ->
                                    attendanceRecords.clear()
                                    attendanceRecords.addAll(fetchedAttendances)
                                }
                            }
                        }, text = {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTabIndex == index) GlobalColors.secondaryColor else GlobalColors.primaryColor,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp), contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = course.courseName,
                                    color = if (selectedTabIndex == index) GlobalColors.textColor else GlobalColors.tertiaryColor,
                                )
                            }
                        }, modifier = Modifier.background(GlobalColors.primaryColor)
                        )
                    }
                }

                if (selectedTabIndex in courses.indices) {
                    AttendanceList(
                        studentID = currentAdmissionNumber,
                        courseCode = courses[selectedTabIndex].courseCode,
                        context,
                        attendanceRecords
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceList(studentID: String, courseCode: String, context: Context, attendanceRecords: List<Attendance>) {
    val coroutineScope = rememberCoroutineScope()
    val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    var loading by remember { mutableStateOf(false) }
    val hasSignedToday by remember {
        mutableStateOf(attendanceRecords.any { it.date == today })
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        if (attendanceRecords.isEmpty()) {
            Text("No attendance records found", style = CC.descriptionTextStyle(context))
        } else {
            LazyColumn {
                items(attendanceRecords) { record ->
                    AttendanceCard(record, context)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    MyDatabase.signAttendance(studentID, courseCode) { success ->
                        if (success) {
                            MyDatabase.fetchAttendances(studentID, courseCode) { fetchedAttendance ->

                            }
                            Toast.makeText(context, "Attendance signed successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to sign attendance", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.secondaryColor),
            enabled = !hasSignedToday // Disable the button if attendance has been signed today
        ) {
            Text("Sign Attendance", style = CC.descriptionTextStyle(context))
        }
    }
}

@Composable
fun AttendanceCard(attendance: Attendance, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Date: ${attendance.date}", style = CC.descriptionTextStyle(context))
            Text("Status: ${attendance.status}", style = CC.descriptionTextStyle(context))
        }
    }
}


