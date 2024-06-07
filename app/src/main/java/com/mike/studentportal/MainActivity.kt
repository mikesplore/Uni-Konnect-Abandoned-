package com.mike.studentportal

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavHost()


        }
    }


@Composable
fun NavHost(){
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = "authentication") {

        composable("authentication") {
            AuthenticationScreen(navController = navController, context)
        }
        composable("dashboard") {
            Dashboard(navController = navController, context)
        }
        composable("reset") {
            PasswordReset(navController = navController, context)
        }
        composable("moredetails") {
            MoreDetails(context, navController = navController)
        }

    }



}
}