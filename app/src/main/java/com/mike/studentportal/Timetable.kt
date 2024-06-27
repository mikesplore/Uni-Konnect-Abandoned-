package com.mike.studentportal

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.mike.studentportal.CommonComponents as CC


@Composable
fun TimetableScreen(navController: NavController, context: Context) {
    var selectedTabIndex by remember { mutableIntStateOf(CC.currentDayID()) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var loading by remember { mutableStateOf(true) }
    val days = remember { mutableStateListOf<Day>() }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        LaunchedEffect(Unit) {
            MyDatabase.getDays { fetchedDays ->
                days.clear()
                days.addAll(fetchedDays ?: emptyList())
                loading = false
            }
        }


        val indicator = @Composable { tabPositions: List<TabPosition> ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .height(4.dp)
                    .width(screenWidth / (days.size.coerceAtLeast(1))) // Avoid division by zero
                    .background(GlobalColors.secondaryColor, CircleShape)
            )
        }

        val coroutineScope = rememberCoroutineScope()

        if (loading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    color = GlobalColors.secondaryColor, trackColor = GlobalColors.textColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Loading Days...Please wait", style = CC.descriptionTextStyle(context))

            }

        } else {
            if (days.isEmpty()) {
                Text("No days found")
            } else {

                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.background(GlobalColors.primaryColor),
                    contentColor = Color.Black,
                    indicator = indicator,
                    edgePadding = 0.dp,
                    containerColor = GlobalColors.primaryColor
                ) {
                    days.forEachIndexed { index, day ->

                        Tab(selected = selectedTabIndex == index, onClick = {
                            selectedTabIndex = index
                            coroutineScope.launch {
                                //load days
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
                                    text = day.name,
                                    color = if (selectedTabIndex == index) GlobalColors.textColor else GlobalColors.tertiaryColor,
                                )
                            }
                        }, modifier = Modifier.background(GlobalColors.primaryColor)
                        )
                    }
                }
            }

            when (selectedTabIndex) {
                in days.indices -> {
                    DayList(dayid = days[selectedTabIndex].id, context)
                }
            }
        }
    }
}

@Composable
fun DayList(dayid: String, context: Context) {
    var timetables by remember { mutableStateOf<List<Timetable>?>(null) }
    LaunchedEffect(dayid) {
        MyDatabase.getTimetable(dayid) { fetchedTimetable ->
            timetables = fetchedTimetable
        }
    }

    if (timetables == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = GlobalColors.secondaryColor, trackColor = GlobalColors.textColor
            )
            Text("Loading Events...Please wait", style = CC.descriptionTextStyle(context))
            Text(
                "If this takes longer, please check your internet connection",
                style = CC.descriptionTextStyle(context),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn {
            if (timetables!!.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No event found.", style = CC.descriptionTextStyle(context))
                    }
                }
            }
            items(timetables!!) { timetable ->
                AnimatedVisibility(
                    visible = true, enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                        animationSpec = tween(500)
                    ), exit = fadeOut(animationSpec = tween(500)) + slideOutVertically(
                        animationSpec = tween(500)
                    )
                ) {
                    TimetableCard(timetable = timetable, context = context)
                }
            }
        }
    }
}


@Composable
fun TimetableCard(
    timetable: Timetable, context: Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), colors = CardDefaults.cardColors(
            containerColor = GlobalColors.secondaryColor, contentColor = GlobalColors.textColor
        ), elevation = CardDefaults.elevatedCardElevation(), shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp, color = GlobalColors.textColor, shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = timetable.unitName,
                    style = CC.titleTextStyle(context).copy(fontSize = 18.sp),
                    color = GlobalColors.textColor
                )

            }

            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Venue",
                        tint = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timetable.venue,
                        style = CC.descriptionTextStyle(context),
                        color = GlobalColors.textColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Lecturer",
                        tint = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timetable.lecturer,
                        style = CC.descriptionTextStyle(context),
                        color = GlobalColors.textColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "Time",
                        tint = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${timetable.startTime} - ${timetable.endTime}",
                        style = CC.descriptionTextStyle(context),
                        color = GlobalColors.textColor
                    )
                }
            }

        }
    }
}


@Preview
@Composable
fun TimetableScreenPreview() {
    TimetableScreen(rememberNavController(), LocalContext.current)

}
