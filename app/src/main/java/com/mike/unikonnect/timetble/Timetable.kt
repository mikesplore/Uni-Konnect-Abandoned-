package com.mike.unikonnect.timetble

import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mike.unikonnect.model.Day
import com.mike.unikonnect.model.ScreenTime
import com.mike.unikonnect.model.Timetable
import com.mike.unikonnect.ui.theme.GlobalColors
import com.mike.unikonnect.MyDatabase
import com.mike.unikonnect.chat.ExitScreen
import kotlinx.coroutines.delay
import com.mike.unikonnect.CommonComponents as CC


@Composable
fun TimetableScreen(context: Context) {
    var selectedTabIndex by remember { mutableIntStateOf(CC.currentDayID()-1) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var loading by remember { mutableStateOf(true) }
    val days = remember { mutableStateListOf<Day>() }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        Box(
            modifier = Modifier
                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                .height(4.dp)
                .width(screenWidth / (days.size.coerceAtLeast(1))) // Avoid division by zero
                .background(CC.secondary(), CircleShape)
        )
    }
    val startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timeSpent by remember { mutableLongStateOf(0L) }
    val screenID = "SC4"

    LaunchedEffect(Unit) {
        while (true) {
            timeSpent = System.currentTimeMillis() - startTime
            delay(1000) // Update every second (adjust as needed)
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

        LaunchedEffect(Unit) {
            MyDatabase.getDays { fetchedDays ->
                days.clear()
                days.addAll(fetchedDays ?: emptyList())
                loading = false
            }
        }

        if (loading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    color = CC.secondary(), trackColor = CC.textColor()
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Loading Days...Please wait", style = CC.descriptionTextStyle(context))

            }

        } else {
            if (days.isEmpty()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("No days found", style = CC.descriptionTextStyle(context))
                }

            } else {

                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.background(CC.primary()),
                    contentColor = Color.Black,
                    indicator = indicator,
                    edgePadding = 0.dp,
                    containerColor = CC.primary()
                ) {
                    days.forEachIndexed { index, day ->

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
                                    text = day.name,
                                    color = if (selectedTabIndex == index) CC.textColor() else CC.tertiary(),
                                )
                            }
                        }, modifier = Modifier.background(CC.primary())
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
                color = CC.secondary(), trackColor = CC.textColor()
            )
            Text("Loading Events...", style = CC.descriptionTextStyle(context))
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
            containerColor = CC.secondary(), contentColor = CC.textColor()
        ), elevation = CardDefaults.elevatedCardElevation(), shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp, color = CC.textColor(), shape = RoundedCornerShape(8.dp)
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
                    color = CC.textColor()
                )

            }

            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Venue",
                        tint = CC.textColor()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timetable.venue,
                        style = CC.descriptionTextStyle(context),
                        color = CC.textColor()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Lecturer",
                        tint = CC.textColor()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timetable.lecturer,
                        style = CC.descriptionTextStyle(context),
                        color = CC.textColor()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "Time",
                        tint = CC.textColor()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${timetable.startTime} - ${timetable.endTime}",
                        style = CC.descriptionTextStyle(context),
                        color = CC.textColor()
                    )
                }
            }

        }
    }
}


@Preview
@Composable
fun TimetableScreenPreview() {
    TimetableScreen(LocalContext.current)

}
