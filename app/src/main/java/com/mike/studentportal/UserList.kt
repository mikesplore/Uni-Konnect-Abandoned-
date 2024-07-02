package com.mike.studentportal

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mike.studentportal.MyDatabase.fetchUserDataByEmail
import com.mike.studentportal.MyDatabase.getUsers
import kotlinx.coroutines.delay
import com.mike.studentportal.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsScreen(navController: NavController, context: Context) {
    val auth = FirebaseAuth.getInstance()
    var users by remember { mutableStateOf<List<User>?>(null) }
    var currentMe by remember { mutableStateOf(User()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var currentPerson by remember { mutableStateOf("") }
    LaunchedEffect(key1 = Unit) { // Use a stable key
        while (true) {
            delay(10L) // Delay for 10 seconds
            auth.currentUser?.email?.let { email ->
                fetchUserDataByEmail(email) { fetchedUser ->
                    fetchedUser?.let {
                        currentMe = it
                        currentPerson = it.firstName
                        Log.e("ProfileCard", "Fetched user: $currentMe")
                        // You might want to add a mechanism to notify the UI about the updated data
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        getUsers { fetchedUsers ->
            if (fetchedUsers == null) {
                errorMessage = "Failed to fetch users. Please try again later."
            } else {
                users = fetchedUsers
            }
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Participants (${users?.size})", style = CC.titleTextStyle(context)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalColors.primaryColor)
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Background(context)
                when {
                    loading -> {
                        CircularProgressIndicator(color = GlobalColors.textColor)
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            style = CC.descriptionTextStyle(context)
                        )
                    }
                    users.isNullOrEmpty() -> {
                        Text(
                            text = "No participants found.",
                            color = GlobalColors.textColor,
                            style = CC.descriptionTextStyle(context)
                        )
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            users!!.forEach { user ->
                                ProfileCard(user, navController, context)
                            }
                        }
                    }
                }
            }
        },
        containerColor = GlobalColors.primaryColor
    )
}

@Composable
fun ProfileCard(user: User, navController: NavController, context: Context) {
    val auth = FirebaseAuth.getInstance()
    var currentMe by remember { mutableStateOf(User()) }
    var currentPerson by remember { mutableStateOf("") }
    LaunchedEffect(key1 = Unit) { // Use a stable key
        while (true) {
            delay(10L) // Delay for 10 seconds
            auth.currentUser?.email?.let { email ->
                fetchUserDataByEmail(email) { fetchedUser ->
                    fetchedUser?.let {
                        currentMe = it
                        currentPerson = it.firstName
                        Log.e("ProfileCard", "Fetched user: $currentMe")
                        // You might want to add a mechanism to notify the UI about the updated data
                    }
                }
            }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GlobalColors.primaryColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.student), // Replace with your profile icon
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            val displayName = if (user.id == currentMe.id) {
                if (user.isAdmin) {
                    "You (Admin)"
                } else {
                    "You"
                }
            } else if (user.isAdmin) {
                user.firstName + " " + user.lastName + " (Admin)"
            } else {
                user.firstName + " " + user.lastName
            }
            Text(
                text = displayName,
                style = CC.descriptionTextStyle(navController.context),
                modifier = Modifier.weight(1f),
                color = GlobalColors.textColor
            )
            Button(
                onClick = {
                    navController.navigate("chat/${user.id}")
                },
                colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.extraColor2),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (user.id == currentMe.id) {
                    Text("Self Chat", style = CC.descriptionTextStyle(context))}
                else{
                    Text("DM", style = CC.descriptionTextStyle(context))
                }
            }
        }
    }
}

@Preview
@Composable
fun UsersPreview() {
    //ParticipantsScreen(rememberNavController(), LocalContext.current)
    ProfileCard(
        user = User(firstName = "Mike", email = "mike@2020"),
        navController = rememberNavController(),
        context = LocalContext.current
    )
}
