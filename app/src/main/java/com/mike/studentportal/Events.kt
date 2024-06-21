package com.mike.studentportal


import android.content.Context
import android.icu.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mike.studentportal.MyDatabase.getEvents
import com.mike.studentportal.CommonComponents as CC


@Composable
fun EventScreen(navController: NavController, context: Context) {

    var isLoading by rememberSaveable { mutableStateOf(true) }
    val events = remember { mutableStateListOf<Event>() }

    LaunchedEffect(Unit) {
        getEvents { fetchedEvents ->
            events.addAll(fetchedEvents ?: emptyList())
            isLoading = false
        }
    }
    Details.totalAnnouncements.value = events.size

    val showNotification = remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)
    val date = "$day/$month/$year"
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = {
            isLoading = true
            getEvents { fetchedEvents ->
                events.clear()
                events.addAll(fetchedEvents ?: emptyList())
                isLoading = false
            }
        },
            containerColor = GlobalColors.tertiaryColor,
            contentColor = GlobalColors.primaryColor,
            content = {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            })
    }) {
        Column(
            modifier = Modifier
                .background(CC.backbrush)
                .fillMaxSize()
                .padding(it)
        ) {

            if (isLoading) {
                Column(
                    modifier = Modifier
                        .background(GlobalColors.primaryColor)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MyProgress()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading Events...", style = CC.descriptionTextStyle(context))
                }
            } else if (events.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("😒", fontSize = 50.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No Events found.",
                        style = CC.descriptionTextStyle(context),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                NotificationCard(
                    title = title, message = description, visibleState = showNotification, context
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(events, key = { event -> event.id }) { event ->
                        EventCard(
                            event = event, context = context
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EventCard(
    event: Event, context: Context

) {

    var expanded by remember { mutableStateOf(false) }
    val text = if (expanded) "Close" else "Open"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlobalColors.secondaryColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.student),
                contentDescription = "Announcement Icon",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp),
            )
            Text(
                text = event.title,
                style = CC.descriptionTextStyle(context),
                fontWeight = FontWeight.Bold,
                color = GlobalColors.textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { expanded = !expanded },
                colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.primaryColor)
            ) {
                Text(text, style = CC.descriptionTextStyle(context))
            }
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description,
                style = CC.descriptionTextStyle(context).copy(fontSize = 14.sp),
                color = GlobalColors.textColor.copy(alpha = 0.8f),
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.author,
                    style = CC.descriptionTextStyle(context).copy(fontSize = 12.sp),
                    color = GlobalColors.textColor.copy(alpha = 0.6f),
                )
                Text(
                    text = event.date,
                    style = CC.descriptionTextStyle(context).copy(fontSize = 12.sp),
                    color = GlobalColors.textColor.copy(alpha = 0.6f),
                )
            }
        }
    }
}


@Preview
@Composable
fun EventsPreview() {
    EventScreen(
        navController = rememberNavController(), context = LocalContext.current,
    )
}