package com.mike.unikonnect

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.mike.unikonnect.MyDatabase.getUpdate
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import com.mike.unikonnect.CommonComponents as CC

object Global {
    val showAlert: MutableState<Boolean> = mutableStateOf(false)
    val edgeToEdge: MutableState<Boolean> = mutableStateOf(true)
    var loading: MutableState<Boolean> = mutableStateOf(true)
}

class MainActivity : AppCompatActivity() {
    val promptManager by lazy {
        BiometricPromptManager(this)
    }

    private lateinit var sharedPreferences: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_UniKonnect)
        super.onCreate(savedInstanceState)
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
    var isDownloading by remember { mutableStateOf(false) }
    var downloadId by remember { mutableLongStateOf(-1L) }

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
    fun installApk(context: Context, uri: Uri) {
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(installIntent)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
     fun startDownload(context: Context, url: String, onProgress: (Int, Long) -> Unit) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("UniKonnect Update")
            .setDescription("Downloading update")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "UniKonnect.apk")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadid = downloadManager.enqueue(request)

        // Registering receiver for download complete
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadid) {
                    context.unregisterReceiver(this)
                    val apkUri = downloadManager.getUriForDownloadedFile(id)
                    installApk(context, apkUri)
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)

        // Track progress
        val progressHandler = Handler(Looper.getMainLooper())
        progressHandler.post(object : Runnable {
            override fun run() {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor: Cursor? = downloadManager.query(query)

                cursor?.use {
                    if (it.moveToFirst()) {
                        val bytesDownloadedIndex = it.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        val bytesTotalIndex = it.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

                        if (bytesDownloadedIndex != -1 && bytesTotalIndex != -1) {
                            val bytesDownloaded = it.getLong(bytesDownloadedIndex)
                            val bytesTotal = it.getLong(bytesTotalIndex)

                            // Log for debugging
                            Log.d("DownloadManager", "Downloaded: $bytesDownloaded, Total: $bytesTotal")

                            if (bytesTotal > 0) {
                                val progress = ((bytesDownloaded * 100) / bytesTotal).toInt()
                                onProgress(progress, downloadId)

                                // Update progress in UI
                                Log.d("DownloadProgress", "Progress: $progress%")

                                if (progress < 100) {
                                    progressHandler.postDelayed(this, 1000)
                                }
                            }
                        } else {
                            Log.e("DownloadManager", "Column index not found")
                        }
                    }
                }
                cursor?.close()
            }
        })
    }




    if (update) {
        BasicAlertDialog(
            onDismissRequest = {
                isDownloading = false
                update = false },
            modifier = Modifier.background(
                Color.Transparent, RoundedCornerShape(10.dp)
            )
        ) {
            Column(
                modifier = Modifier
                    .background(CC.secondary(), RoundedCornerShape(10.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "New Update available!", style = CC.titleTextStyle(context).copy(
                        fontSize = 18.sp, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "A new version of this app is available. The update contains bug fixes and improvements.",
                    style = CC.descriptionTextStyle(context),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (isDownloading) {
                    Text(
                        "Downloading update...please wait",
                        style = CC.descriptionTextStyle(context),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LinearProgressIndicator(
                        color = CC.textColor(),
                        trackColor = CC.extraColor1()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (!isDownloading) {
                                startDownload(context, "https://github.com/mikesplore/Uni-Konnect/releases/download/V1.2.7/UniKonnect.apk") { progress, id ->
                                    downloadId = id
                                    isDownloading = progress < 100
                                }
                                isDownloading = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CC.primary())
                    ) {
                        Text("Update", style = CC.descriptionTextStyle(context))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { update = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Cancel", color = CC.primary())
                    }
                }
            }
        }
    }

    val navController = rememberNavController()
    NavHost(navController, startDestination = "splashscreen") {

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
            Dashboard(navController, pagerState, coroutineScope, screens, context, mainActivity)
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
            fadeOut(animationSpec = tween(1000))
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

        composable("authentication"){
            AuthenticationScreen(navController, context, mainActivity)
        }

        composable("chat", enterTransition = {
            fadeIn(animationSpec = tween(1000))
        }, exitTransition = {
            fadeOut(animationSpec = tween(1000))
        }) {
            ChatScreen(navController, context)
        }

        composable("courses",
            enterTransition = {
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

        composable("settings",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)
                )
            },
            exitTransition = {
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
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(500)
                )
            }, exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(500)
                )
            }
        ) { backStackEntry ->
            val courseCode = backStackEntry.arguments?.getString("courseCode") ?: ""
            CourseScreen(courseCode = courseCode, context)
        }
    }

}





