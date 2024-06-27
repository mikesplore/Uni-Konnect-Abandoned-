package com.mike.studentportal

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    var emailFound by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var addloading by remember { mutableStateOf(false) }
    val backbrush = Brush.verticalGradient(
        colors = listOf(
            GlobalColors.primaryColor, GlobalColors.secondaryColor
        )
    )
    LaunchedEffect(Unit) {
        MyDatabase.getUsers { fetchedUsers ->
            users = fetchedUsers

            checkEmailExists(Details.email.value) { exists ->
                if (exists) {
                    // Find the user with the matching email
                    val existingUser = fetchedUsers?.find { it.email == Details.email.value }

                    if (existingUser != null) {
                        Details.name.value = existingUser.name

                        val userName =
                            existingUser.name // Assuming your User class has a 'name' property
                        loading = false
                        Toast.makeText(context, "Welcome back, $userName!", Toast.LENGTH_SHORT)
                            .show()
                        navController.navigate("dashboard")
                    } else {
                        // Handle the case where the user is not found (shouldn't happen if email exists)
                        loading = false
                        Toast.makeText(
                            context,
                            "Unexpected error: User not found",
                            Toast.LENGTH_SHORT
                        ).show()
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
                    onClick = { },
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
                .background(backbrush)
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backbrush)
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
                            MyDatabase.writeUsers(
                                User(
                                    name = Details.name.value, email = Details.email.value
                                )
                            )

                            navController.navigate("dashboard")


                        }, modifier = Modifier
                            .width(275.dp)
                            .background(
                                backbrush, RoundedCornerShape(10.dp)
                            ), // Background moved to outer Modifier
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier, verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (loading || addloading) {
                                CircularProgressIndicator(
                                    color = GlobalColors.primaryColor,
                                    trackColor = GlobalColors.textColor,
                                    modifier = Modifier.size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                            if (loading) {
                                Text("Checking Database", style = CC.descriptionTextStyle(context))
                            } else {
                                Text(
                                    if (addloading) "Adding" else "Add",
                                    style = CC.descriptionTextStyle(context)
                                )
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
