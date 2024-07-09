package com.mike.studentportal

import android.content.Context
import android.graphics.Paint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mike.studentportal.MyDatabase.getAllScreenTimes
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.roundToInt
import com.mike.studentportal.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarGraph(navController: NavController, context: Context) {
    val courses = remember { mutableStateListOf<Course>() }
    var animatedValues by remember { mutableStateOf(emptyList<Float>()) }

    LaunchedEffect(Unit) {
        while (true) {
            MyDatabase.fetchCourses { fetchedCourses ->
                courses.clear()
                courses.addAll(fetchedCourses)
                animatedValues = List(courses.size) { 0f }

                if (courses.isNotEmpty()) {
                    courses.forEachIndexed { index, course ->
                        animatedValues = animatedValues.toMutableList().apply {
                            this[index] = course.visits.toFloat()
                        }
                    }
                }
            }
            delay(3000)
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {navController.navigate("dashboard")}) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null,
                        tint = GlobalColors.textColor)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalColors.primaryColor)
        )
    }, content = {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues = it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(0.9f))  {
                Text("Statistics", style = CC.titleTextStyle(context).copy(fontSize = 40.sp, fontWeight = FontWeight.ExtraBold))
            }

            Text("Course Visits", style = CC.titleTextStyle(context).copy(fontWeight = FontWeight.ExtraBold))
            Text(
                "Number of visits to each course across all users",
                style = CC.descriptionTextStyle(context)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(400.dp), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GlobalColors.primaryColor
                )
            ) {
                BarGraphContent(courses, animatedValues, context)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Screen Time", style = CC.titleTextStyle(context), fontWeight = FontWeight.ExtraBold)
            Text(
                "Time spent across different screens",
                style = CC.descriptionTextStyle(context)
            )
            Spacer(modifier = Modifier.height(10.dp))

            ScreenTimeList(context)
        }
    }, containerColor = GlobalColors.primaryColor
    )
}


@Composable
fun BarGraphContent(courses: List<Course>, animatedValues: List<Float>, context: Context) {
    val maxVisits = animatedValues.maxOrNull()?.roundToInt() ?: 1
    val colors = listOf(GlobalColors.extraColor2, GlobalColors.extraColor1)
    var clickedBarIndex by remember { mutableIntStateOf(-1) }

    val animatedHeights = animatedValues.map { value ->
        animateFloatAsState(
            targetValue = (value / maxVisits) * 1f,
            animationSpec = tween(durationMillis = 3000),
            label = ""
        ).value
    }

    Canvas(modifier = Modifier
        .background(GlobalColors.secondaryColor)
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                val canvasWidth = size.width
                val graphWidth = canvasWidth * 0.6f
                val leftPadding = canvasWidth * 0.1f
                val barWidth = graphWidth / (courses.size * 4)
                val spacing = barWidth / 2
                val clickedIndex = ((offset.x - leftPadding) / (barWidth * 2 + spacing)).toInt()
                if (clickedIndex in courses.indices) {
                    clickedBarIndex = if (clickedBarIndex == clickedIndex) -1 else clickedIndex
                }
            }
        },) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val graphWidth = canvasWidth * 0.7f
        val graphHeight = canvasHeight * 0.8f
        val leftPadding = canvasWidth * 0.1f
        val bottomPadding = canvasHeight * 0.1f

        // Draw Y-axis
        drawLine(
            color = GlobalColors.textColor,
            start = Offset(leftPadding, canvasHeight - bottomPadding),
            end = Offset(leftPadding, bottomPadding),
            strokeWidth = 2f
        )

        // Draw X-axis
        drawLine(
            color = GlobalColors.textColor,
            start = Offset(leftPadding, canvasHeight - bottomPadding),
            end = Offset(canvasWidth - leftPadding / 2, canvasHeight - bottomPadding),
            strokeWidth = 2f
        )

//        // Draw Y-axis title
//        rotate(90f) {
//            drawContext.canvas.nativeCanvas.drawText("Total Visits",
//                canvasHeight / 2,
//                -leftPadding / 2,
//                Paint().apply {
//                    color = GlobalColors.textColor.toArgb()
//                    textAlign = android.graphics.Paint.Align.CENTER
//                    textSize = 14.sp.toPx()
//                })
//        }

        // Draw X-axis title
        drawContext.canvas.nativeCanvas.drawText("Courses",
            canvasWidth / 2,
            canvasHeight - bottomPadding / 4,
            Paint().apply {
                color = GlobalColors.textColor.toArgb()
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 14.sp.toPx()
            })

        // Draw Y-axis labels
        val ySteps = 5
        for (i in 0..ySteps) {
            val y = canvasHeight - bottomPadding - (i.toFloat() / ySteps) * graphHeight
            val labelValue = ((i.toFloat() / ySteps) * maxVisits).roundToInt()
            drawContext.canvas.nativeCanvas.drawText(labelValue.toString(),
                leftPadding - 10.dp.toPx(),
                y,
                Paint().apply {
                    color = GlobalColors.textColor.toArgb()
                    textAlign = android.graphics.Paint.Align.RIGHT
                    textSize = 12.sp.toPx()
                })
        }

        val barWidth = graphWidth / (courses.size * 2)
        val spacing = barWidth / 2

        courses.forEachIndexed { index, course ->
            val animatedHeight = animatedHeights[index] * graphHeight
            var color = colors[index % colors.size]

            // Handle bar click
            val isClicked = clickedBarIndex == index
            if (isClicked) {
                drawContext.canvas.nativeCanvas.drawText(course.courseName,
                    leftPadding + index * (barWidth * 2 + spacing) + barWidth,
                    canvasHeight - bottomPadding - animatedHeight - 20.dp.toPx(),
                    Paint().apply {
                        color = GlobalColors.tertiaryColor
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 14.sp.toPx()
                        alpha = 255 // Full opacity
                    })
            }

            //draw the bars
            drawRoundRect(
                color = color, topLeft = Offset(
                    x = leftPadding + index * (barWidth * 2 + spacing),
                    y = canvasHeight - bottomPadding - animatedHeight
                ), size = Size(barWidth * 2, animatedHeight), cornerRadius = CornerRadius(10f, 10f)
            )

//            //Draw the course code
//            drawContext.canvas.nativeCanvas.drawText(course.courseCode,
//                leftPadding + index * (barWidth * 2 + spacing) + barWidth,
//                canvasHeight - bottomPadding + 15.dp.toPx(),
//                Paint().apply {
//                    val textStyle = TextStyle(
//                        color = GlobalColors.textColor,
//                        fontFamily = FontFamily.SansSerif,
//                        fontSize = 12.sp
//                    )
//                    color = textStyle.color // Extract color from TextStyle
//                    textAlign = android.graphics.Paint.Align.CENTER // Set text alignment
//                    textSize = textStyle.fontSize.toPx() // Extract font size from TextStyle
//
//                })

            // Draw visit count inside the bar
            if (animatedHeight > 20.dp.toPx()) {
                drawContext.canvas.nativeCanvas.drawText(course.visits.toString(),
                    leftPadding + index * (barWidth * 2 + spacing) + barWidth,
                    canvasHeight - bottomPadding - animatedHeight + 15.dp.toPx(),
                    Paint().apply {
                        color = GlobalColors.textColor
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 12.sp.toPx()
                    })
            }
        }
    }
}

@Composable
fun ScreenTimeList(context: Context) {
    val screenTimes = remember { mutableStateListOf<ScreenTime>() }

    // Fetch screen times when the composable enters the composition
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            getAllScreenTimes { fetchedScreenTimes ->
                Log.d("fetched Screen time", "The screen times are: $fetchedScreenTimes")
                screenTimes.clear()
                screenTimes.addAll(fetchedScreenTimes)
                // Sort the list in descending order of screen time
                screenTimes.sortByDescending { it.time }
            }
        }
    }

    // Display the screen times with animations
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (screenTime in screenTimes) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                ScreenTimeItem(screenTime, context)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ScreenTimeItem(screenTime: ScreenTime, context: Context) {
    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = GlobalColors.textColor,
                shape = RoundedCornerShape(10.dp) // Use RectangleShape for a square
            )
            .background(
                GlobalColors.extraColor2.copy(0.5f),
                RoundedCornerShape(10.dp)
            )
            .fillMaxWidth(0.9f)
            .padding(16.dp)
    ) {
        Text(
            text = screenTime.screenName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp), // Add padding between the two Text composables
            style = CC.titleTextStyle(context)
        )
        Text(
            text = convertToHoursMinutesSeconds(screenTime.time),
            modifier = Modifier.fillMaxWidth(),
            style = CC.descriptionTextStyle(context)
        )
    }
}

fun convertToHoursMinutesSeconds(timeInMillis: Long): String {
    val totalSeconds = timeInMillis / 1000

    // Calculate hours, minutes, and seconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    // Build the formatted string based on non-zero values
    val timeComponents = mutableListOf<String>()
    if (hours > 0) {
        timeComponents.add(String.format(Locale.getDefault(), "%02d hours", hours))
    }
    if (minutes > 0) {
        timeComponents.add(String.format(Locale.getDefault(), "%02d minutes", minutes))
    }
    if (seconds > 0 || timeComponents.isEmpty()) {
        timeComponents.add(String.format(Locale.getDefault(), "%02d seconds", seconds))
    }

    // Join the components with a comma
    return timeComponents.joinToString(", ")
}













