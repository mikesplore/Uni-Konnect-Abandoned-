package com.mike.studentportal

import android.content.Context
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
import com.mike.studentportal.MyDatabase.getUsers
import com.mike.studentportal.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsScreen(navController: NavController, context: Context) {
    var users by remember { mutableStateOf<List<User>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

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
            Text(
                text = user.firstName +" "+ user.lastName,
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
                Text("Text", style = CC.descriptionTextStyle(context))
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
