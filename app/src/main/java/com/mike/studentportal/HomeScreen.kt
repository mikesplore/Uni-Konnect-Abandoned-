package com.mike.studentportal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.mike.studentportal.CommonComponents as CC


data class IconItem(val icon: ImageVector, val contentDescription: String)

@Composable
fun HomeScreen(context: Context, navController: NavController) {
    val iconList = listOf(
        IconItem(Icons.Filled.Home, "Dashboard"),
        IconItem(Icons.Filled.Search, ""),
        IconItem(Icons.Filled.Settings, "Settings"),
        IconItem(Icons.Filled.Home, "Home"),
        IconItem(Icons.Filled.Search, "Search"),
        IconItem(Icons.Filled.Settings, "Settings"),
    )

    val imageUrls = listOf(
        Pair(
            "https://burycollegewebstore.blob.core.windows.net/uploads/4877dfea-c2a7-4a8c-bb1f-4075789d27c6/pexels-jeshootscom-530024_1296x800.jpg",
            "Text for Image 1"
        ),
        Pair(
            "https://burycollegewebstore.blob.core.windows.net/uploads/4877dfea-c2a7-4a8c-bb1f-4075789d27c6/pexels-jeshootscom-530024_1296x800.jpg",
            "Text for Image 2"
        ),
        // Add more image URLs and their corresponding text
    )
    Column(
        modifier = Modifier
            .background(CC.primary)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(10.dp)
                .width(350.dp)
        ) {
            SearchTextField()
        }
        Spacer(modifier = Modifier.height(10.dp))
        IconList(iconList, navController = navController)

        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recently accessed courses", style = CC.titleTextStyle(context), fontSize = 20.sp)
            Text("View All", style = CC.descriptionTextStyle(context), fontSize = 15.sp)
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ImageList(imageUrls, context)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween){
            Text("Upcoming events", style = CC.titleTextStyle(context), fontSize = 20.sp)
            Text("View All", style = CC.descriptionTextStyle(context), fontSize = 15.sp)
        }
        Row(modifier = Modifier
            .border(
                1.dp,
                CC.tertiary,
                shape = RoundedCornerShape(16.dp)
            )
            .height(230.dp)
            .fillMaxWidth(0.9f)){
            EventCard(
                title = "Event 1",
                dateTime = "10:00 AM - 12:00 PM",
                location = "Location 1",
                description = "Description 1",
                onRegisterClick = {},
                context
            )

        }


    }
}

@Composable
fun IconBox(item: IconItem, navController: NavController) { // Add NavController parameter
    Box(
        modifier = Modifier
            .padding(start = 10.dp)
            .size(60.dp)
            .border(1.dp, CC.tertiary, shape = RoundedCornerShape(8.dp))
            .clickable {
                // Navigate to the appropriate route based on the icon clicked
                navController.navigate("routeFor${item.contentDescription}") // Replace with actual route
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            tint = CC.secondary,
            contentDescription = item.contentDescription,
            modifier = Modifier.size(60.dp / 2)
        )
    }
}

@Composable
fun IconList(iconList: List<IconItem>, navController: NavController) { // Add NavController parameter
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(iconList) { item ->
            IconBox(item, navController) // Pass navController to IconBox
        }
    }
}

@Composable
fun EventCard(
    title: String,
    dateTime: String,
    location: String,
    description: String,
    onRegisterClick: () -> Unit,
    context: Context

) {
    var isLoading by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            AsyncImage(
                model = "https://www.adobe.com/content/dam/www/us/en/events/overview-page/eventshub_evergreen_opengraph_1200x630_2x.jpg",
                contentDescription = "Event Image",
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 8.dp), // Apply blur effect
                contentScale = ContentScale.Crop,
                onLoading = { isLoading = true },
                onSuccess = { isLoading = false }
            )
            if (isLoading) {
                CircularProgressIndicator(
                    color = CC.secondary, trackColor = CC.primary
                )
            }
            if(!isLoading){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = CC.titleTextStyle(context),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Event Date and Time",
                        tint = CC.secondary
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = dateTime,
                        style = CC.descriptionTextStyle(context)
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Event Location",
                        tint = CC.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = location,
                        style = CC.descriptionTextStyle(context)
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = description,
                    style = CC.descriptionTextStyle(context),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onRegisterClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff810CA8),
                        contentColor = CC.secondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Register", style = CC.descriptionTextStyle(context))
                }
            }
        }
    }
    }
}


@Composable
fun SearchTextField() {
    var searchText by remember { mutableStateOf("") }

    OutlinedTextField(value = searchText,
        onValueChange = { searchText = it },
        label = { Text("Search") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search, contentDescription = "Search Icon"
            )
        },
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CC.primary,
            unfocusedContainerColor = CC.primary,
            focusedIndicatorColor = CC.tertiary,
            unfocusedIndicatorColor = CC.tertiary,
            focusedTextColor = CC.textColor,
            unfocusedTextColor = CC.textColor,
            cursorColor = CC.textColor,
            focusedLabelColor = CC.textColor,
            unfocusedLabelColor = CC.secondary
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = TextStyle(
            color = CC.textColor,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        singleLine = true
    )
}


@Composable
fun ImageBox(imageUrl: String, boxText: String,context: Context) { // Add boxText parameter
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .padding(1.dp)
            .width(250.dp)
            .height(200.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp))
                    .fillMaxWidth()
                    .weight(1f)
                    .background(CC.secondary),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(model = imageUrl,
                    contentDescription = "Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onLoading = { isLoading = true },
                    onSuccess = { isLoading = false })

                if (isLoading) {
                    CircularProgressIndicator(
                        color = CC.secondary, trackColor = CC.primary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Last accessed: 2 hours ago", style = CC.descriptionTextStyle(context))
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .height(50.dp)
                        .border(1.dp, Color(0xff6E85B2), shape = RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CC.primary, contentColor = CC.secondary
                    )

                ) {
                    Text("Visit", style = CC.descriptionTextStyle(context))
                }

            }
        }
    }
}

@Composable
fun ImageList(imageUrls: List<Pair<String, String>>, context: Context) { // Use Pair for image and text
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(imageUrls) { (imageUrl, boxText) -> // Destructure Pair
            ImageBox(imageUrl, boxText, context)
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(LocalContext.current, navController = rememberNavController())

}