package com.mike.studentportal

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.mike.studentportal.MyDatabase.fetchUserDataByEmail
import com.mike.studentportal.MyDatabase.updatePassword
import kotlin.math.sign
import com.mike.studentportal.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController, context: Context, mainActivity: MainActivity){
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var currentUser by remember { mutableStateOf(User()) }

    // Fetch user data when the composable is launched
    LaunchedEffect(auth.currentUser?.email) {
        auth.currentUser?.email?.let {
            fetchUserDataByEmail(it) { fetchedUser ->
                fetchedUser?.let {
                    currentUser = it


                }
                Log.e("ProfileCard", "Fetched user: $user")
            }
        }
    }
    Log.d("Authenticated User", "The user is: $user")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                },
                navigationIcon = {
                    IconButton(onClick ={navController.popBackStack()} ) {
                        Icon(Icons.Default.ArrowBackIosNew,"Back",
                            tint = GlobalColors.textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor
                )
            )
        },
        containerColor = GlobalColors.primaryColor
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Settings", style = CC.titleTextStyle(context), fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
            }
            Row(modifier = Modifier
                .fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start) {
                Text("Account", style = CC.titleTextStyle(context))
            }
            Spacer(modifier = Modifier.height(20.dp))
            //Profile Section
            Row(modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(70.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround){

                Row (modifier = Modifier
                    .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)){
                    if (user?.photoUrl != null) {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(GlobalColors.secondaryColor)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Picture",
                            tint = GlobalColors.secondaryColor,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                    }
                Column(
                    modifier = Modifier.fillMaxHeight(0.9f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(currentUser.firstName + " " + currentUser.lastName, style = CC.descriptionTextStyle(context), fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text("Personal Info", style = CC.descriptionTextStyle(context), color = GlobalColors.textColor.copy(0.8f))
                }}
                MyIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    navController,
                    "profile"
                )

            }
            Spacer(modifier = Modifier.height(40.dp))
            Row (modifier = Modifier.fillMaxWidth(0.9f)){
                Text("Settings", style = CC.titleTextStyle(context))
            }
            Spacer(modifier = Modifier.height(20.dp))
            DarkMode(context)
            Spacer(modifier = Modifier.height(20.dp))
            Notifications(context)
            Spacer(modifier = Modifier.height(40.dp))
            Row (modifier = Modifier.fillMaxWidth(0.9f)){
                Text("Security", style = CC.titleTextStyle(context))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Biometrics(context, mainActivity)
            Spacer(modifier = Modifier.height(20.dp))
            PasswordUpdateSection(context)
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
fun NewSettingsPreview(){
   // NewSettings(rememberNavController(), LocalContext.current)
    Notifications( LocalContext.current)
}

@Composable
fun MyIconButton(icon: ImageVector, navController: NavController, route: String){
    Box(modifier = Modifier
        .background(GlobalColors.secondaryColor, RoundedCornerShape(10.dp))
        .clickable { navController.navigate(route)}
        .size(50.dp)
        .clip(RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center){
        Icon(icon, contentDescription = null, tint = GlobalColors.textColor)
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
        Box( modifier = Modifier
            .background(GlobalColors.secondaryColor, CircleShape)
            .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = GlobalColors.extraColor2
            )
        }
        Text("Dark Mode", style = CC.descriptionTextStyle(context), fontSize = 20.sp)
        Switch(
            onCheckedChange = {
                isDarkMode = it
                GlobalColors.saveColorScheme(context, it)              },
            checked = isDarkMode,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GlobalColors.extraColor1,
                uncheckedThumbColor = GlobalColors.extraColor2,
                checkedTrackColor = GlobalColors.extraColor2,
                uncheckedTrackColor = GlobalColors.extraColor1,
                checkedIconColor = GlobalColors.textColor,
                uncheckedIconColor = GlobalColors.textColor
            )
        )
    }
}

@Composable
fun Notifications(context: Context) {
    var isNotificationEnabled by remember { mutableStateOf(Global.showAlert.value) }
    val icon = if (isNotificationEnabled) Icons.Filled.Notifications else Icons.Filled.NotificationsOff
    val iconDescription = if (isNotificationEnabled) "Enable Notifications" else "Disable Notifications"

    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box( modifier = Modifier
            .background(GlobalColors.secondaryColor, CircleShape)
            .size(50.dp),
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = GlobalColors.extraColor2
            )
        }
        Text("Notifications", style = CC.descriptionTextStyle(context), fontSize = 20.sp)
        Switch(
            onCheckedChange = { notifications ->
                if (!notifications) {
                    (context as MainActivity).requestNotificationPermission()

                }
                isNotificationEnabled = notifications
            },
            checked = Global.showAlert.value,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GlobalColors.extraColor1,
                uncheckedThumbColor = GlobalColors.extraColor2,
                checkedTrackColor = GlobalColors.extraColor2,
                uncheckedTrackColor = GlobalColors.extraColor1,
                checkedIconColor = GlobalColors.textColor,
                uncheckedIconColor = GlobalColors.textColor
            )
        )
    }
}


@Composable
fun Biometrics(context: Context, mainActivity: MainActivity) {
    val isBiometricsEnabled = Global.isAuthenticationSuccessful.value
    val icon = if (isBiometricsEnabled) Icons.Filled.Security else Icons.Filled.Security
    val iconDescription = if (isBiometricsEnabled) "Biometrics enabled" else "Biometrics disabled"
    var expanded by remember { mutableStateOf(false) }
    var selectedInterval by remember { mutableStateOf("Never") }
    val timeIntervals = listOf("1 minute", "10 minutes", "30 minutes", "Never")
    val promptManager = mainActivity.promptManager

//    LaunchedEffect(Global.isAuthenticationSuccessful.value) {
//        if (isBiometricsEnabled) {
//            promptManager.showBiometricPrompt(
//                title = "Authenticate",
//                description = "Please authenticate to continue"
//            )
//        }
//    }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box( modifier = Modifier
            .background(GlobalColors.secondaryColor, CircleShape)
            .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = GlobalColors.extraColor2
            )
        }
        Text("Biometrics (${if (isBiometricsEnabled) "Enabled" else "Disabled"})", style = CC.descriptionTextStyle(context), fontSize = 20.sp)
        Switch(
            onCheckedChange = { isChecked ->
               // Global.isAuthenticationSuccessful.value = isChecked
            },
            checked = isBiometricsEnabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GlobalColors.extraColor1,
                uncheckedThumbColor = GlobalColors.extraColor2,
                checkedTrackColor = GlobalColors.extraColor2,
                uncheckedTrackColor = GlobalColors.extraColor1,
                checkedIconColor = GlobalColors.textColor,
                uncheckedIconColor = GlobalColors.textColor
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
    if(signInMethod != "password"){
        Row(modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(0.9f))
        {
            Text("This section only applies to users who signed in using Email and Password",
                style = CC.descriptionTextStyle(context),
                color = GlobalColors.tertiaryColor,
                textAlign = TextAlign.Center)

        }
    }else{
    Column(
        modifier = Modifier
            .border(
                1.dp, GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
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
                                        context, "Password updated successfully", Toast.LENGTH_SHORT
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
                containerColor = GlobalColors.tertiaryColor, contentColor = Color.White
            ), shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = GlobalColors.primaryColor,
                        trackColor = GlobalColors.tertiaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Change Password", style = CC.descriptionTextStyle(context))
                }
            }

        }
    }}
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
            focusedTextColor = GlobalColors.textColor,
            disabledContainerColor = GlobalColors.secondaryColor,
            focusedContainerColor = GlobalColors.primaryColor,
            unfocusedContainerColor = GlobalColors.primaryColor,
            focusedIndicatorColor = GlobalColors.secondaryColor,
            unfocusedIndicatorColor = GlobalColors.tertiaryColor,
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
            "Student Portal",
            style = CC.descriptionTextStyle(context).copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Version $versionName", style = CC.descriptionTextStyle(context))
        Text("Developed by Mike", style = CC.descriptionTextStyle(context))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Google Icon with Link
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:+254799013845"))
                context.startActivity(intent)
            },
                modifier = Modifier
                    .background(GlobalColors.extraColor1, CircleShape)
                    .size(35.dp)) {
                Icon(Icons.Default.Call,"Call", tint = GlobalColors.tertiaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            // GitHub Icon with Link
            IconButton(onClick = { uriHandler.openUri("https://github.com/mikesplore")},
                modifier = Modifier
                    .background(GlobalColors.extraColor1, CircleShape)
                    .size(35.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.github), // Replace with your actual drawable
                    contentDescription = "GitHub Profile",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("All rights reserved © 2024", style = CC.descriptionTextStyle(context))
    }
}





