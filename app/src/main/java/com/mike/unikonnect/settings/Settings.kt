package com.mike.unikonnect.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.mike.unikonnect.ExitScreen
import com.mike.unikonnect.ui.theme.FontPreferences
import com.mike.unikonnect.ui.theme.GlobalColors
import com.mike.unikonnect.MainActivity
import com.mike.unikonnect.MyDatabase
import com.mike.unikonnect.MyDatabase.fetchUserDataByEmail
import com.mike.unikonnect.MyDatabase.updatePassword
import com.mike.unikonnect.R
import com.mike.unikonnect.model.Feedback
import com.mike.unikonnect.model.User
import com.mike.unikonnect.model.UserPreferences
import kotlinx.coroutines.delay
import com.mike.unikonnect.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController, context: Context, mainActivity: MainActivity) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var currentUser by remember { mutableStateOf(User()) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var signInMethod by remember { mutableStateOf("") }
    val fontPrefs = remember { FontPreferences(context) }
    val startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timeSpent by remember { mutableLongStateOf(0L) }
    var savedFont by remember { mutableStateOf("system") }
    val screenID = "SC8"
    LaunchedEffect(Unit) {
        savedFont = fontPrefs.getSelectedFont().toString()
        while (true) {
            timeSpent = System.currentTimeMillis() - startTime
            delay(1000)
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

    // Fetch user data when the composable is launched
    LaunchedEffect(auth.currentUser?.email) {
        auth.currentUser?.email?.let {
            fetchUserDataByEmail(it) { fetchedUser ->
                fetchedUser?.let {
                    currentUser = fetchedUser
                    MyDatabase.fetchPreferences(currentUser.id) { preferences ->
                        // Log.d("Shared Preferences", "Retrieved preferences for student ID: ${currentUser.id}: $preferences")
                        preferences?.let {
                            selectedImageUri = Uri.parse(preferences.profileImageLink)
                        }
                    }
                }
                Log.e("ProfileCard", "Fetched user: $user")
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        if (user != null) {
            for (userInfo in user.providerData) {
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
    Log.d("Authenticated User", "The user is: $user")
    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = { navController.navigate("dashboard") }) {
                    Icon(
                        Icons.Default.ArrowBackIosNew, "Back", tint = CC.textColor()
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = CC.primary()
            )
            )
        }, containerColor = CC.primary()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Settings",
                    style = CC.titleTextStyle(context),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text("Account", style = CC.titleTextStyle(context))
            }
            Spacer(modifier = Modifier.height(20.dp))
            //Profile Section
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(80.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (user?.photoUrl != null && user.photoUrl.toString().isNotEmpty()) {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else if (selectedImageUri != null && signInMethod == "password") {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Picture",
                            tint = Color.Gray, // Or your preferred color
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxHeight(0.9f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            currentUser.firstName + " " + currentUser.lastName,
                            style = CC.descriptionTextStyle(context),
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            "Personal Info",
                            style = CC.descriptionTextStyle(context),
                            color = CC.textColor().copy(0.8f)
                        )
                    }
                }
                MyIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowForwardIos, navController, "profile"
                )

            }
            Spacer(modifier = Modifier.height(40.dp))
            Row(modifier = Modifier.fillMaxWidth(0.9f)) {
                Text("System", style = CC.titleTextStyle(context))
            }
            Spacer(modifier = Modifier.height(20.dp))
            DarkMode(context)
            Spacer(modifier = Modifier.height(20.dp))
            Notifications(context)
            Spacer(modifier = Modifier.height(40.dp))
            Row(modifier = Modifier.fillMaxWidth(0.9f)) {
                Text("Security", style = CC.titleTextStyle(context))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Biometrics(context, mainActivity)
            Spacer(modifier = Modifier.height(20.dp))
            PasswordUpdateSection(context)
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Font Style", style = CC.titleTextStyle(context))

                Text(
                    savedFont,
                    style = CC.descriptionTextStyle(context)
                        .copy(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                )
                IconButton(
                    onClick = { navController.navigate("appearance") },
                    modifier = Modifier.background(
                            CC.secondary(),
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Font Style",
                        tint = CC.textColor()
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                Text("We care about your feedback", style = CC.titleTextStyle(context))
            }
            RatingAndFeedbackScreen(context)
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(0.9f)) {
                Text("About", style = CC.titleTextStyle(context))
            }
            MyAbout(context)


        }
    }

}

@Preview
@Composable
fun NewSettingsPreview() {
    // NewSettings(rememberNavController(), LocalContext.current)
    Notifications(LocalContext.current)
}

@Composable
fun MyIconButton(icon: ImageVector, navController: NavController, route: String) {
    Box(modifier = Modifier
        .background(CC.secondary(), RoundedCornerShape(10.dp))
        .clickable { navController.navigate(route) }
        .size(50.dp)
        .clip(RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = CC.textColor())
    }

}

@Composable
fun DarkMode(context: Context) {
    var isDarkMode by remember { mutableStateOf(true) }
    val icon = if (isDarkMode) Icons.Filled.ModeNight else Icons.Filled.WbSunny
    val iconDescription = if (isDarkMode) "Switch to Dark Mode" else "Switch to Light Mode"

    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .background(CC.secondary(), CircleShape)
                .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = CC.extraColor2()
            )
        }
        Text("Dark Mode", style = CC.descriptionTextStyle(context), fontSize = 20.sp)
        Switch(
            onCheckedChange = {
                isDarkMode = it
                GlobalColors.saveColorScheme(context, it)
            }, checked = isDarkMode, colors = SwitchDefaults.colors(
                checkedThumbColor = CC.extraColor1(),
                uncheckedThumbColor = CC.extraColor2(),
                checkedTrackColor = CC.extraColor2(),
                uncheckedTrackColor = CC.extraColor1(),
                checkedIconColor = CC.textColor(),
                uncheckedIconColor = CC.textColor()
            )
        )
    }
}

@Composable
fun Notifications(context: Context) {
    var isNotificationEnabled by remember { mutableStateOf(false) }
    val icon =
        if (isNotificationEnabled) Icons.Filled.Notifications else Icons.Filled.NotificationsOff
    val iconDescription =
        if (isNotificationEnabled) "Enable Notifications" else "Disable Notifications"
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(User()) }

    LaunchedEffect(auth.currentUser?.email) {
        auth.currentUser?.email?.let { email ->
            fetchUserDataByEmail(email) { fetchedUser ->
                fetchedUser?.let {
                    currentUser = it
                    Log.d("Current User", "Fetched user name: ${currentUser.firstName}")
                    MyDatabase.fetchPreferences(currentUser.id) { preferences ->
                        preferences?.let {
                            isNotificationEnabled = preferences.notifications == "enabled"
                        }
                    }
                }
            }
        }
    }

    fun updatePreferences(isEnabled: Boolean) {
        MyDatabase.generateSharedPreferencesID { id ->
            val myPreferences = UserPreferences(
                studentID = currentUser.id,
                id = id,
                notifications = if (isEnabled) "enabled" else "disabled"
            )
            MyDatabase.writePreferences(myPreferences) {
                Log.d("Preferences", "Preferences successfully updated: $myPreferences")
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .background(CC.secondary(), CircleShape)
                .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = CC.extraColor2()
            )
        }
        Text("Notifications", style = CC.descriptionTextStyle(context), fontSize = 20.sp)
        Switch(
            onCheckedChange = { notifications ->
                if (!notifications) {
                    (context as MainActivity).requestNotificationPermission()

                }
                isNotificationEnabled = notifications
                updatePreferences(notifications)
            }, checked = isNotificationEnabled, colors = SwitchDefaults.colors(
                checkedThumbColor = CC.extraColor1(),
                uncheckedThumbColor = CC.extraColor2(),
                checkedTrackColor = CC.extraColor2(),
                uncheckedTrackColor = CC.extraColor1(),
                checkedIconColor = CC.textColor(),
                uncheckedIconColor = CC.textColor()
            )
        )
    }
}


@Composable
fun Biometrics(context: Context, mainActivity: MainActivity) {
    var isBiometricsEnabled by remember { mutableStateOf(false) }
    val icon = if (isBiometricsEnabled) Icons.Filled.Security else Icons.Filled.Security
    val iconDescription = if (isBiometricsEnabled) "Biometrics enabled" else "Biometrics disabled"
    val promptManager = mainActivity.promptManager
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(User()) }

    LaunchedEffect(auth.currentUser?.email) {
        auth.currentUser?.email?.let { email ->
            fetchUserDataByEmail(email) { fetchedUser ->
                fetchedUser?.let {
                    currentUser = it
                    Log.d("Current User", "Fetched user name: ${currentUser.firstName}")
                    MyDatabase.fetchPreferences(currentUser.id) { preferences ->
                        preferences?.let {
                            isBiometricsEnabled = preferences.biometrics == "enabled"
                        } ?: Log.e(
                            "Shared Preferences",
                            "Preferences not found for student ID: ${currentUser.id}"
                        )
                    }
                }
            }
        }
    }

    fun updatePreferences(isEnabled: Boolean) {
        MyDatabase.generateSharedPreferencesID { id ->
            val myPreferences = UserPreferences(
                studentID = currentUser.id,
                id = id,
                biometrics = if (isEnabled) "enabled" else "disabled"
            )
            MyDatabase.writePreferences(myPreferences) {
                Log.d("Preferences", "Preferences successfully updated: $myPreferences")
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .background(CC.secondary(), CircleShape)
                .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = CC.extraColor2(),
            )
        }
        Text(
            "Biometrics (${if (isBiometricsEnabled) "Enabled" else "Disabled"})",
            style = CC.descriptionTextStyle(context),
            fontSize = 20.sp
        )
        Switch(
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    promptManager.showBiometricPrompt(
                        title = "Authenticate", description = "Please authenticate to continue"
                    ) { success ->
                        if (success) {
                            isBiometricsEnabled = true
                            updatePreferences(true)
                        }
                    }
                } else {
                    isBiometricsEnabled = false
                    updatePreferences(false)
                }
            }, checked = isBiometricsEnabled, colors = SwitchDefaults.colors(
                checkedThumbColor = CC.extraColor1(),
                uncheckedThumbColor = CC.extraColor2(),
                checkedTrackColor = CC.extraColor2(),
                uncheckedTrackColor = CC.extraColor1(),
                checkedIconColor = CC.textColor(),
                uncheckedIconColor = CC.textColor()
            )
        )
    }
}


@Composable
fun PasswordUpdateSection(context: Context) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var loading by remember { mutableStateOf(false) }
    var signInMethod by remember { mutableStateOf("") }

    Row(modifier = Modifier.fillMaxWidth(0.8f)) {
        Text("Change your Password", style = CC.titleTextStyle(context), fontSize = 18.sp)
    }
    Spacer(modifier = Modifier.height(10.dp))
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
    if (signInMethod != "password") {
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(0.9f)
        ) {
            Text(
                "This section only applies to users who signed in using Email and Password",
                style = CC.descriptionTextStyle(context),
                color = CC.tertiary(),
                textAlign = TextAlign.Center
            )

        }
    } else {
        Column(
            modifier = Modifier
                .border(
                    1.dp, CC.secondary(), RoundedCornerShape(10.dp)
                )
                .fillMaxWidth(0.8f)
                .padding(16.dp)
        ) {
            PasswordTextField(
                label = "Current Password",
                value = currentPassword,
                isEditing = true,
                onValueChange = { currentPassword = it },
                context = context
            )
            PasswordTextField(
                label = "New Password",
                value = newPassword,
                isEditing = true,
                onValueChange = { newPassword = it },
                context = context
            )
            PasswordTextField(
                label = "Confirm Password",
                value = confirmPassword,
                isEditing = true,
                onValueChange = { confirmPassword = it },
                context = context
            )

            Button(
                onClick = {
                    loading = true
                    if (newPassword == confirmPassword && newPassword.isNotEmpty() && currentPassword.isNotEmpty()) {
                        currentUser?.let { user ->
                            val credential =
                                EmailAuthProvider.getCredential(user.email!!, currentPassword)
                            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                                if (reauthTask.isSuccessful) {
                                    updatePassword(newPassword, onSuccess = {
                                        // Handle success (e.g., show a success message)
                                        loading = false
                                        Toast.makeText(
                                            context,
                                            "Password updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                    }, onFailure = { exception ->
                                        // Handle failure (e.g., show an error message)
                                        loading = false
                                        Toast.makeText(
                                            context,
                                            "Failed to Change password: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    })
                                } else {
                                    // Handle reauthentication failure
                                    loading = false
                                    Toast.makeText(
                                        context,
                                        "Authentication failed: ${reauthTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else {
                        // Handle password mismatch
                        loading = false
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.padding(top = 16.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = CC.tertiary(), contentColor = Color.White
                ), shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = CC.primary(),
                            trackColor = CC.tertiary(),
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Change Password", style = CC.descriptionTextStyle(context))
                    }
                }

            }
        }
    }
}

@Composable
fun PasswordTextField(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    context: Context
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = CC.descriptionTextStyle(context)) },
        enabled = isEditing,
        textStyle = CC.descriptionTextStyle(context),
        colors = TextFieldDefaults.colors(
            focusedTextColor = CC.textColor(),
            disabledContainerColor = CC.secondary(),
            focusedContainerColor = CC.primary(),
            unfocusedContainerColor = CC.primary(),
            focusedIndicatorColor = CC.secondary(),
            unfocusedIndicatorColor = CC.tertiary(),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun MyAbout(context: Context) {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Uni Konnect", style = CC.descriptionTextStyle(context).copy(
                fontWeight = FontWeight.Bold, fontSize = 20.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Version $versionName", style = CC.descriptionTextStyle(context))
        Text("Developed by Mike", style = CC.descriptionTextStyle(context))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            //Phone Icon
            IconButton(onClick = {
                val intent = Intent(
                    Intent.ACTION_DIAL, Uri.parse("tel:+254799013845")
                )
                context.startActivity(intent)
            }, modifier = Modifier
                .background(CC.extraColor1(), CircleShape)
                .size(35.dp)) {
                Icon(Icons.Default.Call, "Call", tint = CC.textColor())
            }
            Spacer(modifier = Modifier.width(16.dp))
            // GitHub Icon with Link
            IconButton(
                onClick = { uriHandler.openUri("https://github.com/mikesplore") },
                modifier = Modifier
                    .background(CC.extraColor1(), CircleShape)
                    .size(35.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.github), // Replace with your actual drawable
                    contentDescription = "GitHub Profile", modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("All rights reserved © 2024", style = CC.descriptionTextStyle(context))
    }
}

@Composable
fun StarRating(
    currentRating: Int,
    onRatingChanged: (Int) -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val color = when {
                i <= currentRating -> when (i) {
                    in 1..2 -> Color.Red
                    3 -> CC.extraColor2()
                    else -> Color.Green
                }

                else -> CC.secondary()
            }
            val animatedScale by animateFloatAsState(
                targetValue = if (i <= currentRating) 1.2f else 1.0f,
                animationSpec = tween(durationMillis = 300),
                label = ""
            )
            Star(filled = i <= currentRating,
                color = color,
                scale = animatedScale,
                onClick = { onRatingChanged(i) })
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun Star(
    filled: Boolean, color: Color, scale: Float, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val path = Path().apply {
        moveTo(50f, 0f)
        lineTo(61f, 35f)
        lineTo(98f, 35f)
        lineTo(68f, 57f)
        lineTo(79f, 91f)
        lineTo(50f, 70f)
        lineTo(21f, 91f)
        lineTo(32f, 57f)
        lineTo(2f, 35f)
        lineTo(39f, 35f)
        close()
    }

    Canvas(
        modifier = modifier
            .size((40 * scale).dp)
            .clickable(onClick = onClick)
    ) {
        drawPath(
            path = path,
            color = if (filled) color else Color.Gray,
            style = if (filled) Stroke(width = 8f) else Stroke(
                width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun RatingAndFeedbackScreen(context: Context) {
    var currentRating by remember { mutableIntStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }
    var averageRatings by remember { mutableStateOf("") }
    val user by remember { mutableStateOf(User()) }
    var showFeedbackForm by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        MyDatabase.fetchAverageRating { averageRating ->
            averageRatings = averageRating
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (averageRatings.isEmpty()) "No ratings yet" else "Average Rating: $averageRatings",
            style = CC.descriptionTextStyle(context),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        StarRating(
            currentRating = currentRating, onRatingChanged = { rating ->
                currentRating = rating
                showFeedbackForm = true
            }, context = context
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = showFeedbackForm) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = {
                        Text(
                            "Enter your feedback (optional)",
                            style = CC.descriptionTextStyle(context)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    textStyle = CC.descriptionTextStyle(context),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        loading = true
                        MyDatabase.generateFeedbackID { feedbackId ->
                            val feedback = Feedback(
                                id = feedbackId,
                                rating = currentRating,
                                sender = user.firstName + " " + user.lastName,
                                message = feedbackText,
                                admissionNumber = user.id
                            )
                            MyDatabase.writeFeedback(feedback, onSuccess = {
                                loading = false
                                Toast.makeText(
                                    context, "Thanks for your feedback", Toast.LENGTH_SHORT
                                ).show()
                                feedbackText = ""
                                MyDatabase.fetchAverageRating { averageRating ->
                                    averageRatings = averageRating
                                }
                                showFeedbackForm = false
                            }, onFailure = {
                                loading = false
                                Toast.makeText(
                                    context,
                                    "Failed to send feedback: ${it?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CC.extraColor1(),
                        contentColor = CC.secondary()
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color = CC.primary(),
                                trackColor = CC.tertiary(),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Submit Feedback", style = CC.descriptionTextStyle(context))
                        }
                    }
                }
            }
        }
    }
}




