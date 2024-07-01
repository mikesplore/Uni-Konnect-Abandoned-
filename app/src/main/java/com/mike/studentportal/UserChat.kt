package com.mike.studentportal

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.mike.studentportal.MyDatabase.fetchUserDataByAdmissionNumber
import com.mike.studentportal.MyDatabase.fetchUserDataByEmail
import com.mike.studentportal.MyDatabase.fetchUserToUserMessages
import com.mike.studentportal.MyDatabase.sendUserToUserMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.mike.studentportal.CommonComponents as CC


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChatScreen(navController: NavController, context: Context, targetUserId: String) {
    var user by remember { mutableStateOf(User()) }
    var user2 by remember { mutableStateOf(User()) }
    var name by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(emptyList<Message>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var currentName by remember { mutableStateOf("") }
    var currentAdmissionNumber by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val fileName = "user_chat_${targetUserId}.json"


    // Search functionality
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Fetch user data when the composable is launched
    LaunchedEffect(currentUser?.email) {
        currentUser?.email?.let { email ->
            fetchUserDataByEmail(email) { fetchedUser ->
                fetchedUser?.let {
                    user = it
                    currentName = it.firstName
                    currentAdmissionNumber = it.id
                }
            }
        }
    }

    LaunchedEffect(targetUserId) {
        fetchUserDataByAdmissionNumber(targetUserId) { fetchedUser ->
            fetchedUser?.let {
                user2 = it
                name = user2.firstName

            }
        }
    }

    // Load old messages from file
    LaunchedEffect(Unit) {
        messages = loadMessagesFromFile(context, fileName)
    }

    // Generate a unique conversation ID for the current user and the target user
    val conversationId =
        "Direct Messages/${generateConversationId(currentAdmissionNumber, targetUserId)}"

    fun fetchMessages(conversationId: String) {
        try {
            fetchUserToUserMessages(conversationId) { fetchedMessages ->
                messages = fetchedMessages
                // Save the new messages to file
                saveMessagesToFile(context, fetchedMessages, fileName)
            }
        } catch (e: Exception) {
            errorMessage = e.message
            scope.launch {
                snackbarHostState.showSnackbar("Failed to fetch messages: ${e.message}")
                Log.e(
                    "UserChatScreen",
                    "Failed to fetch messages from $conversationId: ${e.message}",
                    e
                )
            }
        }
    }

    // Periodically fetch messages
    LaunchedEffect(conversationId) {
        while (true) {
            fetchMessages(conversationId)
            delay(10) // Adjust the delay as needed
        }
    }
    // Format the date string
    fun formatDate(dateString: String): String {
        val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val yesterday = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        ).format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)) // Yesterday's date

        return when (dateString) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> dateString
        }
    }

    fun sendMessage(messageContent: String) {
        try {
            MyDatabase.generateChatID { chatId ->
            val newMessage = Message(
                id = chatId,
                message = messageContent,
                senderName = user.firstName,
                senderID = currentAdmissionNumber,
                recipientID = targetUserId,
                time = SimpleDateFormat("hh:mm A", Locale.getDefault()).format(Date()),
                date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            )
            sendUserToUserMessage(newMessage, conversationId) { success ->
                if (success) {
                    fetchMessages(conversationId)
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

    Scaffold(topBar = {
        TopAppBar(title = { Text(name, style = CC.titleTextStyle(context)) },
            actions = {
                IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.width(100.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = GlobalColors.textColor,
                            modifier = Modifier.size(24.dp) // Adjust size as needed
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(
                            painter = painterResource(R.drawable.student),
                            contentDescription = "student",
                            modifier = Modifier.size(50.dp) // Match the Icon size
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalColors.primaryColor)
        )
    }, content = { paddingValues ->
        Box(

        ) {
            Background(context)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isSearchVisible) {
                    TextField(value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search Chats") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GlobalColors.primaryColor,
                            unfocusedIndicatorColor = GlobalColors.textColor,
                            focusedIndicatorColor = GlobalColors.secondaryColor,
                            unfocusedContainerColor = GlobalColors.primaryColor,
                            focusedTextColor = GlobalColors.textColor,
                            unfocusedTextColor = GlobalColors.textColor,
                            focusedLabelColor = GlobalColors.secondaryColor,
                            unfocusedLabelColor = GlobalColors.textColor
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    val groupedMessages = messages.groupBy { it.date }

                    groupedMessages.forEach { (date, chatsForDate) ->
                        item {
                            // Display date header
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
                                        text = "Chats are end-to-end encrypted",
                                        modifier = Modifier.padding(5.dp),
                                        style = CC.descriptionTextStyle(context),
                                        textAlign = TextAlign.Center,
                                        color = GlobalColors.textColor
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        items(chatsForDate.filter {
                            it.message.contains(searchQuery, ignoreCase = true)
                        }) { chat ->
                            MessageBubble(
                                message = chat,
                                isUser = chat.senderID == currentAdmissionNumber,
                                context = context
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(value = message,
                        textStyle = CC.descriptionTextStyle(context),
                        onValueChange = { message = it },
                        label = { Text("Message", style = CC.descriptionTextStyle(context)) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GlobalColors.primaryColor,
                            unfocusedContainerColor = GlobalColors.primaryColor,
                            cursorColor = GlobalColors.textColor,
                            focusedTextColor = GlobalColors.textColor,
                            unfocusedTextColor = GlobalColors.textColor,
                            focusedLabelColor = GlobalColors.textColor,
                            unfocusedLabelColor = GlobalColors.textColor,
                            focusedIndicatorColor = GlobalColors.secondaryColor,
                            unfocusedIndicatorColor = GlobalColors.secondaryColor
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (message.isNotBlank() && user.firstName.isNotBlank()) {
                                sendMessage(message)
                                message = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.extraColor2),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send,"Send")
                    }
                }
            }
        }
    })
}

// Other functions and components remain unchanged


fun generateConversationId(userId1: String, userId2: String): String {
    return if (userId1 < userId2) {
        "$userId1$userId2"
    } else {
        "$userId2$userId1"
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MessageBubble(
    message: Message,
    isUser: Boolean,
    context: Context,
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
                Text(
                    text = message.message, style = CC.descriptionTextStyle(context)
                )
                Text(
                    text = message.time,
                    style = CC.descriptionTextStyle(context),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

