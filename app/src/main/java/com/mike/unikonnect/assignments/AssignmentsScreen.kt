package com.mike.unikonnect.assignments

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mike.unikonnect.ui.theme.GlobalColors
import com.mike.unikonnect.MyDatabase
import com.mike.unikonnect.chat.ExitScreen
import com.mike.unikonnect.model.Details
import com.mike.unikonnect.model.Assignment
import com.mike.unikonnect.model.Course
import com.mike.unikonnect.model.ScreenTime
import com.mike.unikonnect.homescreen.ColorProgressIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mike.unikonnect.CommonComponents as CC


@Composable
fun AssignmentScreen(navController: NavController, context: Context) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var loading by remember { mutableStateOf(true) }
    val courses = remember { mutableStateListOf<Course>() }
    val startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timeSpent by remember { mutableLongStateOf(0L) }
    val screenID = "SC7"

    LaunchedEffect(Unit) {
        while (true) {
            timeSpent = System.currentTimeMillis() - startTime
            delay(1000)
        }
    }

    ExitScreen(
        context = context,
        screenID = screenID,
        timeSpent = timeSpent
    )

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        LaunchedEffect(key1 = Unit) { // Trigger the effect only once
            while (true) { // Continuous loop
                MyDatabase.fetchCourses { fetchedCourses ->
                    courses.clear() // Clear previous courses
                    courses.addAll(fetchedCourses)
                    loading = false
                }
                delay(10)
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

        val coroutineScope = rememberCoroutineScope()

        if (loading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                ColorProgressIndicator(modifier = Modifier.fillMaxWidth().height(50.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Loading Units", style = CC.descriptionTextStyle(context))

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
                    modifier = Modifier.background(CC.primary()),
                    contentColor = CC.primary(),
                    indicator = indicator,
                    edgePadding = 0.dp,
                    containerColor = CC.primary()
                ) {
                    courses.forEachIndexed { index, course ->

                        Tab(selected = selectedTabIndex == index, onClick = {
                            selectedTabIndex = index
                            coroutineScope.launch {
                                // Load assignments for the selected course
                            }
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
            }

            when (selectedTabIndex) {
                in courses.indices -> {
                    AssignmentsList(courseCode = courses[selectedTabIndex].courseCode, context)
                }
            }
        }

    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AssignmentsList(courseCode: String, context: Context) {
    var assignments by remember { mutableStateOf<List<Assignment>?>(null) }
    LaunchedEffect(key1 = courseCode) { // Trigger when courseCode changes
        while (true) { // Continuous loop
            MyDatabase.getAssignments(courseCode) { fetchedAssignments ->
                assignments = fetchedAssignments
            }
            delay(10) // Wait for 5 seconds
        }
    }

    if (assignments == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            LoadingAssignmentCard()
            LoadingAssignmentCard()
            LoadingAssignmentCard()
        }
    } else {

        LazyColumn {
            if (assignments!!.isEmpty() || courseCode.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No assignments found.", style = CC.descriptionTextStyle(context))
                    }
                }
            }
            items(assignments!!, key = { assignment -> assignment.id }) { assignment -> // Unique key for each item

                AssignmentCard(assignment = assignment, context)

            }
        }
    }
}


@Composable
fun AssignmentCard(assignment: Assignment, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = CC.secondary()),
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(12.dp) // Slightly more rounded corners
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column { // Group assignment name and author
                    Text(
                        text = assignment.name,
                        style = CC.titleTextStyle(context).copy(
                            fontSize = 18.sp,
                        )
                    )
                    Text(
                        text = "By ${Details.firstName.value}",
                        style = CC.descriptionTextStyle(context),
                        color = CC.extraColor1()
                    )
                }

                // Deadline section (add formatting as needed)
                Text(
                    text = "Due: ${assignment.dueDate}", // Assuming assignment has a deadline property
                    style = CC.descriptionTextStyle(context),
                    color =  CC.textColor() // Change color if overdue
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Assignment description (optional expansion)
            var expanded by remember { mutableStateOf(false) }
            Text(
                text = assignment.description,
                style = CC.descriptionTextStyle(context),
                maxLines = if (expanded) Int.MAX_VALUE else 2, // Show 2 lines initially, expandable
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { if (it.hasVisualOverflow && !expanded) expanded = true }, // Auto-expand if needed
                modifier = Modifier.clickable { expanded = !expanded } // Toggle expansion
            )
        }
    }
}

@Composable
fun LoadingAssignmentCard() {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(tween(1000)),
        exit = slideOutVertically(tween(1000))
    ) {


        Card(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(8.dp), colors = CardDefaults.cardColors(
                containerColor = CC.secondary(), contentColor = CC.textColor()
            ), elevation = CardDefaults.elevatedCardElevation(), shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = CC.textColor(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ColorProgressIndicator(modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

@Preview
@Composable
fun AssignmentScreenPreview() {
    //AssignmentScreen(rememberNavController(), LocalContext.current)
    LoadingAssignmentCard()
}
