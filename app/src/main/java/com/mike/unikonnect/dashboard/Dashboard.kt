package com.mike.unikonnect.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerDefaults
import com.google.accompanist.pager.PagerState
import com.google.firebase.auth.FirebaseAuth
import com.mike.unikonnect.ExitScreen
import com.mike.unikonnect.ui.theme.GlobalColors
import com.mike.unikonnect.MainActivity
import com.mike.unikonnect.MyDatabase
import com.mike.unikonnect.MyDatabase.fetchUserDataByEmail
import com.mike.unikonnect.attendance.SignAttendanceScreen
import com.mike.unikonnect.announcements.AnnouncementsScreen
import com.mike.unikonnect.assignments.AssignmentScreen
import com.mike.unikonnect.model.Screen
import com.mike.unikonnect.homescreen.HomeScreen
import com.mike.unikonnect.model.Update
import com.mike.unikonnect.settings.BiometricPromptManager
import com.mike.unikonnect.timetble.TimetableScreen
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import com.mike.unikonnect.model.User
import com.mike.unikonnect.CommonComponents as CC

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalSnapperApi::class)
@Composable
fun Dashboard(
    navController: NavController,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    screens: List<Screen>,
    context: Context,
    mainActivity: MainActivity
) {
    var user by remember { mutableStateOf(User()) }
    val auth = FirebaseAuth.getInstance()
    var currentName by remember { mutableStateOf("") }
    val startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timeSpent by remember { mutableLongStateOf(0L) }
    val screenID = "SC10"
    val promptManager = remember { BiometricPromptManager(mainActivity) }


    LaunchedEffect(Unit) {
        while (true) {
            timeSpent = System.currentTimeMillis() - startTime
            delay(1000) // Update every second (adjust as needed)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ExitScreen(
                context = context,
                screenID = screenID,
                timeSpent = timeSpent
            )
        }
    }

    val currentUser = auth.currentUser
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var signInMethod by remember { mutableStateOf("") }
    var update by remember { mutableStateOf(Update()) }

    LaunchedEffect(key1 = Unit) { // Use a stable key
        GlobalColors.loadColorScheme(context)
        MyDatabase.getUpdate { fetched ->
            if (fetched != null) {
                update = fetched

            }
        }
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

    ModalNavigationDrawer(drawerState = drawerState,

        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.8f)
                    .background(CC.secondary(), RoundedCornerShape(10.dp))
            ) {
                Column(
                    modifier = Modifier
                        .background(CC.extraColor2())
                        .height(200.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier
                        .clickable {
                            promptManager.showBiometricPrompt(title = "Authenticate",
                                description = "Confirm you are $currentName",
                                onResult = { result ->
                                    if (result) {
                                        navController.navigate("profile")
                                        scope.launch { drawerState.close() }
                                    }
                                })
                        }
                        .size(150.dp)
                        .padding(20.dp)
                        .background(CC.secondary(), CircleShape)
                        .border(1.dp, CC.primary(), CircleShape),
                        contentAlignment = Alignment.Center) {
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
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            user.id,
                            style = CC.descriptionTextStyle(context)
                                .copy(fontWeight = FontWeight.Bold)
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
                            promptManager.showBiometricPrompt(title = "Authenticate",
                                description = "Confirm you are ${
                                    currentName.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.ROOT
                                        ) else it.toString()
                                    }
                                }",
                                onResult = { result ->
                                    if (result) {
                                        navController.navigate("users")
                                        scope.launch { drawerState.close() }
                                    }
                                })
                        }
                        .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Chat",
                            tint = CC.textColor()
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Uni Chat", style = CC.descriptionTextStyle(context))
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable {
                            promptManager.showBiometricPrompt(title = "Authenticate",
                                description = "Confirm you are ${
                                    currentName.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.ROOT
                                        ) else it.toString()
                                    }
                                }",
                                onResult = { result ->
                                    if (result) {
                                        navController.navigate("chat")
                                        scope.launch { drawerState.close() }
                                    }
                                })
                        }
                        .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = "Discussion",
                            tint = CC.textColor()
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Discussions", style = CC.descriptionTextStyle(context))
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
                            tint = CC.textColor()
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
                        .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Collections,
                            contentDescription = "Courses",
                            tint = CC.textColor()
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
                            if (darkMode) Icons.Default.ModeNight else Icons.Default.WbSunny,
                            contentDescription = "Profile",
                            tint = CC.textColor()
                        )
                        Text("Mode", style = CC.descriptionTextStyle(context))
                        Switch(
                            checked = darkMode, modifier = Modifier.size(40.dp), onCheckedChange = {
                                darkMode = it
                                GlobalColors.saveColorScheme(context, it)
                            }, colors = SwitchDefaults.colors(
                                checkedThumbColor = CC.extraColor1(),
                                uncheckedThumbColor = CC.extraColor2(),
                                checkedTrackColor = CC.extraColor2(),
                                uncheckedTrackColor = CC.extraColor1(),
                                checkedIconColor = CC.textColor(),
                                uncheckedIconColor = CC.textColor()
                            )
                        )
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable {
                            promptManager.showBiometricPrompt(title = "Authenticate",
                                description = "Confirm you are ${
                                    currentName.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.ROOT
                                        ) else it.toString()
                                    }
                                }",
                                onResult = { result ->
                                    if (result) {
                                        navController.navigate("settings")
                                        scope.launch { drawerState.close() }
                                    }
                                })

                        }
                        .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "settings",
                            tint = CC.textColor()
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Settings", style = CC.descriptionTextStyle(context))
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "$currentName invites you to join Uni Konnect! Get organized and ace your studies.\n Download now: ${update.updateLink}"
                                ) // Customize the text
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                            scope.launch { drawerState.close() }
                        }
                        .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "share app",
                            tint = CC.textColor()
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Invite Friends", style = CC.descriptionTextStyle(context))
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
                        tint = CC.textColor()
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Sign Out", style = CC.descriptionTextStyle(context))
                }
            }
        }) {
        Scaffold(topBar = {
            TopAppBar(title = {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Hi, ${
                            currentName.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            }
                        } 👋",
                        style = CC.titleTextStyle(context).copy(fontWeight = FontWeight.ExtraBold),
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
                        Box(modifier = Modifier
                            .clickable { scope.launch { drawerState.open() } }
                            .clip(CircleShape)
                            .padding(end = 20.dp)
                            .background(CC.secondary(), CircleShape)
                            .border(1.dp, CC.primary(), CircleShape),
                            contentAlignment = Alignment.Center) {
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
                                    tint = CC.secondary(),
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CC.primary()),
                modifier = Modifier.height(130.dp)
            )

        }, bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(85.dp)
                    .background(Color.Transparent),
                containerColor = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CC.primary().copy()),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    screens.forEachIndexed { index, screen ->
                        val isSelected = pagerState.currentPage == index

                        val iconColor by animateColorAsState(
                            targetValue = if (isSelected) CC.textColor() else CC.textColor().copy(0.7f),
                            label = "",
                            animationSpec = tween(500)
                        )
                        val iconSize by animateFloatAsState(
                            targetValue = if (isSelected) 30f else 25f,
                            label = "",
                            animationSpec = tween(500)
                        )

                        // Use NavigationBarItem
                        NavigationBarItem(
                            selected = isSelected,
                            label = {
                                AnimatedVisibility(
                                    visible = isSelected,
                                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                                        animationSpec = tween(500)
                                    ) { initialState -> initialState },
                                    exit = fadeOut(animationSpec = tween(500)) + slideOutVertically(
                                        animationSpec = tween(500)
                                    ) { initialState -> initialState }
                                ) {
                                    Text(
                                        text = screen.name,
                                        style = CC.descriptionTextStyle(context).copy(fontSize = 13.sp),
                                        color = CC.textColor()
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = CC.extraColor2(),
                                unselectedIconColor = CC.textColor(),
                                selectedIconColor = CC.textColor()
                            ),
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            icon = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.name,
                                        tint = iconColor,
                                        modifier = Modifier.size(iconSize.dp)
                                    )

                                }
                            }
                        )
                    }
                }
            }
        }, containerColor = CC.primary()

        ) { innerPadding ->

            HorizontalPager(
                state = pagerState,
                count = screens.size,
                modifier = Modifier.padding(innerPadding),
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState)
            ) { page ->
                when (screens[page]) {
                    Screen.Home -> HomeScreen(context, navController)
                    Screen.Assignments -> AssignmentScreen( context)
                    Screen.Announcements -> AnnouncementsScreen( context)
                    Screen.Timetable -> TimetableScreen(context)
                    Screen.Attendance -> SignAttendanceScreen( context)

                }
            }
        }
    }
}