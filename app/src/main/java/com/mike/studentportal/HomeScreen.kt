package com.mike.studentportal

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.mike.studentportal.CommonComponents as CC


@Composable
fun HomeScreen(context: Context, navController: NavController) {
    val courses = remember { mutableStateListOf<Course>() }
    val announcements by remember { mutableStateOf(emptyList<Announcement>()) }
    var loading by remember { mutableStateOf(true) }
    val events by remember { mutableStateOf<List<Event>?>(null) }


    LaunchedEffect(loading) {
        MyDatabase.fetchCourses { fetchedCourses ->
            courses.clear() // Clear existing courses
            courses.addAll(fetchedCourses) // Add fetched courses
            loading = false // Set loading to false after fetching
        }


    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Background()
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(10.dp)
                .width(350.dp)
        ) {
            SearchTextField()
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (loading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                LoadingIconBox()
                LoadingIconBox()
                LoadingIconBox()
                LoadingIconBox()
                LoadingIconBox()
                LoadingIconBox()
            }


        } else {
            if (courses.isEmpty()) {
                Column(
                    modifier = Modifier
                        .height(90.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No courses found", style = CC.descriptionTextStyle(context)
                    )
                }
            } else {
                IconList(courses, navController, context)
            }
        }
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recently accessed courses", style = CC.titleTextStyle(context), fontSize = 20.sp)
            Text("View All",
                style = CC.descriptionTextStyle(context),
                fontSize = 15.sp,
                modifier = Modifier.clickable {
                    navController.navigate("courses")
                })
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    LoadingImageBox()
                    LoadingImageBox()
                    LoadingImageBox()
                    LoadingImageBox()
                    LoadingImageBox()
                    LoadingImageBox()
                }
            } else {

                ImageList(courses, context, navController)
            }


        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Upcoming events", style = CC.titleTextStyle(context), fontSize = 20.sp)
            Text("View All", style = CC.descriptionTextStyle(context), fontSize = 15.sp)
        }
        Row(
            modifier = Modifier
                .border(
                    1.dp, CC.tertiary, shape = RoundedCornerShape(16.dp)
                )
                .height(230.dp)
                .fillMaxWidth(0.9f)
        ) {
            EventCard(
                title = "Event 1",
                dateTime = "10:00 AM - 12:00 PM",
                location = "Location 1",
                description = "Description 1",
                onRegisterClick = {},
                context
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${CC.currentDay()} Timetable",
                style = CC.titleTextStyle(context),
                fontSize = 20.sp
            )
            Text("View All", style = CC.descriptionTextStyle(context), fontSize = 15.sp)
        }
        TodayTimetable(context)
    }
}
}


@Composable
fun IconBox(course: Course, navController: NavController, context: Context) {
    val brush = Brush.linearGradient(
        listOf(
            GlobalColors.extraColor2, GlobalColors.extraColor1
        )
    )


    Column(
        modifier = Modifier.padding(start = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(brush, RoundedCornerShape(8.dp))
                .size(60.dp)
                .border(1.dp, CC.tertiary, shape = RoundedCornerShape(8.dp))
                .clickable {
                    CourseName.name.value = course.courseName
                    navController.navigate("course/${course.courseCode}")
                }, contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.School,
                tint = CC.secondary,
                contentDescription = course.courseName,
                modifier = Modifier.size(60.dp / 2)
            )
        }
        Text(
            text = course.courseName,
            style = CC.descriptionTextStyle(context = context),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Composable
fun LoadingIconBox() {
    Column(
        modifier = Modifier.padding(start = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent, RoundedCornerShape(8.dp))
                .size(60.dp)
                .border(1.dp, CC.tertiary, shape = RoundedCornerShape(8.dp))
        ) {
            ColorProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
            )

        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(modifier = Modifier
            .width(60.dp)
            .height(15.dp)) {
            ColorProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
            )
        }
    }
}

@Composable
fun IconList(courses: List<Course>, navController: NavController, context: Context) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(courses) { course ->
            IconBox(course, navController, context) // Pass course, navController to IconBox
        }
    }
}


@Composable
fun EventCard(
    title: String,
    dateTime: String,
    location: String,
    description: String,
    onRegisterClick: () -> Unit,
    context: Context

) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlobalColors.primaryColor
        )
    ) {

        if (Global.loading.value) {
            ColorProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title, style = CC.titleTextStyle(context), textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Event Date and Time",
                        tint = CC.secondary
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = dateTime, style = CC.descriptionTextStyle(context)
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Event Location",
                        tint = CC.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = location, style = CC.descriptionTextStyle(context)
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = description,
                    style = CC.descriptionTextStyle(context),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    }
}

@Composable
fun TodayTimetable(context: Context) {
    var timetables by remember { mutableStateOf<List<Timetable>?>(null) }

    LaunchedEffect(Global.loading.value) {
        MyDatabase.getCurrentDayTimetable(CC.currentDay(), onTimetableFetched = {
            timetables = it
            Global.loading.value = false
        })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(5.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        if (Global.loading.value) {
            ColorProgressIndicator(modifier = Modifier
                .fillMaxSize()
                .height(100.dp))

        } else {
            val timetable = timetables?.firstOrNull()

            if (timetable == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No events", style = CC.titleTextStyle(context))
                }
            } else {
                Column(
                    modifier = Modifier
                        .border(
                            1.dp, CC.tertiary, shape = RoundedCornerShape(16.dp)
                        )
                        .background(GlobalColors.primaryColor)
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = timetable.unitName,
                        style = CC.titleTextStyle(context),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Event Date and Time",
                            tint = CC.secondary
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${timetable.startTime} - ${timetable.endTime}",
                            style = CC.descriptionTextStyle(context)
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Event Location",
                            tint = CC.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = timetable.venue, style = CC.descriptionTextStyle(context)
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = timetable.lecturer,
                        style = CC.descriptionTextStyle(context),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun SearchTextField() {
    var searchText by remember { mutableStateOf("") }

    OutlinedTextField(value = searchText,
        onValueChange = { searchText = it },
        label = { Text("Search") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search, contentDescription = "Search Icon"
            )
        },
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CC.primary,
            unfocusedContainerColor = CC.primary,
            focusedIndicatorColor = CC.tertiary,
            unfocusedIndicatorColor = CC.tertiary,
            focusedTextColor = CC.textColor,
            unfocusedTextColor = CC.textColor,
            cursorColor = CC.textColor,
            focusedLabelColor = CC.textColor,
            unfocusedLabelColor = CC.secondary
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = TextStyle(
            color = CC.textColor,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        singleLine = true
    )
}


@Composable
fun ImageBox(course: Course, context: Context, navController: NavController) {
    Box(
        modifier = Modifier

            .padding(8.dp)
            .width(250.dp)
            .height(250.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
            .clickable { navController.navigate(course.courseCode) },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(GlobalColors.extraColor2, RoundedCornerShape(16.dp))
                .fillMaxSize()
                .padding(10.dp)
        ) {
            AsyncImage(
                model = "https://tipa.in/wp-content/uploads/2021/05/Online-courses.jpg",
                contentDescription = course.courseName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = course.courseName,
                style = CC.titleTextStyle(context).copy(fontSize = 18.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    CourseName.name.value = course.courseName
                    navController.navigate("course/${course.courseCode}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GlobalColors.extraColor1, contentColor = Color.White
                )
            ) {
                Text("Open Course", style = CC.descriptionTextStyle(context))
            }
        }
    }
}


@Composable
fun LoadingImageBox() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(250.dp)
            .height(250.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(GlobalColors.primaryColor, RoundedCornerShape(16.dp))
                .fillMaxSize()
                .padding(10.dp)
        ) {
            ColorProgressIndicator(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .height(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                ColorProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(5.dp))
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .height(50.dp),

                ) {
                ColorProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}


@Composable
fun ImageList(courses: List<Course>, context: Context, navController: NavController) {
    val sortedCourses =
        courses.sortedByDescending { it.lastDate } // Sort by lastDate in descending order

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(sortedCourses) { course -> // Use the sorted list
            ImageBox(course, context, navController)
        }
    }
}


@Composable
fun ColorProgressIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier.background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        GlobalColors.secondaryColor,
                        GlobalColors.primaryColor,
                        GlobalColors.secondaryColor
                    ), start = Offset(offsetX * 1000f - 500f, 0f), // Adjust offset for movement
                    end = Offset(offsetX * 1000f + 500f, 0f)
                )
            )
    )
}


@Composable
fun Background() {
    val icons = listOf(
        Icons.Outlined.Home,
        Icons.AutoMirrored.Outlined.Assignment,
        Icons.Outlined.School,
        Icons.Outlined.AccountCircle,
        Icons.Outlined.BorderColor,
        Icons.Outlined.Book,
    )

    // Calculate the number of repetitions needed to fill the screen
    val repetitions = 1000 // Adjust this value as needed

    val repeatedIcons = mutableListOf<ImageVector>()
    repeat(repetitions) {
        repeatedIcons.addAll(icons.shuffled())
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(10),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(10.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(repeatedIcons) { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GlobalColors.secondaryColor.copy(0.7f),
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}


@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(LocalContext.current, rememberNavController())


}