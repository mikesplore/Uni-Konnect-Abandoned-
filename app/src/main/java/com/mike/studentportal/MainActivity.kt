package com.mike.studentportal

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import kotlinx.coroutines.CoroutineScope
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
    data object Event : Screen("Event", Icons.AutoMirrored.Filled.EventNote)
    data object Assignments : Screen("Assignments", Icons.Filled.CalendarToday)
    data object Announcements : Screen("Announcements", Icons.AutoMirrored.Filled.Announcement)
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalSnapperApi::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val screens = listOf(
        Screen.Home,
        Screen.Event,
        Screen.Assignments,
        Screen.Announcements,
    )
    val navController = rememberNavController()

    NavHost(navController, startDestination = "dashboard") {
        composable("login") { LoginScreen(navController, context) }
        composable("dashboard") {Dashboard(navController,pagerState,coroutineScope,screens,context)}
        composable("moredetails") { MoreDetails(context, navController) }
    }
}


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    navController: NavController,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    screens: List<Screen>,
    context: Context
) {
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
                    Text(
                        "Hello, Michael",
                        style = CC.descriptionTextStyle(context),
                        fontSize = 20.sp
                    )
                    IconButton(onClick = { /*TODO*/ }) {
                        Box(
                            modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center
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
            }, colors = TopAppBarDefaults.topAppBarColors(
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
                screens.forEachIndexed { index, screen ->
                    Box(
                        modifier = Modifier.background(
                            if (pagerState.currentPage == index) CC.style else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                    ) {
                        Icon(imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (pagerState.currentPage == index) CC.secondary else CC.tertiary,
                            modifier = Modifier
                                .padding(5.dp)
                                .size(34.dp)
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
                Screen.Home -> HomeScreen(context, navController)
                Screen.Event -> EventScreen(navController, context)
                Screen.Assignments -> AssignmentScreen(navController, context)
                Screen.Announcements -> AnnouncementsScreen(navController, context)

            }
        }
    }
}


@Preview
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
