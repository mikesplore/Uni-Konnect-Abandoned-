package com.mike.studentportal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.mike.studentportal.MyDatabase.readItems
import com.mike.studentportal.CommonComponents as CC

object CourseName {
    var name: MutableState<String> = mutableStateOf("")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(courseCode: String, context: Context) {
    val notes = remember { mutableStateListOf<GridItem>() }
    val pastPapers = remember { mutableStateListOf<GridItem>() }
    val resources = remember { mutableStateListOf<GridItem>() }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var addItemToSection by remember { mutableStateOf<Section?>(null) }

    LaunchedEffect(courseCode) {
        isLoading = true
        readItems(courseCode, Section.NOTES) { fetchedNotes ->
            notes.addAll(fetchedNotes)
            isLoading = false
        }
        readItems(courseCode, Section.PAST_PAPERS) { fetchedPastPapers ->
            pastPapers.addAll(fetchedPastPapers)
            isLoading = false
        }
        readItems(courseCode, Section.RESOURCES) { fetchedResources ->
            resources.addAll(fetchedResources)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        CourseName.name.value,
                        style = CC.titleTextStyle(context),
                        fontSize = 20.sp
                    )
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor
                )
            )
        }, containerColor = GlobalColors.primaryColor
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GlobalColors.primaryColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GlobalColors.textColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .background(GlobalColors.primaryColor)
                    .padding(it)
            ) {
                Section(
                    title = "Notes", items = notes, context = context
                )
                Section(
                    title = "Past Papers", items = pastPapers, context = context
                )
                Section(
                    title = "Additional Resources", items = resources, context = context
                )
            }
        }
    }

}

@Composable
fun Section(
    title: String, items: List<GridItem>, context: Context
) {
    Text(
        text = title, style = CC.titleTextStyle(context), modifier = Modifier.padding(start = 15.dp)
    )

    Spacer(modifier = Modifier.height(10.dp))

    if (items.isEmpty()) {
        Text(
            text = "No items available",
            style = CC.descriptionTextStyle(context),
            modifier = Modifier.padding(start = 15.dp)
        )
    } else {
        LazyRow {
            items(items) { item ->
                GridItemCard(item = item, context = context)
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun GridItemCard(item: GridItem, context: Context) {
    val uriHandler = LocalUriHandler.current
    val thumbnail = when (item.fileType) {
        "pdf" -> R.drawable.pdf
        "word" -> R.drawable.word
        "excel" -> R.drawable.excel
        else -> item.thumbnail // Assuming thumbnail is a URL for image file types
    }

    Surface(
        modifier = Modifier
            .width(200.dp)
            .padding(start = 15.dp),
        shape = RoundedCornerShape(8.dp),
        color = GlobalColors.secondaryColor,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = thumbnail),
                contentDescription = item.title,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray, RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                style = CC.titleTextStyle(context),
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.description,
                style = CC.descriptionTextStyle(context),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { uriHandler.openUri(item.link) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GlobalColors.secondaryColor),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Open", style = CC.descriptionTextStyle(context))
            }
        }
    }
}
@Preview
@Composable
fun CoursePreview(){
    CoursesScreen(rememberNavController(), LocalContext.current)
}