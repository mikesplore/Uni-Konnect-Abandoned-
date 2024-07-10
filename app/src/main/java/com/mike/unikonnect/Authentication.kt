package com.mike.unikonnect

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mike.unikonnect.CommonComponents as CC

@Composable
fun AuthenticationScreen(navController: NavController, context: Context, activity: MainActivity) {
    var authenticationResult by remember { mutableStateOf<String?>(null) }
    val promptManager = remember { BiometricPromptManager(activity) }
    val currentUser = Firebase.auth.currentUser

    // Check if there's a current user and show biometric prompt if yes
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            promptManager.showBiometricPrompt(
                title = "Authenticate",
                description = "Authenticate to access your account",
                onResult = { result ->
                    authenticationResult = if (result) {
                        "Succeeded"
                    } else {
                        "Failed"
                    }
                }
            )
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CC.primary()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.background(CC.primary()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Crossfade(targetState = authenticationResult, label = "") { result ->
                when (result) {
                    "Succeeded" -> {
                        IconButton(onClick = {},
                            modifier = Modifier
                                .background(CC.secondary(), CircleShape)
                                .size(65.dp)) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Authentication Succeeded",
                            tint = Color.Green,
                            modifier = Modifier.size(64.dp)
                        )}
                        LaunchedEffect(Unit) {
                            //navController.navigate("")
                        }
                    }
                    "Failed" -> {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Authentication Failed",
                            tint = Color.Red,
                            modifier = Modifier.size(64.dp).clickable {
                                promptManager.showBiometricPrompt(
                                    title = "Authenticate",
                                    description = "Authenticate to access your account",
                                    onResult = { result ->
                                        authenticationResult = if (result) {
                                            "Succeeded"
                                        } else {
                                            "Failed"
                                        }
                                    }
                                )
                            }
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Authenticate",
                            tint = CC.secondary(),
                            modifier = Modifier.size(64.dp).clickable {
                                promptManager.showBiometricPrompt(
                                    title = "Authenticate",
                                    description = "Authenticate to access your account",
                                    onResult = { result ->
                                        authenticationResult = if (result) {
                                            "Succeeded"
                                        } else {
                                            "Failed"
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
