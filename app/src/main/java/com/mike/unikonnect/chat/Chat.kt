package com.mike.unikonnect.chat

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mike.unikonnect.MyDatabase
import com.mike.unikonnect.MyDatabase.fetchUserDataByEmail
import com.mike.unikonnect.classes.Chat
import com.mike.unikonnect.classes.ScreenTime
import com.mike.unikonnect.classes.User
import com.mike.unikonnect.homescreen.Background
import com.mike.unikonnect.ui.theme.GlobalColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.mike.unikonnect.CommonComponents as CC

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyChatApp(this, rememberNavController())
        }
    }
}

@Composable
fun MyChatApp(context: Context, navController: NavController){
    val isSearchVisible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
       TopBar(context, navController, isSearchVisible, onValuechange = {!isSearchVisible})
       ChatScreen(navController, context)
    }
}

@Composable
fun ChatScreen(
    navController: NavController, context: Context
) {
    var user by remember { mutableStateOf(User()) }
    var message by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    var previousChatSize by remember { mutableIntStateOf(0) }
    var chats by remember { mutableStateOf(emptyList<Chat>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var currentName by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }
    var currentAdmissionNumber by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timeSpent by remember { mutableLongStateOf(0L) }
    val screenID = "SC6"

    val scrollState = rememberLazyListState()

    LaunchedEffect(currentUser?.email) {
        currentUser?.email?.let { email ->
            fetchUserDataByEmail(email) { fetchedUser ->
                fetchedUser?.let {
                    user = it
                    currentName = it.firstName
                    currentEmail = it.email
                    currentAdmissionNumber = it.id
                }
            }
        }
        while (true) {
            timeSpent = System.currentTimeMillis() - startTime
            delay(1000) // Update every second (adjust as needed)
        }
    }

    ExitScreen(
        context,
        screenID,
        timeSpent
    )

    fun fetchChats() {
        try {
            MyDatabase.fetchChats { fetchedChats ->
                chats = fetchedChats
            }
        } catch (e: Exception) {
            errorMessage = e.message
            scope.launch {
                snackbarHostState.showSnackbar("Failed to fetch chats: ${e.message}")
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(10) // Adjust the delay as needed
            fetchChats()
            if (chats.size != previousChatSize) {
                previousChatSize = chats.size
            }
        }
    }

    LaunchedEffect(chats.size) {
        if (chats.isNotEmpty()) {
            scrollState.animateScrollToItem(chats.size - 1)
        }
    }

    fun formatDate(dateString: String): String {
        val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val yesterday = SimpleDateFormat(
            "dd-MM-yyyy", Locale.getDefault()
        ).format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)) // Yesterday's date

        return when (dateString) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> dateString
        }
    }

    fun sendMessage(message: String) {
        try {
            MyDatabase.generateChatID { chatId ->
                val newChat = Chat(
                    id = chatId,
                    message = message,
                    senderName = currentName,
                    senderID = currentAdmissionNumber,
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                    date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
                    profileImageLink = ""
                )
                MyDatabase.sendMessage(newChat) { success ->
                    if (success) {
                        fetchChats()
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Failed to send message")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message
            scope.launch {
                snackbarHostState.showSnackbar("Failed to send message: ${e.message}")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Background(context)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp)
                .padding(8.dp)
        ) {

            RowMessage(context)
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                state = scrollState
            ) {
                val groupedChats = chats.groupBy { it.date }

                groupedChats.forEach { (date, chatsForDate) ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        GlobalColors.secondaryColor, RoundedCornerShape(10.dp)
                                    )
                                    .clip(RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = formatDate(date),
                                    modifier = Modifier.padding(5.dp),
                                    style = CC.descriptionTextStyle(context),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(chatsForDate.filter {
                        it.message.contains(searchQuery.text, ignoreCase = true)
                    }) { chat ->
                        ChatBubble(
                            chat = chat,
                            isUser = chat.senderID == currentAdmissionNumber,
                            context = context,
                            navController = navController
                        )
                    }
                }
            }

            ChatInput(
                modifier = Modifier.fillMaxWidth(),
                onMessageChange = { message = it },
                sendMessage = { sendMessage(message) },
                context
            )
        }
    }
}



@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ChatBubble(
    chat: Chat, isUser: Boolean, context: Context, navController: NavController
) {
    val alignment = if (isUser) Alignment.TopEnd else Alignment.TopStart
    val backgroundColor = if (isUser) GlobalColors.extraColor1 else GlobalColors.extraColor2
    val bubbleShape = RoundedCornerShape(
        bottomStart = 16.dp,
        bottomEnd = 16.dp,
        topStart = if (isUser) 16.dp else 0.dp,
        topEnd = if (isUser) 0.dp else 16.dp
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor, bubbleShape)
                .padding(8.dp)
                .align(alignment)
        ) {
            Column {
                if (!isUser) {
                    Row(
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = chat.senderName,
                            style = CC.descriptionTextStyle(context),
                            fontWeight = FontWeight.Bold,
                            color = GlobalColors.primaryColor
                        )
                    }
                }
                Text(
                    text = chat.message, style = CC.descriptionTextStyle(context)
                )
                Text(
                    text = chat.time,
                    style = CC.descriptionTextStyle(context),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
        if (!isUser) {
            // Icon with first letter of sender's name, positioned outside the bubble
            Box(modifier = Modifier
                .clickable {
                    navController.navigate("chat/${chat.senderID}")
                }
                .offset(x = (-16).dp, y = (-16).dp)
                .size(24.dp)
                .background(GlobalColors.primaryColor, CircleShape)
                .padding(4.dp),
                contentAlignment = Alignment.Center) {

                Text(
                    text = chat.senderName.first().toString(),
                    style = CC.descriptionTextStyle(context),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier

                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(context: Context, navController: NavController, isSearchVisible: Boolean, onValuechange:(Boolean) -> Unit){
    TopAppBar(title = { Text("Group Discussions", style = CC.titleTextStyle(context)) },
        actions = {
            IconButton(onClick = { onValuechange(isSearchVisible) }) {
                Icon(
                    Icons.Filled.Search, contentDescription = "Search", tint = CC.textColor()
                )
            }
            IconButton(onClick = { navController.navigate("users") }) {
                Icon(
                    Icons.Filled.Person, "Participants", tint = CC.textColor()
                )
            }
        },

        navigationIcon = {
            IconButton(onClick = { navController.navigate("dashboard") }) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = CC.textColor()
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalColors.primaryColor)
    )
}

@Preview
@Composable
fun MyPreview() {
    CompositionLocalProvider(LocalContext provides LocalContext.current) {
        ChatScreen(
            navController = rememberNavController(),
            LocalContext.current
        ) // The Composable that uses the CompositionLocal
    }
}
