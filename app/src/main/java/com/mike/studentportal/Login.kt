package com.mike.studentportal

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.mike.studentportal.MyDatabase.fetchUserDataByEmail
import com.mike.studentportal.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(navController: NavController, context: Context) {
    var user by remember { mutableStateOf<User?>(null) }
    var currentName by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }
    var currentAdmissionNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSigningUp by remember { mutableStateOf(true) }
    var isGithubLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    val firebaseAuth = FirebaseAuth.getInstance()
    var visible by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }
    val backbrush = Brush.verticalGradient(
        colors = listOf(
            GlobalColors.primaryColor,
            GlobalColors.secondaryColor
        )
    )

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }), // Slide in from right
        exit = slideOutHorizontally(targetOffsetX = { -it }) // Slide out to left
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isSigningUp) "Sign Up" else "Sign In",
                            style = CC.titleTextStyle(context)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = GlobalColors.primaryColor
                    )
                )
            },
            containerColor = GlobalColors.primaryColor
        ) { it ->
            Column(
                modifier = Modifier
                    .padding(it)
                    .background(backbrush)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .width(350.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Continue with one of the following",
                        style = CC.descriptionTextStyle(context)
                    )

                    Row(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GoogleAuth(
                            firebaseAuth = firebaseAuth,
                            onSignInSuccess = {
                                Details.email.value = CC.getCurrentUser()
                                fetchUserDataByEmail(CC.getCurrentUser()) { fetchedUser ->
                                    fetchedUser?.let {
                                        user = it
                                        currentName = it.name
                                        currentEmail = it.email
                                        currentAdmissionNumber = it.id
                                    }
                                }
                                Details.name.value = currentName

                                Toast.makeText(
                                    context,
                                    "Sign-in successful: $email",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("moredetails")
                                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {

                                        Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                                        return@OnCompleteListener
                                    }
                                    // retrieve device token and send to database
                                    val token = task.result
                                    MyDatabase.writeFcmToken(token = Fcm(token = token))
                                })
                            },
                            onSignInFailure = {
                                Toast.makeText(context, "Sign-in failed: $it", Toast.LENGTH_SHORT)
                                    .show()
                                isGoogleLoading = false
                            },
                            navController
                        )
                        GitAuth(
                            firebaseAuth = firebaseAuth,
                            onSignInSuccess = {
                                Toast.makeText(context, "Sign-in successful", Toast.LENGTH_SHORT)
                                    .show()

                                Details.email.value = CC.getCurrentUser()
                                navController.navigate("moredetails")
                                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {

                                        Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                                        return@OnCompleteListener
                                    }
                                    // retrieve device token and send to database
                                    val token = task.result
                                    MyDatabase.writeFcmToken(token = Fcm(token = token))
                                })
                            },
                            onSignInFailure = {
                                Toast.makeText(context, "Sign-in failed: $it", Toast.LENGTH_SHORT)
                                    .show()
                                isGithubLoading = false
                            },
                            navController
                        )
                    }

                    Text(
                        text = "Or",
                        style = CC.descriptionTextStyle(context),
                        color = GlobalColors.textColor
                    )
                    Text(if(isSigningUp)"Sign up with your email and password" else "Sign in with your email and password", style = CC.descriptionTextStyle(context))

                    AnimatedContent(targetState = isSigningUp, transitionSpec = {
                        fadeIn(animationSpec = tween(300)) + slideInVertically() with
                                fadeOut(animationSpec = tween(300)) + slideOutVertically()
                    }, label = "") { targetState ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (targetState) {
                                Spacer(modifier = Modifier.height(20.dp))
                                CC.SingleLinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = "Name",
                                    singleLine = true,
                                    context = context
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            CC.SingleLinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = "Email",
                                singleLine = true,
                                context = context
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            CC.PasswordTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = "Password",
                                context = context
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = GlobalColors.textColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            GlobalColors.secondaryColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .height(50.dp)
                        .width(300.dp)
                ) {
                    Button(
                        onClick = {
                            if (isSigningUp) {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    loading = true
                                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                loading = false
                                                Toast.makeText(
                                                    context,
                                                    "Registration successful!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                MyDatabase.writeUsers(
                                                    User(
                                                        name = name,
                                                        email = email
                                                    )
                                                )
                                                Details.name.value = name
                                                name = ""
                                                email = ""
                                                password = ""
                                                navController.navigate("dashboard")
                                            } else {
                                                loading = false
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
                                    loading = true
                                    firebaseAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                loading = false
                                                Toast.makeText(
                                                    context,
                                                    "Sign In successful!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                Details.name.value = name
                                                name = ""
                                                email = ""
                                                password = ""
                                                navController.navigate("dashboard")
                                                FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                                    OnCompleteListener { taask ->
                                                    if (!taask.isSuccessful) {

                                                        Log.w("FCM", "Fetching FCM registration token failed", taask.exception)
                                                        return@OnCompleteListener
                                                    }
                                                    // retrieve device token and send to database
                                                    val token = taask.result
                                                    MyDatabase.writeFcmToken(token = Fcm(token = token))
                                                })
                                            } else {
                                                loading = false
                                                Toast.makeText(
                                                    context,
                                                    "Authentication failed.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
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
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(30.dp),
                                    color = GlobalColors.primaryColor,
                                    trackColor = GlobalColors.textColor
                                )
                            } else {
                                Text(if (isSigningUp) "Sign Up" else "Sign In", style = CC.descriptionTextStyle(context = context))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedVisibility(visible = !isSigningUp) {
                    Text(
                        text = "Forgot Password? Reset",
                        fontSize = 16.sp,
                        color = GlobalColors.textColor,
                        modifier = Modifier.clickable { navController.navigate("passwordreset") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSigningUp) "Already have an account? " else "Don't have an account?",
                        style = CC.descriptionTextStyle(context),
                        fontWeight = FontWeight.Bold,
                        color = GlobalColors.textColor,
                        modifier = Modifier.padding(5.dp)
                    )
                    Text(
                        text = if (isSigningUp) "Sign In" else "Sign Up",
                        style = CC.descriptionTextStyle(context).copy(fontWeight = FontWeight.Bold),
                        color = GlobalColors.tertiaryColor,
                        modifier = Modifier.clickable { isSigningUp = !isSigningUp }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SignInScreenPreview() {
    LoginScreen(rememberNavController(), LocalContext.current)
}
