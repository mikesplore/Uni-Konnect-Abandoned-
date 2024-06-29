package com.mike.studentportal

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.mike.studentportal.CommonComponents as CC

object Global {
    val showAlert: MutableState<Boolean> = mutableStateOf(false)
    val edgeToEdge: MutableState<Boolean> = mutableStateOf(true)
    var loading: MutableState<Boolean> = mutableStateOf(true)
}

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_StudentPortal)
        super.onCreate(savedInstanceState)
        if (Global.edgeToEdge.value) {
            enableEdgeToEdge()
        }

        setContent {
            sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
            MainScreen()

        }
        createNotificationChannel(this)
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Permission already granted
                sharedPreferences.edit().putBoolean("NotificationPermissionGranted", true).apply()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().putBoolean("NotificationPermissionGranted", true).apply()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

}


sealed class Screen(
    val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val name: String
) {
    data object Home : Screen(
        Icons.Filled.Home, Icons.Outlined.Home, "Home"
    )

    data object Timetable :
        Screen(Icons.Filled.CalendarToday, Icons.Outlined.CalendarToday, "Timetable")

    data object Assignments : Screen(
        Icons.AutoMirrored.Filled.Assignment, Icons.AutoMirrored.Outlined.Assignment, "Assignments"
    )

    data object Announcements : Screen(
        Icons.Filled.AddAlert, Icons.Outlined.AddAlert, "Announcements"
    )

    data object Attendance : Screen(
        Icons.Filled.Book, Icons.Outlined.Book, "Attendance"
    )

}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        GlobalColors.loadColorScheme(context)
    }
    val screens = listOf(
        Screen.Home, Screen.Announcements, Screen.Assignments, Screen.Timetable, Screen.Attendance
    )
    if (Global.showAlert.value) {
        BasicAlertDialog(
            onDismissRequest = { Global.showAlert.value = false }, modifier = Modifier.background(
                Color.Transparent, RoundedCornerShape(10.dp)
            )
        ) {
            Column(
                modifier = Modifier
                    .background(
                        GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
                    )
                    .padding(24.dp), // Add padding for better visual spacing
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Enable Notifications", style = CC.titleTextStyle(context).copy(
                        fontSize = 18.sp, fontWeight = FontWeight.Bold
                    ), // Make title bolder
                    modifier = Modifier.padding(bottom = 8.dp) // Add spacing below title
                )
                Text(
                    "Please enable notifications to receive realtime updates.",
                    style = CC.descriptionTextStyle(context),
                    modifier = Modifier.padding(bottom = 16.dp) // Add spacing below description
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            // Call the requestNotificationPermission function from the context
                            (context as MainActivity).requestNotificationPermission()
                            Global.showAlert.value = false
                        }, modifier = Modifier.weight(1f), // Make buttons take equal width
                        colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.primaryColor) // Customize button colors
                    ) {
                        Text(
                            "Enable", style = CC.descriptionTextStyle(context)
                        ) // Set text color for contrast
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // Add space between buttons
                    Button(
                        onClick = { Global.showAlert.value = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray) // Customize button colors
                    ) {
                        Text(
                            "Cancel", color = GlobalColors.primaryColor
                        ) // Set text color for contrast
                    }
                }
            }
        }
    }
    val navController = rememberNavController()
    NavHost(navController, startDestination = "settings") {

        composable(
            route = "login",
            enterTransition = {
                fadeIn(animationSpec = tween(1000))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1000))
            }
        ) {
            LoginScreen(navController, context)
        }

        composable(
            route = "passwordreset",
            enterTransition = {
                fadeIn(animationSpec = tween(1000))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1000))
            }
        ) {
            PasswordReset(navController, context)
        }

        composable(
            route = "splashscreen",
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(1000)
                )
            }
        ) {
            SplashScreen(navController, context)
        }

        composable(
            route = "dashboard",
            enterTransition = {
                fadeIn(animationSpec = tween(1000))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1000))
            }
        ) {
            Dashboard(navController, pagerState, coroutineScope, screens, context)
        }

        composable(
            route = "moredetails",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(1000)
                )
            }
        ) {
            MoreDetails(context, navController)
        }

        composable("attendance") {
            SignAttendanceScreen(navController, context)
        }

        composable(
            route = "appearance",
            enterTransition = {
                fadeIn(animationSpec = tween(1000))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1000))
            }
        ) {
            Appearance(navController, context)
        }

        composable("chat") {
            ChatArea(navController, context)
        }

        composable("courses",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(500)
                )
            }

            ) {
            CoursesScreen(navController = navController, context)
        }

        composable("settings",
            enterTransition = {
                fadeIn(animationSpec = tween(1000))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1000))
            }) {
            SettingsScreen(navController, context)
        }

        composable(
            "course/{courseCode}",
            arguments = listOf(navArgument("courseCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseCode = backStackEntry.arguments?.getString("courseCode") ?: ""
            CourseScreen(courseCode = courseCode, context)
        }
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
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        GlobalColors.loadColorScheme(context)
    }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    "${CC.getGreetingMessage()}, ${Details.name.value} 👋",
                    style = CC.titleTextStyle(context),
                    fontSize = 20.sp
                )
            }, actions = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "More Details",
                        tint = GlobalColors.textColor
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(
                            GlobalColors.primaryColor
                        )
                ) {
                    DropdownMenuItem(text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Profile",
                                tint = GlobalColors.textColor
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Settings", style = CC.descriptionTextStyle(context))
                        }
                    }, onClick = {
                        navController.navigate("settings")
                        expanded = false
                    })

                    DropdownMenuItem(text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Chat",
                                tint = GlobalColors.textColor
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Discussion", style = CC.descriptionTextStyle(context))
                        }
                    }, onClick = {
                        navController.navigate("chat")
                        expanded = false
                    })

                    DropdownMenuItem(text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Exit",
                                tint = GlobalColors.textColor
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Sign Out", style = CC.descriptionTextStyle(context))
                        }
                    }, onClick = {
                        MyDatabase.logout
                        navController.navigate("login")
                        expanded = false
                    })
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GlobalColors.primaryColor
            )
            )
        },

        floatingActionButton = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent,
                contentColor = Color.Transparent,

                ) {
                Box(
                    modifier = Modifier
                        .padding(start = 30.dp)
                        .fillMaxWidth()
                        .height(75.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .background(
                                GlobalColors.extraColor1.copy(), RoundedCornerShape(40.dp)
                            )
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        screens.forEachIndexed { index, screen ->
                            val isSelected = pagerState.currentPage == index

                            // Animate color and size changes

                            val iconColor by animateColorAsState(
                                targetValue = if (isSelected) GlobalColors.extraColor2
                                else GlobalColors.primaryColor, label = ""
                            )
                            val iconSize by animateFloatAsState(
                                targetValue = if (isSelected) 45f else 25f, label = ""
                            )
                            val offsetY by animateDpAsState(
                                targetValue = if (isSelected) (-10).dp else 0.dp, label = ""
                            )

                            Box(
                                modifier = Modifier
                                    .height(50.dp)
                                    .offset(y = offsetY),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = "screen.title",
                                    tint = iconColor,
                                    modifier = Modifier
                                        .clickable {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        }
                                        .offset(y = offsetY)
                                        .padding(5.dp)
                                        .size(iconSize.dp))
                            }
                        }
                    }
                }
            }
        }, containerColor = GlobalColors.primaryColor

    ) { innerPadding ->

        HorizontalPager(
            state = pagerState, count = screens.size, modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (screens[page]) {
                Screen.Home -> HomeScreen(context, navController)
                Screen.Assignments -> AssignmentScreen(navController, context)
                Screen.Announcements -> AnnouncementsScreen(navController, context)
                Screen.Timetable -> TimetableScreen(context)
                Screen.Attendance -> SignAttendanceScreen(navController, context)

            }
        }
    }
}


@Preview
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
