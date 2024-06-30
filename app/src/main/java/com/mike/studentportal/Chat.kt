package com.mike.studentportal

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mike.studentportal.MyDatabase.fetchUserDataByEmail
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.mike.studentportal.CommonComponents as CC


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    context: Context
) {
    var user by remember { mutableStateOf(User()) }
    var message by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var previousChatSize by remember { mutableIntStateOf(0) }
    var isDeleting by remember { mutableStateOf(false) }
    var showdialog by remember { mutableStateOf(true) }
    var chats by remember { mutableStateOf(emptyList<Chat>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var currentName by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }
    var currentAdmissionNumber by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Fetch user data when the composable is launched
    LaunchedEffect(currentUser?.email) {
        currentUser?.email?.let { email ->
            fetchUserDataByEmail(email) { fetchedUser ->
                fetchedUser?.let {
                    user = it
                    currentName = it.name
                    currentEmail = it.email
                    currentAdmissionNumber = it.id
                }
            }
            showdialog = false
        }

    }

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
            delay(1)
            fetchChats()
            if (chats.size != previousChatSize) {
                previousChatSize = chats.size
            }
        }
    }

    fun sendMessage(message: String) {
        try {
            val newChat = Chat(
                message = message,
                sender = currentName,
                time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
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
        } catch (e: Exception) {
            errorMessage = e.message
            scope.launch {
                snackbarHostState.showSnackbar("Failed to send message: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discussions", style = CC.titleTextStyle(context)) },
                actions = {
                    IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = GlobalColors.textColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = GlobalColors.textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalColors.primaryColor)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GlobalColors.primaryColor)
                    .padding(paddingValues)
                    .padding(8.dp)
            ) {
                if (showdialog) {
                    BasicAlertDialog(
                        onDismissRequest = { showdialog = false },
                        modifier = Modifier.border(1.dp, GlobalColors.textColor, RoundedCornerShape(10.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .width(200.dp)
                                .background(GlobalColors.secondaryColor, RoundedCornerShape(10.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Silent Conversation",
                                style = CC.titleTextStyle(context)
                                    .copy(fontSize = 20.sp, textAlign = TextAlign.Center),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "The discussions are silent. You will not receive any notifications of incoming messages. " +
                                        "The messages will only load while you are on this screen.",
                                style = CC.descriptionTextStyle(context)
                                    .copy(fontSize = 16.sp, textAlign = TextAlign.Center),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = { showdialog = false },
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(top = 16.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(GlobalColors.extraColor2)
                            ) {
                                Text("Ok")
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isSearchVisible, enter = fadeIn(), exit = fadeOut()
                ) {
                    TextField(
                        value = searchQuery,
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
                                        text = date,
                                        modifier = Modifier.padding(5.dp),
                                        style = CC.descriptionTextStyle(context),
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
                                        text = "The messages are NOT end-to-end encrypted. The admin can choose " +
                                                "to modify or delete chats not related to Education",
                                        modifier = Modifier.padding(5.dp),
                                        style = CC.descriptionTextStyle(context),
                                        textAlign = TextAlign.Center,
                                        color = GlobalColors.textColor
                                    )
                                }
                            }
                        }

                        items(chatsForDate.filter {
                            it.message.contains(searchQuery.text, ignoreCase = true)
                        }) { chat ->
                            ChatBubble(
                                chat = chat,
                                isUser = chat.sender == user.name,
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
                    CC.SingleLinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = "Message",
                        enabled = true,
                        singleLine = true,
                        context = context
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (message.isNotBlank() && user.name.isNotBlank()) {
                                sendMessage(message)
                                message = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.extraColor2),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Send")
                    }
                }
            }
            }
        }
    )
}

@Composable
fun ChatBubble(
    chat: Chat,
    isUser: Boolean,
    context: Context,
) {
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val backgroundColor = if (isUser) GlobalColors.extraColor1 else GlobalColors.extraColor2
    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 0.dp,
        bottomEnd = if (isUser) 0.dp else 16.dp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor, bubbleShape)
                .padding(8.dp)
        ) {
            Column {
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        // Icon with first letter of sender's name
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(GlobalColors.primaryColor, CircleShape)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chat.sender.first().toString(),
                                style = CC.descriptionTextStyle(context),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // Sender name
                        Text(
                            text = chat.sender,
                            style = CC.descriptionTextStyle(context),
                            color = GlobalColors.primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = chat.message,
                    style = CC.descriptionTextStyle(context)
                )
                Text(
                    text = chat.time,
                    style = CC.descriptionTextStyle(context),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewMyScreen() {
    ChatScreen(rememberNavController(), LocalContext.current)
//    ChatBubble(
//        chat = Chat(
//            sender = "Michael", message = "Hello there", time = "10:00", date = "2023-08-01"
//        ), isUser = true, context = LocalContext.current
//    )
}
