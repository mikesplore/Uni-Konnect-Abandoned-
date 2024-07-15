package com.mike.unikonnect.announcements


import android.content.Context
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.mike.unikonnect.homescreen.ColorProgressIndicator
import com.mike.unikonnect.ui.theme.GlobalColors
import com.mike.unikonnect.MyDatabase
import com.mike.unikonnect.MyDatabase.getAnnouncements
import com.mike.unikonnect.R
import com.mike.unikonnect.classes.Announcement
import com.mike.unikonnect.classes.ScreenTime
import kotlinx.coroutines.delay
import com.mike.unikonnect.CommonComponents as CC

object Details {
    var email: MutableState<String> = mutableStateOf("")
    var firstName: MutableState<String> = mutableStateOf("null")
    var lastName: MutableState<String> = mutableStateOf("null")


}


@Composable
fun AnnouncementsScreen(navController: NavController, context: Context) {

    var isLoading by rememberSaveable { mutableStateOf(true) }
    val announcements = remember { mutableStateListOf<Announcement>() }
    val startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var timeSpent by remember { mutableLongStateOf(0L) }
    val screenID = "SC1"

    LaunchedEffect(Unit) {
        while (true) {
            timeSpent = System.currentTimeMillis() - startTime
            delay(1000) // Update every second (adjust as needed)
        }
    }


    DisposableEffect(Unit) {
        GlobalColors.loadColorScheme(context)
        onDispose {
            // Fetch the screen details
            MyDatabase.getScreenDetails(screenID) { screenDetails ->
                if (screenDetails != null) {
                    MyDatabase.writeScren(courseScreen = screenDetails) {}
                    // Fetch existing screen time
                    MyDatabase.getScreenTime(screenID) { existingScreenTime ->
                        val totalScreenTime = if (existingScreenTime != null) {
                            Log.d("Screen Time", "Retrieved Screen time: $existingScreenTime")
                            existingScreenTime.time + timeSpent
                        } else {
                            timeSpent
                        }

                        // Create a new ScreenTime object
                        val screentime = ScreenTime(
                            id = screenID,
                            screenName = screenDetails.screenName,
                            time = totalScreenTime
                        )

                        // Save the updated screen time
                        MyDatabase.saveScreenTime(screenTime = screentime, onSuccess = {
                            Log.d("Screen Time", "Saved $totalScreenTime to the database")
                        }, onFailure = {
                            Log.d("Screen Time", "Failed to save $totalScreenTime to the database")
                        })
                    }

                } else {
                    Log.d("Screen Time", "Screen details not found for ID: $screenID")
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) { // Trigger the effect only once
        while (true) { // Continuous loop
            getAnnouncements { fetchedAnnouncements ->
                announcements.clear() // Clear previous announcements
                announcements.addAll(fetchedAnnouncements ?: emptyList())
                isLoading = false
            }
            delay(10)
        }
    }

        Column(
            modifier = Modifier
                .background(CC.primary())
                .fillMaxSize()
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(CC.primary(), RoundedCornerShape(10.dp))
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ColorProgressIndicator(modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(10.dp)))
                }
            } else if (announcements.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("😒", fontSize = 50.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No Announcements found.",
                        style = CC.descriptionTextStyle(context),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(announcements,
                        key = { announcement -> announcement.id }) { announcement ->
                        AnnouncementCard(
                            announcement = announcement, context = context
                        )
                    }
                }
            }
        }
    }



@Composable
fun AnnouncementCard(
    announcement: Announcement, context: Context

) {

    var expanded by remember { mutableStateOf(false) }
    val text = if (expanded) "Close" else "Open"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CC.secondary(), shape = RoundedCornerShape(8.dp))
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
                text = announcement.title,
                style = CC.descriptionTextStyle(context),
                fontWeight = FontWeight.Bold,
                color = CC.textColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { expanded = !expanded },
                colors = ButtonDefaults.buttonColors(containerColor = CC.primary())
            ) {
                Text(text, style = CC.descriptionTextStyle(context))
            }
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = announcement.description,
                style = CC.descriptionTextStyle(context).copy(fontSize = 14.sp),
                color = CC.textColor().copy(alpha = 0.8f),
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
                    text = announcement.author,
                    style = CC.descriptionTextStyle(context).copy(fontSize = 12.sp),
                    color = CC.textColor().copy(alpha = 0.6f),
                )
                Text(
                    text = announcement.date,
                    style = CC.descriptionTextStyle(context).copy(fontSize = 12.sp),
                    color = CC.textColor().copy(alpha = 0.6f),
                )
            }
        }
    }
}

@Preview
@Composable
fun AlertsPreview() {
    AnnouncementsScreen(
        navController = rememberNavController(), context = LocalContext.current,
    )
}