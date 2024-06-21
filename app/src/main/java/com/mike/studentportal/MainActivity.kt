package com.mike.studentportal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import kotlinx.coroutines.launch
import com.mike.studentportal.CommonComponents as CC

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

sealed class Screen(val title: String, val icon: ImageVector) {
    data object Home : Screen("Home", Icons.Filled.Home)
    data object Chat : Screen("Chat", Icons.AutoMirrored.Filled.Chat)
    data object Profile : Screen("Profile", Icons.Filled.Person)
    data object Timetable : Screen("Timetable", Icons.Filled.CalendarToday)
    data object Announcements : Screen("Announcements", Icons.AutoMirrored.Filled.Announcement)
    data object Settings : Screen("Settings", Icons.Filled.Settings)
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalSnapperApi::class)
@Composable
fun MainScreen() {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val screens = listOf(
        Screen.Home,
        Screen.Chat,
        Screen.Profile,
        Screen.Timetable,
        Screen.Announcements,
        Screen.Settings
    )

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .fillMaxWidth(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Hello, Michael", style = CC.descriptionTextStyle, fontSize = 20.sp)
                    IconButton(onClick = { /*TODO*/ }) {
                        Box(
                            modifier = Modifier.size(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Settings",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize()

                            )
                        }
                    }

                }

            },

            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CC.primary
            )
        )
    }, bottomBar = {
        BottomAppBar(
            containerColor = CC.primary
        ) {
            Row(
                modifier = Modifier
                    .background(CC.primary)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val screens = listOf(
                    Screen.Home,
                    Screen.Chat,
                    Screen.Timetable,
                    Screen.Announcements,
                    Screen.Profile,
                    Screen.Settings
                )
                screens.forEachIndexed { index, screen ->
                    Box(
                        modifier = Modifier.background(
                                if (pagerState.currentPage == index) CC.style else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(imageVector = screen.icon, // Use the icon property
                            contentDescription = screen.title,
                            tint = if (pagerState.currentPage == index) CC.secondary else CC.tertiary,
                            modifier = Modifier
                                .padding(5.dp)
                                .size(24.dp) // Adjust icon size as needed
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                })
                    }
                }
            }
        }
    }) { innerPadding ->
        HorizontalPager(
            state = pagerState, count = screens.size, modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (screens[page]) {
                Screen.Home -> HomeScreen()
                Screen.Chat -> ChatScreen()
                Screen.Timetable -> TimetableScreen()
                Screen.Announcements -> AnnouncementsScreen()
                Screen.Profile -> ProfileScreen()
                Screen.Settings -> SettingsScreen()
            }
        }
    }
}


@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD3E0EA)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Settings Screen", style = TextStyle(fontSize = 24.sp, color = Color.Black)
        )
    }
}

@Composable
fun AnnouncementsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD3E0EA)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Announcements Screen", style = TextStyle(fontSize = 24.sp, color = Color.Black)
        )
    }
}


@Composable
fun ChatScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD3E0EA)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Chat Screen", style = TextStyle(fontSize = 24.sp, color = Color.Black)
        )
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3D1D1)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Profile Screen", style = TextStyle(fontSize = 24.sp, color = Color.Black)
        )
    }
}

@Composable
fun TimetableScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC1E1C1)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Timetable Screen", style = TextStyle(fontSize = 24.sp, color = Color.Black)
        )
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
