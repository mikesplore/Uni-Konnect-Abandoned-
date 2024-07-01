package com.mike.studentportal


import android.content.Context
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import com.mike.studentportal.MyDatabase.getAnnouncements
import kotlinx.coroutines.delay
import com.mike.studentportal.CommonComponents as CC

object Details {
    var email: MutableState<String> = mutableStateOf("")
    var firstName: MutableState<String> = mutableStateOf("null")
    var lastName: MutableState<String> = mutableStateOf("null")
    var phoneNumber: MutableState<String> = mutableStateOf("null")


}


@Composable
fun AnnouncementsScreen(navController: NavController, context: Context) {

    var isLoading by rememberSaveable { mutableStateOf(true) }
    val announcements = remember { mutableStateListOf<Announcement>() }

    LaunchedEffect(key1 = Unit) { // Trigger the effect only once
        while (true) { // Continuous loop
            getAnnouncements { fetchedAnnouncements ->
                announcements.clear() // Clear previous announcements
                announcements.addAll(fetchedAnnouncements ?: emptyList())
                isLoading = false
            }
            delay(10) // Wait for 5 seconds
        }
    }

        Column(
            modifier = Modifier
                .background(GlobalColors.primaryColor)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(GlobalColors.primaryColor, RoundedCornerShape(10.dp))
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
                text = announcement.title,
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
                text = announcement.description,
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
                    text = announcement.author,
                    style = CC.descriptionTextStyle(context).copy(fontSize = 12.sp),
                    color = GlobalColors.textColor.copy(alpha = 0.6f),
                )
                Text(
                    text = announcement.date,
                    style = CC.descriptionTextStyle(context).copy(fontSize = 12.sp),
                    color = GlobalColors.textColor.copy(alpha = 0.6f),
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