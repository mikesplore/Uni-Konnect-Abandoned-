package com.mike.studentportal

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.mike.studentportal.MyDatabase.fetchUserDataByEmail
import com.mike.studentportal.CommonComponents as CC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, context: Context){
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(User()) }

    LaunchedEffect(auth.currentUser?.email) {
        auth.currentUser?.email?.let {
            fetchUserDataByEmail(it) { fetchedUser ->
                fetchedUser?.let {
                    currentUser = it
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,"Back",
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
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Profile", style = CC.titleTextStyle(context), fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            DisplayImage(context)
            Spacer(modifier = Modifier.height(20.dp))
            ProfileDetails(navController, context)
            Spacer(modifier = Modifier.height(20.dp))
            GenderRow(context)


        }
    }

}

@Composable
fun DisplayImage(context: Context) {
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(User()) }
    val user = auth.currentUser
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }
    var signInMethod by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
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

    LaunchedEffect(user?.email) {
        user?.email?.let {
            fetchUserDataByEmail(it) { fetchedUser ->
                fetchedUser?.let {
                    currentUser = it
                    // Try to load image URI from SharedPreferences (if available)
                    val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    val imageUriString = sharedPreferences.getString("profile_image_uri", null)
                    selectedImageUri = if (imageUriString != null) Uri.parse(imageUriString) else null
                }
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth(0.9f)) {
        Text("Profile Picture", style = CC.descriptionTextStyle(context))
    }

    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user?.photoUrl != null && user.photoUrl.toString().isNotEmpty()) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else if (selectedImageUri != null && signInMethod == "password") {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                tint = Color.Gray, // Or your preferred color
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GlobalColors.secondaryColor
            ),
            enabled = selectedImageUri == null || signInMethod == "password"
        ) {
            Text("Upload Image", style = CC.descriptionTextStyle(context))
        }
    }

    // Store image URI in SharedPreferences when it changes
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let {
            val sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("profile_image_uri", it.toString()).apply()
        }
    }
}


@Composable
fun ProfileDetails(navController: NavController, context: Context){
    val auth= FirebaseAuth.getInstance()
    val user = auth.currentUser
    var currentUser by remember { mutableStateOf(User()) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    LaunchedEffect(user?.email) {
        user?.email?.let {
            fetchUserDataByEmail(it) { fetchedUser ->
                fetchedUser?.let {
                    currentUser = it
                    firstName = it.firstName
                    lastName = it.lastName
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
    MyDetails(
        title = "First Name",
        value = firstName,
        onValueChange = { firstName = it },
        context = context
    )
    Spacer(modifier = Modifier.height(10.dp))
    MyDetails(
        title = "Last Name",
        value = lastName,
        onValueChange = { lastName = it },
        context = context
    )
    Spacer(modifier = Modifier.height(10.dp))
    MyDetails(
        title = "Email",
        value = user?.email ?: "",
        onValueChange = {},
        context = context
    )
    Spacer(modifier = Modifier.height(10.dp))
    MyDetails(
        title = "Phone Number",
        value = user?.phoneNumber ?: "",
        onValueChange = {},
        context
    )

}

@Composable
fun MyDetails(title: String, value: String, onValueChange: (String) -> Unit, context: Context, fontSize: TextUnit = 18.sp){
    Row(modifier = Modifier
        .height(60.dp)
        .fillMaxWidth(.8f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = CC.descriptionTextStyle(context))
        TextField(
            value = value,
            textStyle = CC.titleTextStyle(context).copy(fontSize = fontSize),
            onValueChange = onValueChange, // Pass the callback directly
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = GlobalColors.tertiaryColor,
                focusedIndicatorColor = GlobalColors.tertiaryColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = GlobalColors.textColor,
                unfocusedTextColor = GlobalColors.textColor,
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(60.dp)
        )
    }
}

@Composable
fun GenderRow(context: Context) {
    var selectedMale by remember { mutableStateOf(false) }
    var selectedFemale by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth(0.8f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Gender", style = CC.descriptionTextStyle(context))
        Spacer(modifier = Modifier.width(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    selectedMale = true
                    selectedFemale = false
                },
                modifier = Modifier
                    .size(60.dp)
                    .background(if (selectedMale) GlobalColors.extraColor1 else GlobalColors.secondaryColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Male,
                    contentDescription = "Male",
                    tint = if (selectedMale) GlobalColors.extraColor2 else GlobalColors.extraColor1,
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
            IconButton(
                onClick = {
                    selectedFemale = true
                    selectedMale = false
                },
                modifier = Modifier
                    .size(60.dp)
                    .background(if (selectedFemale) GlobalColors.extraColor1 else GlobalColors.secondaryColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Female,
                    contentDescription = "Female",
                    tint = if (selectedFemale) GlobalColors.extraColor2 else GlobalColors.extraColor1,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Composable
fun DangerZone(context: Context){
    Row {
        Text("Danger Zone", style = CC.titleTextStyle(context))
    }
}
