package com.mike.studentportal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerDefaults
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.mike.studentportal.MyDatabase.fetchUserDataByEmail
import com.mike.studentportal.MyDatabase.getUpdate
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import com.mike.studentportal.CommonComponents as CC

object Global {
    val showAlert: MutableState<Boolean> = mutableStateOf(false)
    val edgeToEdge: MutableState<Boolean> = mutableStateOf(true)
    var loading: MutableState<Boolean> = mutableStateOf(true)
    var isAuthenticationSuccessful: MutableState<Boolean> = mutableStateOf(true)
}

class MainActivity : AppCompatActivity() {
    val promptManager by lazy {
        BiometricPromptManager(this)
    }

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_StudentPortal)
        super.onCreate(savedInstanceState)
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true).build()
        val chatFetchRequest =
            PeriodicWorkRequestBuilder<ChatFetchWorker>(1, TimeUnit.SECONDS).setConstraints(
                constraints
            ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ChatFetchWorker", ExistingPeriodicWorkPolicy.KEEP, chatFetchRequest
        )
        if (Global.edgeToEdge.value) {
            enableEdgeToEdge()
        }

        setContent {
            sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
            MainScreen(this)


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
                Global.showAlert.value = false
            } else {
                // Permission already granted
                Global.showAlert.value = true
                sharedPreferences.edit().putBoolean("NotificationPermissionGranted", true).apply()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Global.showAlert.value = true
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().putBoolean("NotificationPermissionGranted", true).apply()
            } else {
                Global.showAlert.value = false
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
fun MainScreen(mainActivity: MainActivity) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    var update by remember { mutableStateOf(false) }
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName

    LaunchedEffect(Unit) {
        while (true) {
            GlobalColors.loadColorScheme(context) // Assuming this is necessary for each check
            getUpdate { localUpdate ->
                if (localUpdate != null) {
                    Log.d("Package Update", "New version available: $localUpdate")
                    if (localUpdate.id != versionName) {

                        update = true
                    }
                } else {
                    Log.d("Package Update", "No information found regarding the update")
                }
            }
            delay(60000) // Wait for 60 seconds
        }
    }
    val screens = listOf(
        Screen.Home, Screen.Announcements, Screen.Assignments, Screen.Timetable, Screen.Attendance
    )

    if (update) {
        BasicAlertDialog(
            onDismissRequest = { update = false }, modifier = Modifier.background(
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
                    "New Update available!", style = CC.titleTextStyle(context).copy(
                        fontSize = 18.sp, fontWeight = FontWeight.Bold
                    ), // Make title bolder
                    modifier = Modifier.padding(bottom = 8.dp) // Add spacing below title
                )
                Text(
                    "New version of this app is available. " + "The update contains bug fixes and addresses some of user-reported issues..",
                    style = CC.descriptionTextStyle(context),
                    modifier = Modifier.padding(bottom = 16.dp) // Add spacing below description
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/mikesplore/Student-Portal/blob/main/app/release/app-release.apk")
                            )
                            context.startActivity(intent)
                            update = false

                        }, modifier = Modifier.weight(1f), // Make buttons take equal width
                        colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.primaryColor)
                    ) {
                        Text(
                            "Update", style = CC.descriptionTextStyle(context)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { update = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text(
                            "Cancel", color = GlobalColors.primaryColor
                        )
                    }
                }
            }
        }
    }
    val navController = rememberNavController()
    NavHost(navController, startDestination = "settings") {

        composable(route = "login", enterTransition = {
            fadeIn(animationSpec = tween(1000))
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000))
        }) {
            LoginScreen(navController, context)
        }

        composable(route = "passwordreset", enterTransition = {
            fadeIn(animationSpec = tween(1000))
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000))
        }) {
            PasswordReset(navController, context)
        }

        composable(route = "splashscreen", exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(1000)
            )
        }) {
            SplashScreen(navController, context)
        }

        composable(route = "dashboard", enterTransition = {
            fadeIn(animationSpec = tween(1000))
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000))
        }) {
            Dashboard(navController, pagerState, coroutineScope, screens, context)
        }

        composable(route = "moredetails", enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)
            )
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000))
        }) {
            MoreDetails(context, navController)
        }

        composable("profile", enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(1000)
            )
        }, exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(1000)
            )
        }) {
            ProfileScreen(navController, context)
        }

        composable("attendance", enterTransition = {
            fadeIn(animationSpec = tween(1000))
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000))
        }) {
            SignAttendanceScreen(navController, context)
        }

        composable(route = "appearance", enterTransition = {
            fadeIn(animationSpec = tween(1000))
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000))
        }) {
            Appearance(navController, context)
        }

        composable("chat", enterTransition = {
            fadeIn(animationSpec = tween(1000))
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000))
        }) {
            ChatScreen(navController, context)
        }

        composable("courses", enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(500)
            )
        }, exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(500)
            )
        }

        ) {
            CoursesScreen(navController = navController, context)
        }

        composable("settings", enterTransition = {
            fadeIn(animationSpec = tween(500))
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000))
        }) {
            Settings(navController, context, mainActivity)
        }

        composable("statistics") {
            BarGraph(navController, context)
        }

        composable("users", enterTransition = {
            fadeIn(animationSpec = tween(500))
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000))
        }) {
            ParticipantsScreen(navController, context)
        }

        composable("chat/{userId}", enterTransition = {
            fadeIn(animationSpec = tween(1000)) + slideInVertically(animationSpec = tween(1000)) { initialState -> initialState }
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000)) + slideOutVertically(animationSpec = tween(1000)) { finalState -> finalState }
        }, arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            UserChatScreen(
                navController,
                LocalContext.current,
                backStackEntry.arguments?.getString("userId") ?: ""
            )
        }

        composable("course/{courseCode}",
            arguments = listOf(navArgument("courseCode") { type = NavType.StringType }),
            enterTransition = { fadeIn(animationSpec = tween(1000)) },
            exitTransition = { fadeOut(animationSpec = tween(1000)) }) { backStackEntry ->
            val courseCode = backStackEntry.arguments?.getString("courseCode") ?: ""
            CourseScreen(courseCode = courseCode, context)
        }
    }

}


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalSnapperApi::class)
@Composable
fun Dashboard(
    navController: NavController,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    screens: List<Screen>,
    context: Context
) {
    var user by remember { mutableStateOf(User()) }
    val auth = FirebaseAuth.getInstance()
    var currentName by remember { mutableStateOf("") }
    val startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timeSpent by remember { mutableLongStateOf(0L) }
    val screenID = "SC10"
    val screenName = "Dashboard Screen"

    LaunchedEffect(Unit) {
        while (true) {
            timeSpent = System.currentTimeMillis() - startTime
            delay(1000) // Update every second (adjust as needed)
        }
    }

    DisposableEffect(Unit) {
        GlobalColors.loadColorScheme(context)
        MyDatabase.writeScren(courseScreen = Screens(screenID, screenName)) {}
        onDispose {
            // Fetch existing screen time
            MyDatabase.getScreenTime(screenID) { existingScreenTime ->
                val totalScreenTime = if (existingScreenTime != null) {
                    Log.d("Screen Time", "Retrieved Screen time: $existingScreenTime")
                    existingScreenTime.time + timeSpent
                } else {
                    timeSpent
                }

                // Create a new ScreenTime object
                val screentime = ScreenTime(
                    id = screenID, screenName = screenName, time = totalScreenTime
                )

                // Save the updated screen time
                MyDatabase.saveScreenTime(screenTime = screentime, onSuccess = {
                    Log.d("Screen Time", "Saved $totalScreenTime to the database")
                }, onFailure = {
                    Log.d("Screen Time", "Failed to save $totalScreenTime to the database")
                })
            }
        }
    }
    val currentUser = auth.currentUser
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var signInMethod by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) { // Use a stable key
        while (true) {
            delay(10L) // Delay for 10 seconds
            auth.currentUser?.email?.let { email ->
                fetchUserDataByEmail(email) { fetchedUser ->
                    fetchedUser?.let {
                        user = it
                        currentName = it.firstName
                        MyDatabase.fetchPreferences(user.id) { preferences ->
                            Log.d(
                                "Shared Preferences",
                                "Retrieved preferences for student ID: ${user.id}: $preferences"
                            )
                            preferences?.let {
                                selectedImageUri = Uri.parse(preferences.profileImageLink)
                            }
                        }
                    }
                }
            }
        }
    }



    LaunchedEffect(key1 = Unit) {
        if (currentUser != null) {
            for (userInfo in currentUser.providerData) {
                when (userInfo.providerId) {
                    "password" -> {
                        // User signed in with email and password
                        signInMethod = "password"
                        Log.d("Auth", "User signed in with email/password")
                    }

                    "google.com" -> {
                        // User signed in with Google
                        signInMethod = "google.com"
                        Log.d("Auth", "User signed in with Google")
                    }

                    "github.com" -> {
                        // User signed in with GitHub
                        signInMethod = "github.com"
                        Log.d("Auth", "User signed in with GitHub")
                    }
                }
            }
        }
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.8f)
                .background(GlobalColors.secondaryColor, RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = Modifier
                    .background(GlobalColors.extraColor2)
                    .height(200.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clickable { navController.navigate("profile") }
                        .size(150.dp)
                        .padding(20.dp)
                        .background(GlobalColors.secondaryColor, CircleShape)
                        .border(1.dp, GlobalColors.primaryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentUser?.photoUrl != null && currentUser.photoUrl.toString()
                            .isNotEmpty()
                    ) {
                        AsyncImage(
                            model = currentUser.photoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else if (selectedImageUri != null && signInMethod == "password") {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Picture",
                            tint = Color.Gray,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        user.firstName + " " + user.lastName,
                        style = CC.descriptionTextStyle(context)
                            .copy(fontWeight = FontWeight.ExtraBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable {
                        navController.navigate("chat")
                        scope.launch { drawerState.close() }
                    }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "Chat",
                        tint = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Discussion", style = CC.descriptionTextStyle(context))
                }

                Row(modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable {
                        navController.navigate("statistics")
                        scope.launch { drawerState.close() }
                    }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = "Statistics",
                        tint = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Statistics", style = CC.descriptionTextStyle(context))
                }

                Row(modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable {
                        navController.navigate("courses")
                        scope.launch { drawerState.close() }
                    }
                    .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Collections,
                        contentDescription = "Courses",
                        tint = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("My Courses", style = CC.descriptionTextStyle(context))
                }

                var darkMode by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        Icons.Default.ModeNight,
                        contentDescription = "Profile",
                        tint = GlobalColors.textColor
                    )
                    Text("Mode", style = CC.descriptionTextStyle(context))
                    Switch(
                        checked = darkMode, modifier = Modifier.size(40.dp), onCheckedChange = {
                            darkMode = it
                            GlobalColors.saveColorScheme(context, it)
                        }, colors = SwitchDefaults.colors(
                            checkedThumbColor = GlobalColors.extraColor1,
                            uncheckedThumbColor = GlobalColors.extraColor2,
                            checkedTrackColor = GlobalColors.extraColor2,
                            uncheckedTrackColor = GlobalColors.extraColor1,
                            checkedIconColor = GlobalColors.textColor,
                            uncheckedIconColor = GlobalColors.textColor
                        )
                    )
                }
                Row(modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable {
                        navController.navigate("settings")
                        scope.launch { drawerState.close() }
                    }
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "settings",
                        tint = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Settings", style = CC.descriptionTextStyle(context))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "$currentName invited you to join Student Portal! Get organized and ace your studies. Download now: [https://github.com/mikesplore/Student-Portal/blob/main/app/release/StudentPortal.apk]") // Customize the text
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                            scope.launch { drawerState.close() }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "share app",
                        tint = GlobalColors.textColor
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Share", style = CC.descriptionTextStyle(context))
                }


            }
            Row(modifier = Modifier
                .fillMaxWidth(0.9f)
                .clickable {
                    auth.signOut()
                    navController.navigate("login")
                    scope.launch { drawerState.close() }
                }
                .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Exit",
                    tint = GlobalColors.textColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Sign Out", style = CC.descriptionTextStyle(context))
            }
        }
    }) {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hi, $currentName👋",
                            style = CC.titleTextStyle(context)
                                .copy(fontWeight = FontWeight.ExtraBold),
                            fontSize = 25.sp,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                    }
                },
                    actions = {
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .padding(end = 20.dp)
                                    .background(GlobalColors.secondaryColor, CircleShape)
                                    .border(1.dp, GlobalColors.primaryColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (currentUser?.photoUrl != null && currentUser.photoUrl.toString()
                                        .isNotEmpty()
                                ) {
                                    AsyncImage(
                                        model = currentUser.photoUrl,
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (selectedImageUri != null && signInMethod == "password") {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profile Picture",
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalColors.primaryColor),
                    modifier = Modifier.height(130.dp)
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
                                .fillMaxWidth(0.7f)
                                .align(Alignment.BottomCenter)
                                .background(
                                    GlobalColors.extraColor2.copy(), RoundedCornerShape(40.dp)
                                ),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            screens.forEachIndexed { index, screen ->
                                val isSelected = pagerState.currentPage == index

                                val iconColor by animateColorAsState(
                                    targetValue = if (isSelected) GlobalColors.extraColor1
                                    else GlobalColors.primaryColor,
                                    label = "",
                                    animationSpec = tween(1000)
                                )
                                val iconSize by animateFloatAsState(
                                    targetValue = if (isSelected) 40f else 25f,
                                    label = "",
                                    animationSpec = tween(2000)
                                )
                                val offsetY by animateDpAsState(
                                    targetValue = if (isSelected) (-10).dp else 0.dp,
                                    label = "",
                                    animationSpec = tween(1000)
                                )

                                Column(
                                    modifier = Modifier

                                        .height(60.dp)
                                        .offset(y = offsetY),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.name,
                                        tint = iconColor,
                                        modifier = Modifier
                                            .clickable {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(index)
                                                }
                                            }
                                            .size(iconSize.dp))
                                    AnimatedVisibility(visible = isSelected,
                                        enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                                            animationSpec = tween(1000)
                                        ) { initialState -> initialState },
                                        exit = fadeOut(animationSpec = tween(1000)) + slideOutVertically(
                                            animationSpec = tween(1000)
                                        ) { initialState -> initialState }) {
                                        Text(
                                            text = screen.name,
                                            style = CC.descriptionTextStyle(context),
                                            color = GlobalColors.extraColor1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }, containerColor = GlobalColors.primaryColor

        ) { innerPadding ->

            HorizontalPager(
                state = pagerState,
                count = screens.size,
                modifier = Modifier.padding(innerPadding),
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState)
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
}




