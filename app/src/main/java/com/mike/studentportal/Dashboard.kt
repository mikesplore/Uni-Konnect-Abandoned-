package com.mike.studentportal


import android.content.Context
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mike.studentportal.ui.theme.RobotoMono
import kotlinx.coroutines.delay
import java.time.LocalTime
import com.mike.studentportal.CommonComponents as CC

object Global{
    val signedInUser: MutableState<String> = mutableStateOf("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(navController: NavController, context: Context){
    fun getGreetingMessage(): String {
        val currentTime = LocalTime.now()
        return when (currentTime.hour) {
            in 5..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
    }
    var expanded  by remember { mutableStateOf(false)}
    val horizontalScrollState = rememberScrollState()
    val boxes = listOf(
        R.drawable.announcement to "date" to "announcements",
        R.drawable.attendance to "Have you updated attendance sheet?" to "RecordAttendance",
        R.drawable.assignment to "assignment" to "assignments",
        R.drawable.timetable to "timetable" to "timetable"
    )

    val totalDuration = 10000
    val delayDuration = 5000L
    val boxCount = boxes.size
    val boxScrollDuration = (totalDuration / boxCount)

    LaunchedEffect(Unit) {
        while (true) {
            for (i in 0 until boxCount) {
                val targetScrollPosition = i * (horizontalScrollState.maxValue / (boxCount - 1))
                horizontalScrollState.animateScrollTo(
                    targetScrollPosition,
                    animationSpec = tween(durationMillis = boxScrollDuration, easing = EaseInOut)
                )
                delay(delayDuration)
            }
            horizontalScrollState.scrollTo(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${getGreetingMessage()}, ${Global.signedInUser.value}", style = CC.titleTextStyle) },
                actions = {
                    Icon(Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = GlobalColors.textColor,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 10.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor,

                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(CC.backbrush)
        ) {
            // Combine the two boxes into one
            Box(
                modifier = Modifier

                    .background(CC.backbrush, RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .requiredHeight(200.dp)
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(10.dp))

                    boxes.forEach { item ->
                        TopBoxes(
                            image = painterResource(id = item.first.first),
                            description = item.first.second,
                            route = item.second,
                            navController = navController,
                            context = context
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                }
                Box(
                    modifier = Modifier
                        .shadow(
                            ambientColor = Color.Red,
                            elevation = 0.dp,
                            spotColor = Color.Blue
                        )
                        .fillMaxWidth(0.9f)
                        .align(Alignment.Center)
                        .height(200.dp)
                        .offset(y = 130.dp)
                        .background(GlobalColors.secondaryColor, RoundedCornerShape(20.dp))
                ){  Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 20.dp,
                            clip = true,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = DefaultShadowColor,
                            spotColor = Color.Transparent
                        )
                        .fillMaxSize()
                ){
                    Column(
                        modifier = Modifier
                            .background(CC.backbrush)
                            .fillMaxSize()
                    ) {
                        Row(modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround) {
                            ActionImages(
                                icon = Icons.Filled.AddAlert,
                                title = "Alerts",
                                route = "menu",
                                navController = navController
                            )
                            ActionImages(
                                icon = Icons.Filled.AssignmentInd,
                                title = "Assignments",
                                route = "menu",
                                navController = navController
                            )
                            ActionImages(
                                icon = Icons.Filled.ChatBubble,
                                title = "Menu",
                                route = "menu",
                                navController = navController
                            )


                        }
                        Row(modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround) {
                            ActionImages(
                                icon = Icons.Filled.Colorize,
                                title = "Colors",
                                route = "menu",
                                navController = navController
                            )
                            ActionImages(
                                icon = Icons.Filled.Attachment,
                                title = "Files",
                                route = "menu",
                                navController = navController
                            )
                            ActionImages(
                                icon = Icons.Filled.Schedule,
                                title = "Timetable",
                                route = "menu",
                                navController = navController
                            )

                        }
                    }}
                }

            }

            //ends here
            Column (
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .weight(1f)
            ){
                Spacer(modifier = Modifier.height(135.dp))
                Text("Upcoming Events",
                    style = CC.descriptionTextStyle,
                    modifier = Modifier.padding(10.dp)
                )
                //upcoming events here
                Column(modifier = Modifier
                    .padding(10.dp)
                    .background(GlobalColors.secondaryColor, RoundedCornerShape(20.dp))
                    .height(190.dp)
                    .border(
                        width = 1.dp,
                        color = GlobalColors.textColor,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .fillMaxWidth()) {
                    //my content
                }
                Text("Quick tasks",
                    style = CC.descriptionTextStyle,
                    modifier = Modifier.padding(10.dp)
                )

                Row(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp)
                            .border(
                                width = 1.dp,
                                color = GlobalColors.textColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .width(150.dp)
                    ) {
                        //my content
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp)
                            .border(
                                width = 1.dp,
                                color = GlobalColors.textColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .width(150.dp)
                    ) {
                        //my content
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp)
                            .border(
                                width = 1.dp,
                                color = GlobalColors.textColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .width(150.dp)
                    ) {
                        //my content here
                    }
                }
                //ends here
            }
        }
    }
}

@Composable
fun ActionImages(icon: ImageVector, title: String, route: String, navController: NavController){
    Column(modifier = Modifier
        .clickable { navController.navigate(route) }
        .fillMaxHeight()
        .width(100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Box(
            modifier = Modifier
                .background(GlobalColors.secondaryColor, CircleShape)
                .size(50.dp),
            contentAlignment = Alignment.Center
        ){
            Icon(imageVector = icon, contentDescription = title, tint = GlobalColors.textColor)
        }

        Text(title, style = CC.descriptionTextStyle)
    }
}


@Composable
fun TopBoxes(
    image: Painter,
    description: String,
    route: String,
    navController: NavController,
    context: Context
) {
    Row(modifier = Modifier
        .clickable {
            navController.navigate(route)
        }
        .background(Color.Transparent, shape = RoundedCornerShape(30.dp))
        .fillMaxHeight()
        .width(350.dp)) {
        Box(modifier = Modifier) {
            LaunchedEffect(Unit) {
                GlobalColors.currentScheme = GlobalColors.loadColorScheme(context)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .fillMaxSize()
            ) {
                Image(
                    painter = image,
                    contentDescription = "sample",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter) // Position at the bottom
                        .background(
                            GlobalColors.secondaryColor.copy(alpha = 0.3f), // Semi-transparent black background
                            shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                        )
                        .padding(16.dp),
                ) {
                    Text(
                        text = description,
                        color = GlobalColors.textColor,
                        style = TextStyle(
                            fontFamily = RobotoMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        ),
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun DashboardPreview(){
    Dashboard(navController = rememberNavController(), LocalContext.current)


}

