package com.mike.studentportal

import android.content.Context
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(loading) {
        MyDatabase.fetchCourses { fetchedCourses ->
            courses.clear() // Clear existing courses
            courses.addAll(fetchedCourses) // Add fetched courses
            loading = false // Set loading to false after fetching
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(CC.primary)
            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
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
            Column(
                modifier = Modifier
                    .height(90.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = CC.secondary, trackColor = CC.primary
                )

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
                Column(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = CC.secondary, trackColor = CC.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Loading...", color = CC.textColor
                    )
                }

            } else {
                if (courses.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No courses found", style = CC.descriptionTextStyle(context)
                        )
                    }
                } else {
                    ImageList(courses, context, navController)
                }
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
    }
}


@Composable
fun IconBox(course: Course, navController: NavController, context: Context) {
    val brush = Brush.linearGradient(
        listOf(
        Color(0xff3572EF),
        Color(0xffFF76CE),
    ))


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
                },
            contentAlignment = Alignment.Center
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
    var isLoading by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            AsyncImage(model = "https://www.adobe.com/content/dam/www/us/en/events/overview-page/eventshub_evergreen_opengraph_1200x630_2x.jpg",
                contentDescription = "Event Image",
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 8.dp), // Apply blur effect
                contentScale = ContentScale.Crop,
                onLoading = { isLoading = true },
                onSuccess = { isLoading = false })
            if (isLoading) {
                CircularProgressIndicator(
                    color = CC.secondary, trackColor = CC.primary
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
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
                    Button(
                        onClick = onRegisterClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff810CA8), contentColor = CC.secondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Register", style = CC.descriptionTextStyle(context))
                    }
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
            modifier = Modifier.fillMaxSize().padding(10.dp)
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
                    containerColor = CC.tertiary,
                    contentColor = Color.White
                )
            ) {
                Text("Open Course", style = CC.descriptionTextStyle(context))
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


@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(LocalContext.current, navController = rememberNavController())

}