package com.mike.studentportal

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.mike.studentportal.MyDatabase.getAnnouncements
import com.mike.studentportal.CommonComponents as CC

data class Images(val link: String, val description: String)

val OnlineImages = listOf(
    Images(
        "https://bs-uploads.toptal.io/blackfish-uploads/components/seo/5923698/og_image/optimized/0712-Bad_Practices_in_Database_Design_-_Are_You_Making_These_Mistakes_Dan_Social-754bc73011e057dc76e55a44a954e0c3.png",
        "CCI 4301"
    ), Images(
        "https://t3.ftcdn.net/jpg/06/69/40/52/360_F_669405248_bH5WPZiAFElWP06vqlPvj2qWcShUR4o8.jpg",
        "CCS 4301"
    ), Images(
        "https://incubator.ucf.edu/wp-content/uploads/2023/07/artificial-intelligence-new-technology-science-futuristic-abstract-human-brain-ai-technology-cpu-central-processor-unit-chipset-big-data-machine-learning-cyber-mind-domination-generative-ai-scaled-1-1500x1000.jpg",
        "CCS 4302"
    ), Images(
        "https://cdn.analyticsvidhya.com/wp-content/uploads/2023/05/human-computer-interaction.webp",
        "CCS 4304"
    ), Images(
        "https://static.javatpoint.com/definition/images/computer-graphics-definition.png",
        "CCS 4305"
    ), Images(
        "https://media.geeksforgeeks.org/wp-content/uploads/20211018204909/communication.jpg",
        "CIT 4307"
    ), Images("https://images.slideplayer.com/25/7665857/slides/slide_11.jpg", "CSE 4301")
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(context: Context, navController: NavController) {
    val courses = remember { mutableStateListOf<Course>() }
    val images = remember { mutableStateOf(OnlineImages) }
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
                .background(CC.primary())
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .height(90.dp)
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {
                        EmptyIconBox(context)
                        EmptyIconBox(context)
                        EmptyIconBox(context)
                        EmptyIconBox(context)
                        EmptyIconBox(context)
                        EmptyIconBox(context)
                        EmptyIconBox(context)


                    }
                } else {
                    AnimatedVisibility(visible = !loading,
                        enter = slideInHorizontally(animationSpec = tween(10000)) + fadeIn(animationSpec = tween(1000)),
                        exit = slideOutHorizontally(animationSpec = tween(10000))+ fadeOut(animationSpec = tween(1000))
                    ) {
                    IconList(courses, navController, context)
                }}
            }
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Popular courses", style = CC.titleTextStyle(context), fontSize = 20.sp
                )
                Text("View All",
                    style = CC.descriptionTextStyle(context),
                    color = CC.extraColor1(),
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
                } else if (courses.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth()
                    ) {
                        EmptyImageBox(context)
                        EmptyImageBox(context)
                        EmptyImageBox(context)
                        EmptyImageBox(context)
                        EmptyImageBox(context)
                    }
                } else {

                    ImageList(courses, context, images.value, navController)
                }


            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Latest Announcement", style = CC.titleTextStyle(context), fontSize = 20.sp)

            }

            AnnouncementItem(context)

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
            }
            TodayTimetable(context)
            Spacer(modifier = Modifier.height(50.dp))

        }

}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IconBox(
    course: Course,
    navController: NavController,
    context: Context,
    modifier: Modifier = Modifier,
    animationDelay: Int = 0
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, label = ""
    )
    val offsetX by animateDpAsState(
        targetValue = 0.dp, // Initially off-screen
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = animationDelay,
            easing = LinearOutSlowInEasing
        ), label = ""
    )

    Column(
        modifier = modifier
            .padding(start = 10.dp)
            .offset(x = offsetX), // Apply offset for sliding animation
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier

                .background(Color.Transparent, CircleShape)
                .size(60.dp)
                .border(1.dp, CC.tertiary(), shape = CircleShape)
                .clickable {
                    CourseName.name.value = course.courseName
                    navController.navigate("course/${course.courseCode}")
                }, contentAlignment = Alignment.Center
        ) {
            MyBackground(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
            Icon(
                imageVector = Icons.Filled.School,
                tint = CC.extraColor1(),
                contentDescription = course.courseName,
                modifier = Modifier.size(60.dp / 2)
            )
        }
        Text(
            text = if (course.courseName.length > 10) {
                course.courseName.substring(0, 10) + "..." // Truncate and add ellipsis
            } else {
                course.courseName // Display full name if less than or equal to 10 characters
            },
            style = CC.descriptionTextStyle(context = context),
            color = CC.textColor().copy(0.7f),
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
                .background(Color.Transparent, CircleShape)
                .size(60.dp)
                .border(1.dp, CC.tertiary(), shape = CircleShape)
        ) {
            ColorProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )

        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier
                .width(60.dp)
                .height(15.dp)
        ) {
            ColorProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
            )
        }
    }
}

@Composable
fun EmptyIconBox(context: Context) {
    Column(
        modifier = Modifier.padding(start = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent, CircleShape)
                .size(60.dp)
                .border(1.dp, CC.tertiary(), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🕳️", style = CC.descriptionTextStyle(context))
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier
                .width(60.dp)
                .height(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("🕳️", style = CC.descriptionTextStyle(context))
        }
    }
}

@Composable
fun IconList(courses: List<Course>, navController: NavController, context: Context) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        items(courses) { course ->
            IconBox(course, navController, context) // Pass course, navController to IconBox
        }
    }

}

@Composable
fun TodayTimetable(context: Context) {
    var timetables by remember { mutableStateOf<List<Timetable>?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(loading) {
        MyDatabase.getCurrentDayTimetable(CC.currentDay()) { timetable ->
            timetables = timetable
        }
        loading = false

    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(CC.primary())
                    .border(
                        1.dp, CC.tertiary(), shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                ColorProgressIndicator(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxSize(),
                )
            }
        } else {
            if (timetables.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(CC.primary())
                        .border(
                            1.dp, CC.tertiary(), shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp), contentAlignment = Alignment.Center
                ) {
                    Text("No events", style = CC.titleTextStyle(context))
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .border(
                            1.dp, CC.tertiary(), shape = RoundedCornerShape(16.dp)
                        )
                        .background(CC.primary())
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(timetables!!) { timetable ->

                        Column(
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    CC.tertiary(),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .width(350.dp)
                                .height(200.dp)
                                .background(CC.primary(), RoundedCornerShape(16.dp))
                                .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally
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
                                    tint = CC.secondary()
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
                                    tint = CC.secondary()
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
                        Spacer(modifier = Modifier.width(10.dp))

                    }
                }
            }
        }
    }
}


@Composable
fun ImageBox(course: Course, image: Images, context: Context, navController: NavController) {

    LaunchedEffect(course.courseCode) {

    }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(250.dp)
            .height(260.dp)
            .border(1.dp, CC.secondary(), shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(CC.primary(), RoundedCornerShape(16.dp))
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = image.link),
                    contentDescription = image.description,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp, 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .background(
                        CC.secondary().copy(0.5f),
                        RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
                    )
                    .fillMaxSize(1f), verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = course.courseCode,
                    style = CC.descriptionTextStyle(context),
                    textAlign = TextAlign.Left,
                    color = CC.extraColor2(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, end = 5.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = course.courseName,
                    style = CC.titleTextStyle(context),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, end = 5.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val visits = if(course.visits==0) "Never visited" else if(course.visits==1) "Visited once" else "Visited ${course.visits} times"
                    Text(
                        text = visits,
                        style = CC.descriptionTextStyle(context),
                        color = CC.textColor().copy(0.5f),
                        textAlign = TextAlign.Left,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        val updatedCourse = Course(
                            courseCode = course.courseCode,
                            courseName = course.courseName,
                            visits = course.visits + 1
                        )
                        // Save updated last date to preferences
                        MyDatabase.writeCourse(updatedCourse) {
                            Log.d("Updated Course:", "Updated Visit is ${updatedCourse.visits}")
                        }
                        CourseName.name.value = course.courseName
                        navController.navigate("course/${course.courseCode}")
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Visit",
                            tint = CC.extraColor1()
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ImageList(
    courses: List<Course>, context: Context, images: List<Images>, navController: NavController
) {
    val sortedCourses =
        courses.sortedByDescending { it.visits } // Sort by lastDate in descending order

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(sortedCourses) { course -> // Use the sorted list
            val matchedImage =
                images.find { it.description.equals(course.courseCode, ignoreCase = true) }
            if (matchedImage != null) {
                ImageBox(course, matchedImage, context, navController)
            } else {
                // Handle case where no matching image is found (optional)
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(250.dp)
                        .height(250.dp)
                        .border(
                            1.dp, CC.secondary(), shape = RoundedCornerShape(16.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    Text("No image available")
                }
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
            .border(1.dp, CC.secondary(), shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            ColorProgressIndicator(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp, 16.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
            ) {
                ColorProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp))
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun EmptyImageBox(context: Context) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(250.dp)
            .height(250.dp)
            .border(1.dp, CC.secondary(), shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {

        Text("🕳️", style = CC.descriptionTextStyle(context))

    }
}

@Composable
fun ColorProgressIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500, easing = LinearEasing
            ), repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        CC.secondary().copy(0.1f),
                        CC.secondary().copy(0.3f),
                        CC.secondary().copy(0.5f),
                        CC.secondary().copy(0.7f),
                        CC.secondary().copy(0.9f),
                        CC.secondary().copy(0.7f),
                        CC.secondary().copy(0.5f),
                        CC.secondary().copy(0.3f),
                        CC.secondary().copy(0.1f),
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent


                    ),
                    start = Offset(offsetX * 1000f - 500f, 0f),
                    end = Offset(offsetX * 1000f + 500f, 0f)
                )
            )
    )
}


@Composable
fun MyBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val colors = listOf(
        CC.secondary(),
        CC.primary(),
        CC.secondary(),
        CC.primary()
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = colors,
                    center = Offset.Zero,
                    radius = 800f,  // Increase the radius for a smoother gradient transition
                    tileMode = TileMode.Mirror
                )
            )
    )
}

@Composable
fun Background(context: Context) {
    val icons = listOf(
        Icons.Outlined.Home,
        Icons.AutoMirrored.Outlined.Assignment,
        Icons.Outlined.School,
        Icons.Outlined.AccountCircle,
        Icons.Outlined.BorderColor,
        Icons.Outlined.Book,
    )
    LaunchedEffect(Unit) {
        GlobalColors.loadColorScheme(context)

    }
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
            .background(CC.primary())
            .padding(10.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(repeatedIcons) { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CC.secondary().copy(0.5f),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun AnnouncementItem(context: Context) {
    var loading by remember { mutableStateOf(true) }
    val announcements = remember { mutableStateListOf<Announcement>() }
    LaunchedEffect(Unit) {
        GlobalColors.loadColorScheme(context)
        getAnnouncements { fetchedAnnouncements ->
            announcements.addAll(fetchedAnnouncements ?: emptyList())
            loading = false
        }
    }
    Column(
        modifier = Modifier
            .background(Color.Transparent, RoundedCornerShape(16.dp))
            .height(200.dp)
            .fillMaxWidth(0.9f)
            .border(
                width = 1.dp, color = CC.tertiary(), shape = RoundedCornerShape(10.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (loading) {
            Column(
                modifier = Modifier
                    .background(Color.Transparent, RoundedCornerShape(16.dp))
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ColorProgressIndicator(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxSize(),
                )
            }

        } else if (announcements.isNotEmpty()) {
            val firstAnnouncement = announcements[announcements.lastIndex]
            Box(
                modifier = Modifier.background(
                    CC.primary(), RoundedCornerShape(10.dp)
                )
            ) {
                MyBackground(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // Title row
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            firstAnnouncement.title,
                            style = CC.titleTextStyle(context),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // Content column with vertical scrolling
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .fillMaxHeight(1f)
                            .background(
                                CC.primary().copy(alpha = 0.5f),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        // Author and date row
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween  // Space items evenly across the row
                        ) {
                            Text(
                                firstAnnouncement.author,
                                style = CC.descriptionTextStyle(context),
                                // Adding color for better visual separation
                            )
                            Text(
                                firstAnnouncement.date,
                                style = CC.descriptionTextStyle(context),
                            )
                        }

                        // Description column
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Start  // Align text to the start (left)
                        ) {
                            Text(
                                firstAnnouncement.description,
                                style = CC.descriptionTextStyle(context),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(8.dp)  // Adding padding around the text
                            )
                        }
                    }
                }
            }

        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No announcements available", style = CC.descriptionTextStyle(context)
                )
            }

        }
    }
}




@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(LocalContext.current, rememberNavController())

}