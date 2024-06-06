package com.mike.studentportal

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mike.studentportal.CommonComponents as CC


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(navController: NavController, context: Context) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSigningUp by remember { mutableStateOf(true) } // Track if signing up or logging in
    var feedbackMessage by remember { mutableStateOf("") }
    var isGithubLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    val firebaseAuth = FirebaseAuth.getInstance()
    var visibility by remember { mutableStateOf(false) }
    var loginSuccess by remember { mutableStateOf(false) }
    var registrationSuccess by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = if (isSigningUp) "Sign Up" else "Sign In",
                    fontSize = 30.sp,
                    color = GlobalColors.textColor
                )
            }, navigationIcon = {
                IconButton(onClick = { /* Handle back button click */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = GlobalColors.textColor
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GlobalColors.primaryColor
            )
            )
        }, modifier = Modifier.background(GlobalColors.primaryColor), containerColor = GlobalColors.primaryColor
    ) {
        //main screen
        Column(
            modifier = Modifier
                .padding(it)
                .background(GlobalColors.primaryColor)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.width(350.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isSigningUp) "Sign up with one of the following options" else "Sign In with one of the following options",
                    fontSize = 15.sp,
                    color = GlobalColors.textColor,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Row(
                    modifier = Modifier
                        .height(100.dp)
                        .width(320.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    GoogleAuth(
                        firebaseAuth = firebaseAuth,
                        onSignInSuccess = {
                            // Handle successful sign-in
                            isGoogleLoading = false
                            Toast.makeText(context, "Sign-in successful", Toast.LENGTH_SHORT).show()

                        },
                        onSignInFailure = {
                            // Handle sign-in failure
                            Toast.makeText(context, "Sign-in failed: $it", Toast.LENGTH_SHORT).show()
                            isGoogleLoading = false
                        },
                        navController
                    )
                    GitAuth(
                        firebaseAuth = firebaseAuth,
                        onSignInSuccess = {
                            isGithubLoading = false
                            Toast.makeText(context, "Sign-in successful", Toast.LENGTH_SHORT).show()
                            navController.navigate("moredetails")
                        },

                        onSignInFailure = {
                            // Handle sign-in failure
                            Toast.makeText(context, "Sign-in failed: $it", Toast.LENGTH_SHORT).show()
                            isGithubLoading = false
                        },
                        navController

                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Or", style = CC.descriptionTextStyle)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Enter your Credentials below ",style = CC.descriptionTextStyle)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                AnimatedVisibility(visible = isSigningUp) {
                    Column {
                        Text(
                            "Name",
                            fontSize = 16.sp,
                            color = GlobalColors.textColor,
                            modifier = Modifier.padding(start = 50.dp, top = 20.dp)
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CC.SingleLinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = "Name",
                                singleLine = true
                            )
                        }
                    }
                }

                Text(
                    "Email",
                    fontSize = 16.sp,
                    color = GlobalColors.textColor,
                    modifier = Modifier.padding(start = 50.dp, top = 20.dp)
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CC.SingleLinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        singleLine = true
                    )
                }

                Text(
                    "Password",
                    fontSize = 16.sp,
                    color = GlobalColors.textColor,
                    modifier = Modifier.padding(start = 50.dp, top = 20.dp)
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CC.PasswordTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        singleLine = true,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp, color = GlobalColors.textColor, shape = RoundedCornerShape(10.dp)
                        )
                        .background(GlobalColors.secondaryColor, shape = RoundedCornerShape(10.dp))
                        .height(50.dp)
                        .width(300.dp)
                ) {
                    Button(
                        onClick = {
                            if (isSigningUp) {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                registrationSuccess = true
                                                MyDatabase.writeNewUser(
                                                    auth.currentUser!!.uid, name, email
                                                )
                                                Toast.makeText(
                                                    context,
                                                    "Registration successful!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Authentication failed.",
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        context, "Please fill all fields", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                loginSuccess = true
                                                Toast.makeText(
                                                    context, "Sign In successful!", Toast.LENGTH_SHORT
                                                ).show()

                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Authentication failed.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                feedbackMessage = "Invalid email or password"
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        context, "Please fill all fields", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Text(if (isSigningUp) "Sign Up" else "Sign In")
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    if (isSigningUp) "Already have an account? " else "Don't have an account?",
                    style = CC.descriptionTextStyle
                )
                Text(
                    if (isSigningUp) "Sign In" else "Sign Up",
                    style = CC.descriptionTextStyle,
                    modifier = Modifier.clickable { isSigningUp = !isSigningUp }
                )
            }
            if (isSigningUp) {
                Row(modifier = Modifier
                    .clickable { navController.navigate("reset") }
                    .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(
                        "Forgot Password? Reset",
                        fontSize = 16.sp,
                        color = GlobalColors.textColor,
                    )
                }

            }
        }

    }
}

@Preview
@Composable
fun SignInScreenPreview() {
    AuthenticationScreen(rememberNavController(), LocalContext.current)
}