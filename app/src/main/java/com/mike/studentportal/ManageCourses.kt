package com.mike.studentportal

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mike.studentportal.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(navController: NavController, context: Context) {
    val courses = remember { mutableStateListOf<Course>() }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(loading) {
        MyDatabase.fetchCourses { fetchedCourses ->
            courses.clear() // Clear existing courses
            courses.addAll(fetchedCourses) // Add fetched courses
            loading = false // Set loading to false after fetching
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Courses", style = CC.titleTextStyle(context)) },
                actions = {
                    IconButton(onClick = { loading = true }) {
                        Icon(
                            Icons.Default.Refresh, "refresh", tint = GlobalColors.textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor
                )
            )
        },
        containerColor = GlobalColors.primaryColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GlobalColors.primaryColor)
                .padding(it)
        ) {
            if (loading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = GlobalColors.secondaryColor, trackColor = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading...", style = CC.descriptionTextStyle(context)
                    )
                }
            } else {
                courses.forEach { course ->
                    Row(
                        modifier = Modifier
                            .background(GlobalColors.secondaryColor, RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp, color = GlobalColors.secondaryColor, shape = RoundedCornerShape(10.dp)
                            )
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = course.courseCode,
                                style = CC.descriptionTextStyle(context),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = course.courseName,
                                style = CC.descriptionTextStyle(context),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = {
                            CourseName.name.value = course.courseName
                            navController.navigate("course/${course.courseCode}")
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "View Course",
                                tint = GlobalColors.textColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun CoursesScreenPreview() {
    CoursesScreen(rememberNavController(), LocalContext.current)
}
