package com.mike.studentportal

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*
import com.mike.studentportal.MyDatabase.getAnnouncements
import com.mike.studentportal.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreDetails(context: Context, navController: NavController) {
    val database = MyDatabase.database.child("Users")
    var users by remember { mutableStateOf<List<User>?>(null) }

    fun checkEmailExists(email: String, onResult: (Boolean) -> Unit) {
        val query = database.orderByChild("email").equalTo(email) // Query for the email
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val emailExists = snapshot.exists() // Check if the email exists
                onResult(emailExists) // Call the callback with the result
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
                onResult(false) // You might want to handle errors differently
            }
        })
    }

    var mloading by remember { mutableStateOf(true) }
    val subjects = remember { mutableStateListOf<Subjects>() }
    val subjectId by remember { mutableStateOf("") }
    val announcements = remember { mutableStateListOf<Announcement>() }
    var assignments by remember { mutableStateOf<List<Assignment>?>(null) }
    val timetables = remember { mutableStateListOf<Timetable>() }
    val days = remember { mutableStateListOf<Day>() }

    LaunchedEffect(mloading) {
        getAnnouncements { fetchedAnnouncements ->
            announcements.addAll(fetchedAnnouncements ?: emptyList())
        }
        MyDatabase.getAssignments(subjectId) { fetchedAssignments ->
            assignments = fetchedAssignments
        }
        MyDatabase.getSubjects { fetchedSubjects ->
            subjects.addAll(fetchedSubjects ?: emptyList())
        }
        MyDatabase.getUsers { fetchedUsers ->
            users = fetchedUsers
        }
        MyDatabase.getDays { fetchedDays ->
            days.clear()
            days.addAll(fetchedDays ?: emptyList())
        }
        mloading = false
        Details.totalAssignments.value = assignments?.size ?: 0
        Details.totalusers.value = users?.size ?: 0

    }
    if(mloading){
        BasicAlertDialog(onDismissRequest = {}) {
            Column(modifier = Modifier.height(200.dp)) {
                CircularProgressIndicator(
                    color = GlobalColors.primaryColor,
                    trackColor = GlobalColors.textColor
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text("Loading Data...Please wait", style = CC.descriptionTextStyle(LocalContext.current))
            }
        }
    }

    var emailFound by remember { mutableStateOf(false) }
    var loading by remember {  mutableStateOf(true)}
    var addloading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        MyDatabase.getUsers { fetchedUsers ->
            users = fetchedUsers

            checkEmailExists(Details.email.value) { exists ->
                if (exists) {
                    // Find the user with the matching email
                    val existingUser = fetchedUsers?.find { it.email == Details.email.value }

                    if (existingUser != null) {
                        Details.name.value = existingUser.name
                        val userName = existingUser.name // Assuming your User class has a 'name' property
                        loading = false
                        Toast.makeText(context, "Welcome back, $userName!", Toast.LENGTH_SHORT).show()
                        navController.navigate("dashboard")
                    } else {
                        // Handle the case where the user is not found (shouldn't happen if email exists)
                        loading = false
                        Toast.makeText(context, "Unexpected error: User not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    loading = false
                    Toast.makeText(context, "Add your name", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "More Details", style = CC.titleTextStyle(context)) },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigate("dashboard") },
                    modifier = Modifier.absolutePadding(left = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = GlobalColors.textColor,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalColors.primaryColor)
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .background(CC.backbrush)
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CC.backbrush)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    CC.SingleLinedTextField(
                        value = Details.name.value,
                        onValueChange = {
                            if (!emailFound) { // Only update if email is not found
                                Details.name.value = it
                            }
                        },
                        label = "First name",
                        context = context,
                        singleLine = true,
                        enabled = !emailFound // Disable the field if email is found
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                                    addloading = true
                                    database.push().setValue(
                                        User(
                                            email = Details.email.value,
                                            name = Details.name.value
                                        )
                                    ).addOnSuccessListener {
                                        addloading = false
                                        Toast.makeText(context, "Success data added!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("dashboard")
                                    }.addOnFailureListener {
                                        addloading = false
                                        Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show()
                                    }


                        },
                        modifier = Modifier
                            .width(275.dp)
                            .background(
                                CC.backbrush, RoundedCornerShape(10.dp)
                            ), // Background moved to outer Modifier
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically) {
                            if(loading || addloading){
                                CircularProgressIndicator(
                                    color = GlobalColors.primaryColor,
                                    trackColor = GlobalColors.textColor,
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                            if(loading){
                                Text("Checking Database", style = CC.descriptionTextStyle(context))
                            }else{
                                Text(if(addloading)"Adding" else "Add", style = CC.descriptionTextStyle(context))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Extra() {
    MoreDetails(
        navController = rememberNavController(), context = LocalContext.current
    )
}
