package com.mike.studentportal

import android.content.Context
import android.net.Uri
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.mike.studentportal.MyDatabase.fetchUserDataByEmail
import kotlin.random.Random
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
                .verticalScroll(rememberScrollState())
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
            Spacer(modifier = Modifier.height(50.dp))
            DangerZone(navController, context)


        }
    }

}

@Composable
fun DisplayImage(context: Context) {
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(User()) }
    val user = auth.currentUser
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
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
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var currentUser by remember { mutableStateOf(User()) }
    var currentFirstName by remember { mutableStateOf(currentUser.firstName) }
    var currentLastName by remember { mutableStateOf(currentUser.lastName) }
    var currentPhone by remember { mutableStateOf(currentUser.phoneNumber) }
    var isEditing by remember { mutableStateOf(false) }
    var currentGender by remember { mutableStateOf(currentUser.gender) }
    var currentEmail by remember { mutableStateOf(currentUser.email) }
    var currentAdmissionNumber by remember { mutableStateOf(currentUser.id) }

    LaunchedEffect(Unit) {
        user?.email?.let { email ->
            fetchUserDataByEmail(email) { fetchedUser ->
                fetchedUser?.let {
                    currentUser = it
                    Log.d("Current Phone","fetched User  is: $it")
                    currentFirstName = it.firstName
                    Log.d("Current Phone","fetched first name  is: ${it.firstName}")
                    currentLastName = it.lastName
                    Log.d("Current Phone","fetched last name is: ${it.lastName}")
                    currentPhone = it.phoneNumber
                    Log.d("Current Phone","fetched phone number is: ${it.phoneNumber}")
                    currentGender = it.gender
                    Log.d("Current Phone","fetched gender is: ${it.gender}")
                    currentEmail = it.email
                    Log.d("Current Phone","fetched email is: ${it.email}")
                    currentAdmissionNumber = it.id
                    Log.d("Current Phone","fetched admission number is: ${it.id}")
                }
            }
        }
    }

    fun saveUserData() {
        MyDatabase.writeUsers(
            user = User(
                id = currentAdmissionNumber,
                firstName = currentFirstName,
                lastName = currentLastName,
                phoneNumber = currentPhone,
                gender = currentGender,
                email = currentEmail,
                isAdmin = currentUser.isAdmin
            )
        ) {
            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth(0.9f)) {
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                if (isEditing) {
                    saveUserData()
                }
                isEditing = !isEditing
            }) {
                Icon(
                    if (isEditing) Icons.Filled.Check else Icons.Default.Edit,
                    contentDescription = "save",
                    tint = GlobalColors.textColor
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        MyDetails(
            title = "First Name",
            value = currentFirstName,
            onValueChange = { currentFirstName = it },
            context = context,
            isEditing = isEditing
        )

        Spacer(modifier = Modifier.height(10.dp))

        MyDetails(
            title = "Last Name",
            value = currentLastName,
            onValueChange = { currentLastName = it },
            context = context,
            isEditing = isEditing
        )

        Spacer(modifier = Modifier.height(10.dp))

        MyDetails(
            title = "Email",
            value = currentEmail,
            onValueChange = {},
            context = context,
            isEditing = isEditing,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        MyDetails(
            title = "Phone Number",
            value = currentPhone,
            onValueChange = { currentPhone = it },
            context = context,
            isEditing = isEditing
        )
    }
}


@Composable
fun MyDetails(title: String, value: String, onValueChange: (String) -> Unit, context: Context, fontSize: TextUnit = 18.sp, isEditing: Boolean){
    Row(modifier = Modifier
        .height(60.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Row(modifier = Modifier.width(100.dp)) {
            Text(title, style = CC.descriptionTextStyle(context))
        }

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
                disabledContainerColor = Color.Transparent
            ),
            enabled = isEditing,
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(60.dp)
        )
    }
}

@Composable
fun GenderRow(context: Context) {
    var selectedMale by remember { mutableStateOf(false) }
    var selectedFemale by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf(User()) }
    var save by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    var phoneNumber by remember { mutableStateOf(currentUser.phoneNumber) }
    val user = auth.currentUser
    var gender by remember { mutableStateOf("") }
    gender = if(!selectedFemale && !selectedMale){
        "not set"
    }else if(!selectedFemale ){
        "Male"
    }
    else{
        "Female"
    }
    if (gender == "Male"){
        selectedMale = true
    }
    else{
        selectedFemale = true
    }

    LaunchedEffect(user?.email) {
        user?.email?.let { it ->
            fetchUserDataByEmail(it) { fetchedUser ->
                fetchedUser?.let {
                    currentUser = it
                    gender = currentUser.gender

                }
            }
        }
    }
    if(save){
        MyDatabase.writeUsers(
            user = User(
                id = currentUser.id,
                firstName = currentUser.firstName,
                lastName = currentUser.lastName,
                phoneNumber = currentUser.phoneNumber,
                gender = gender,
                email = currentUser.email,
                isAdmin = currentUser.isAdmin
            ),{}
        )
    }
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
                    save = true
                    selectedMale = true
                    selectedFemale = false
                },
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        if (selectedMale) GlobalColors.extraColor1 else GlobalColors.secondaryColor,
                        CircleShape
                    )
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
                    save = true
                    selectedFemale = true
                    selectedMale = false
                },
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        if (selectedFemale) GlobalColors.extraColor1 else GlobalColors.secondaryColor,
                        CircleShape
                    )
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
fun DangerZone(navController: NavController, context: Context) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var showPuzzle by remember { mutableStateOf(false) }
    var puzzleWords by remember { mutableStateOf(generateRandomNonsenseWord()) }
    var userInput by remember { mutableStateOf("") }
    var showWarning by remember { mutableStateOf(false) }
    var deleteConfirmed by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var currentAdmissionNumber by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var currentEmail by remember { mutableStateOf("") }
    LaunchedEffect(user?.email) {
        user?.email?.let {
            fetchUserDataByEmail(it) { fetchedUser ->
                fetchedUser?.let {
                    currentAdmissionNumber = it.id
                    currentEmail = it.email
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
        Row {
            Text("Danger Zone", style = CC.titleTextStyle(context).copy(color = Color.Red.copy(0.7f)))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text("Delete Account", style = CC.descriptionTextStyle(context))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                showPuzzle = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = GlobalColors.secondaryColor
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Solve a Puzzle before proceeding", style = CC.descriptionTextStyle(context))
        }

        if (showPuzzle) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Please enter the following code to confirm account deletion:", style = CC.descriptionTextStyle(context))
            Spacer(modifier = Modifier.height(10.dp))
            Text(puzzleWords, style = CC.titleTextStyle(context), color = GlobalColors.tertiaryColor)
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = userInput,
                textStyle = CC.titleTextStyle(context).copy(fontSize = 18.sp, color = if(isError) Color.Red else GlobalColors.textColor),
                onValueChange = {
                    isError = false
                    userInput = it },
                isError = isError,
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = GlobalColors.tertiaryColor,
                    focusedIndicatorColor = GlobalColors.tertiaryColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = GlobalColors.textColor,
                    unfocusedTextColor = GlobalColors.textColor,
                    errorIndicatorColor = Color.Red,
                    errorContainerColor = GlobalColors.primaryColor
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    if (userInput == puzzleWords) {
                        showWarning = true
                        puzzleWords = generateRandomNonsenseWord()
                        userInput = ""
                    } else{
                        isError = true
                        puzzleWords = generateRandomNonsenseWord()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GlobalColors.secondaryColor
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Proceed", style = CC.descriptionTextStyle(context))
            }
            if (showWarning) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = {
                    deleteConfirmed = true
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    shape = RoundedCornerShape(10.dp)
                    ) {
                    Text("Send Account Deletion Request", style = CC.descriptionTextStyle(context))
                }
                Text(
                    "Your account will be deleted within 3 days. You will have acces to your account upto that period",
                    style = CC.descriptionTextStyle(context),
                    color = Color.Red.copy(0.5f)
                )
            }
        }

        if (deleteConfirmed) {
            loading = true
            MyDatabase.generateAccountDeletionID { id ->
                val account = AccountDeletion(
                    id = id,
                    admissionNumber = currentAdmissionNumber,
                    email = currentEmail

                )
                MyDatabase.writeAccountDeletionData(account) {
                    Log.d("Account Deletion Request", "Request sent Successfully!")
                    loading = false
                    showWarning = false
                    showPuzzle = false
                    Toast.makeText(context, "Account Deletion Request Sent", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

fun generateRandomNonsenseWord(length: Int = 6): String {
    val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('!', '@', '#', '$', '%', '^', '&', '*')
    return (1..length)
        .map { allowedChars.random(Random) }
        .joinToString("")
}


