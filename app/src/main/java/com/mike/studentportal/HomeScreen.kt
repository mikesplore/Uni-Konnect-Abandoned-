package com.mike.studentportal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mike.studentportal.CommonComponents as CC


data class IconItem(val icon: ImageVector, val contentDescription: String)
@Composable
fun HomeScreen() {
    val iconList = listOf(
        IconItem(Icons.Filled.Home, "Home"),
        IconItem(Icons.Filled.Search, "Search"),
        IconItem(Icons.Filled.Settings, "Settings"),
        IconItem(Icons.Filled.Home, "Home"),
        IconItem(Icons.Filled.Search, "Search"),
        IconItem(Icons.Filled.Settings, "Settings"),
    )
    Column(modifier = Modifier
        .background(CC.primary)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Row(modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(10.dp)
            .width(350.dp)) {
            SearchTextField()
        }
        Spacer(modifier = Modifier.height(10.dp))
        IconList(iconList, 60.dp)

        Row(modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Recent Units", style = CC.titleTextStyle, fontSize = 20.sp)
            Text("View All", style = CC.descriptionTextStyle, fontSize = 15.sp)
        }


    }
}

@Composable
fun IconBox(item: IconItem, boxSize: Dp) {
    Box(
        modifier = Modifier
            .padding(start = 10.dp)
            .size(boxSize)
            .border(1.dp, CC.tertiary, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            tint = CC.secondary  ,
            contentDescription = item.contentDescription,
            modifier = Modifier.size(boxSize / 2) // Adjust icon size as needed
        )
    }
}

@Composable
fun IconList(iconList: List<IconItem>, boxSize: Dp) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp) // Add padding around the row
    ) {
        items(iconList) { item ->
            IconBox(item, boxSize)
        }
    }
}

@Composable
fun SearchTextField() {
    var searchText by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText,
        onValueChange = { searchText = it },
        label = { Text("Search") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon"
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
fun UnitsBoxes(){

}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}