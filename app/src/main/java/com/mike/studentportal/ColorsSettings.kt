package com.mike.studentportal

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.mike.studentportal.ui.theme.Amatic
import com.mike.studentportal.ui.theme.Crimson
import com.mike.studentportal.ui.theme.Lora
import com.mike.studentportal.ui.theme.Segoe
import com.mike.studentportal.ui.theme.Zeyada
import com.mike.studentportal.ui.theme.Caveat
import com.mike.studentportal.CommonComponents as CC



data class ColorScheme(
    val primaryColor: String,
    val secondaryColor: String,
    val tertiaryColor: String,
    val textColor: String,
    val extraColor1: String,
    val extraColor2: String
)

fun parseColor(hex: String): Color {
    return try {
        // Ensure the hex string starts with '#'
        Color(android.graphics.Color.parseColor(if (hex.startsWith("#")) hex else "#$hex"))
    } catch (e: IllegalArgumentException) {
        Color.Unspecified
    }
}

object GlobalColors {
    private const val PREFS_NAME = "color_scheme_prefs"
    private const val COLOR_SCHEME_KEY = "color_scheme"

    private val defaultScheme = ColorScheme("000000", "333333", "CCCCCC", "FFFFFF", "7BC9FF","A3FFD6")

    var currentScheme by mutableStateOf(defaultScheme)

    fun loadColorScheme(context: Context): ColorScheme {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(COLOR_SCHEME_KEY, null)
        val scheme = if (json != null) {
            Gson().fromJson(json, ColorScheme::class.java)
        } else {
            defaultScheme
        }
        currentScheme = scheme
        return scheme
    }

    fun saveColorScheme(context: Context, scheme: ColorScheme) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Clear the old data
        val json = Gson().toJson(scheme)
        editor.putString(COLOR_SCHEME_KEY, json)
        editor.apply()
        currentScheme = scheme
    }

    fun resetToDefaultColors(context: Context) {
        saveColorScheme(context, defaultScheme)
    }

    val primaryColor: Color
        get() = parseColor(currentScheme.primaryColor)

    val secondaryColor: Color
        get() = parseColor(currentScheme.secondaryColor)

    val tertiaryColor: Color
        get() = parseColor(currentScheme.tertiaryColor)

    val textColor: Color
        get() = parseColor(currentScheme.textColor)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Composable
fun ColorSettings(navController: NavController, context: Context) {
    var primaryColor by remember { mutableStateOf(GlobalColors.currentScheme.primaryColor) }
    var secondaryColor by remember { mutableStateOf(GlobalColors.currentScheme.secondaryColor) }
    var tertiaryColor by remember { mutableStateOf(GlobalColors.currentScheme.tertiaryColor) }
    var extraColor1 by remember { mutableStateOf(GlobalColors.currentScheme.extraColor1) }
    var extraColor2 by remember { mutableStateOf(GlobalColors.currentScheme.extraColor2) }
    var textColor by remember { mutableStateOf(GlobalColors.currentScheme.textColor) }
    var currentFont by remember { mutableStateOf<FontFamily?>(null) }
    var fontUpdated by remember { mutableStateOf(false) }

    // Load color scheme from SharedPreferences
    LaunchedEffect(Unit) {
        val scheme = GlobalColors.loadColorScheme(context)
        primaryColor = scheme.primaryColor
        secondaryColor = scheme.secondaryColor
        tertiaryColor = scheme.tertiaryColor
        textColor = scheme.textColor
        extraColor1 = scheme.extraColor1
        extraColor2 = scheme.extraColor2
    }

    // Listen to changes in global color scheme and update local states
    LaunchedEffect(GlobalColors.currentScheme) {
        primaryColor = GlobalColors.currentScheme.primaryColor
        secondaryColor = GlobalColors.currentScheme.secondaryColor
        tertiaryColor = GlobalColors.currentScheme.tertiaryColor
        textColor = GlobalColors.currentScheme.textColor
        extraColor1 = GlobalColors.currentScheme.extraColor1
        extraColor2 = GlobalColors.currentScheme.extraColor2

    }

    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        visible = true
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Colors and Font", style = CC.titleTextStyle(context)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("dashboard") }) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            GlobalColors.resetToDefaultColors(context)
                        }
                    ) {
                        Icon(Icons.Filled.Replay, "Revert", tint = GlobalColors.tertiaryColor)
                    }
                    IconButton(
                        onClick = {
                            val newScheme = ColorScheme(
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor,
                                tertiaryColor = tertiaryColor,
                                textColor = textColor,
                                extraColor1 = extraColor1,
                                extraColor2 = extraColor2
                            )
                            GlobalColors.saveColorScheme(context, newScheme)
                        }
                    ) {
                        Icon(Icons.Filled.Check, "Save", tint = GlobalColors.tertiaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlobalColors.primaryColor,
                    titleContentColor = GlobalColors.textColor,
                    navigationIconContentColor = GlobalColors.textColor,
                )
            )
        },
        containerColor = GlobalColors.primaryColor,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                Text("Color Palette", style = CC.titleTextStyle(context))
            }
            Spacer(modifier = Modifier.height(16.dp))
            ColorPalette { colors ->
                primaryColor = colors[0].toArgb().toHexString()
                secondaryColor = colors[1].toArgb().toHexString()
                tertiaryColor = colors[2].toArgb().toHexString()
                textColor = colors[3].toArgb().toHexString()
                extraColor1 = colors[4].toArgb().toHexString()
                extraColor2 = colors[5].toArgb().toHexString()
            }
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextStyle(context = LocalContext.current) { selectedFont ->
                currentFont = selectedFont
                fontUpdated = !fontUpdated // Toggle the state to trigger recomposition
            }
        }
    }

}

data class Theme(val name: String, val colors: List<Color>)

@Composable
fun ColorPalette(onColorSelected: (List<Color>) -> Unit) {
    val midnightTheme = Theme("Midnight", listOf(
        Color(0xFF000000), // Black
        Color(0xFF2C3E50), // Dark Blue
        Color(0xFF34495E), // Grayish Blue
        Color(0xFF1C2833) , // Very Dark Blue
                Color(0xFF000000), // Black
        Color(0xFF2C3E50),

    ))

    val galaxyTheme = Theme("Galaxy", listOf(
        Color(0xFF2C3E50), // Dark Blue
        Color(0xFF4A235A), // Dark Purple
        Color(0xFF1B2631), // Very Dark Blue
        Color(0xFF212F3D)  // Very Dark Grayish
    ))

    val forestNightTheme = Theme("Forest Night", listOf(
        Color(0xFF0B3D91), // Very Dark Blue
        Color(0xFF145A32), // Very Dark Green
        Color(0xFF0E6655), // Dark Cyan
        Color(0xFF186A3B)  // Dark Green
    ))

    val deepSeaTheme = Theme("Deep Sea", listOf(
        Color(0xFF154360), // Dark Blue
        Color(0xFF1A5276), // Dark Blue
        Color(0xFF1F618D), // Medium Blue
        Color(0xFF2874A6)  // Blue
    ))

    val nightSkyTheme = Theme("Night Sky", listOf(
        Color(0xFF1C2833), // Very Dark Blue
        Color(0xFF2C3E50), // Dark Blue
        Color(0xFF34495E), // Grayish Blue
        Color(0xFF283747)  // Dark Grayish Blue
    ))

    val darkChocolateTheme = Theme("Dark Chocolate", listOf(
        Color(0xFF3E2723), // Very Dark Brown
        Color(0xFF4E342E), // Dark Brown
        Color(0xFF5D4037), // Medium Brown
        Color(0xFF6D4C41)  // Brown
    ))

    val charcoalTheme = Theme("Charcoal", listOf(
        Color(0xFF212121), // Very Dark Gray
        Color(0xFF424242), // Dark Gray
        Color(0xFF616161), // Medium Gray
        Color(0xFF757575)  // Gray
    ))

    val shadowTheme = Theme("Shadow", listOf(
        Color(0xFF2E2E2E), // Dark Gray
        Color(0xFF424242), // Dark Gray
        Color(0xFF616161), // Medium Gray
        Color(0xFF757575)  // Gray
    ))


    val monochromeTheme = Theme("Monochrome", listOf(
        Color(0xFF000000), // Black
        Color(0xFF333333), // Dark Grey
        Color(0xFFCCCCCC), // Light Grey
        Color(0xFFFFFFFF)  // White
    ))

    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = GlobalColors.textColor,
                shape = RoundedCornerShape(10.dp)
            )
            .background(Color.Transparent, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .horizontalScroll(rememberScrollState())
            .height(200.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        ThemeColorRow(midnightTheme.colors, onColorSelected)
        ThemeColorRow(galaxyTheme.colors, onColorSelected)
        ThemeColorRow(forestNightTheme.colors, onColorSelected)
        ThemeColorRow(deepSeaTheme.colors, onColorSelected)
        ThemeColorRow(nightSkyTheme.colors, onColorSelected)
        ThemeColorRow(darkChocolateTheme.colors, onColorSelected)
        ThemeColorRow(charcoalTheme.colors, onColorSelected)
        ThemeColorRow(shadowTheme.colors, onColorSelected)
        ThemeColorRow(monochromeTheme.colors, onColorSelected)
        Customize { colors -> onColorSelected(colors) }

    }
}

@Composable
fun ThemeColorRow(colors: List<Color>, onColorSelected: (List<Color>) -> Unit) {
    Row(
        modifier = Modifier
            .padding(5.dp)
            .border(
                width = 1.dp,
                color = GlobalColors.textColor,
                shape = RoundedCornerShape(10.dp)
            )
            .fillMaxHeight()
            .width(350.dp)
            .background(Color.Transparent, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable { onColorSelected(colors) }
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}

@Composable
fun Customize(onColorSelected: (List<Color>) -> Unit) {
    var color1 by remember { mutableStateOf<Color?>(null) }
    var color2 by remember { mutableStateOf<Color?>(null) }
    var color3 by remember { mutableStateOf<Color?>(null) }
    var color4 by remember { mutableStateOf<Color?>(null) }

    var color1Error by remember { mutableStateOf(false) }
    var color2Error by remember { mutableStateOf(false) }
    var color3Error by remember { mutableStateOf(false) }
    var color4Error by remember { mutableStateOf(false) }

    val customizedColors = listOf(color1, color2, color3, color4)

    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = GlobalColors.textColor,
                shape = RoundedCornerShape(10.dp)
            )
            .fillMaxHeight()
            .padding(5.dp)
            .width(350.dp)
            .background(Color.Transparent, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable { onColorSelected(customizedColors.filterNotNull()) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text("Customize yours", style = CC.descriptionTextStyle(LocalContext.current))

        for (i in 1..4) {
            var text by remember { mutableStateOf("") }
            val isError = when (i) {
                1 -> color1Error
                2 -> color2Error
                3 -> color3Error
                else -> color4Error
            }
            BasicTextField(
                value = text,
                onValueChange = {
                    text = it
                    try {
                        val color = Color(android.graphics.Color.parseColor(it))
                        when (i) {
                            1 -> {
                                color1 = color
                                color1Error = false
                            }
                            2 -> {
                                color2 = color
                                color2Error = false
                            }
                            3 -> {
                                color3 = color
                                color3Error = false
                            }
                            4 -> {
                                color4 = color
                                color4Error = false
                            }
                        }
                    } catch (e: Exception) {
                        when (i) {
                            1 -> color1Error = true
                            2 -> color2Error = true
                            3 -> color3Error = true
                            4 -> color4Error = true
                        }
                    }
                },
                modifier = Modifier
                    .padding(5.dp)
                    .border(
                        width = 1.dp,
                        color = if (isError) Color.Red else GlobalColors.textColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .fillMaxWidth(0.9f),
                textStyle = TextStyle(
                    color = if (isError) Color.Red else GlobalColors.textColor,
                    fontSize = 16.sp,
                    background = GlobalColors.primaryColor
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = "Color $i",
                                style = CC.descriptionTextStyle(context = LocalContext.current)
                            )
                        }
                        innerTextField()
                    }
                },
                singleLine = true
            )
        }
    }
}











@Composable
fun currentFontFamily(context: Context): FontFamily {
    val fontPrefs = remember { FontPreferences(context) }
    val selectedFontName = fontPrefs.getSelectedFont()

    return when (selectedFontName) {
        "Segoe" -> Segoe
        "Lora" -> Lora
        "Amatic" -> Amatic
        "Crimson" -> Crimson
        "Zeyada" -> Zeyada
        "Caveat" -> Caveat
        else -> FontFamily.Default // Use system font if no preference is saved
    }
}




class FontPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("font_prefs", Context.MODE_PRIVATE)

    fun saveSelectedFont(fontName: String?) {
        prefs.edit().putString("selected_font", fontName).apply()
    }

    fun getSelectedFont(): String? {
        return prefs.getString("selected_font", null) // Default to null (system font)
    }
    fun resetToSystemFont() {
        prefs.edit().remove("selected_font").apply()
    }
}

@Composable
fun CustomTextStyle(context: Context, onFontSelected: (FontFamily) -> Unit) {
    val fontPrefs = remember { FontPreferences(context) }
    var fontUpdated by remember { mutableStateOf(false) }
    var selectedFontFamily by remember { mutableStateOf<FontFamily?>(null) }
    val fontFamilies = mapOf(
        "Segoe" to Segoe,
        "Amatic" to Amatic,
        "Lora" to Lora,
        "Crimson" to Crimson,
        "Zeyada" to Zeyada,
        "Caveat" to Caveat,
        "System" to FontFamily.Default
    )
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text("Font Styles", style = CC.titleTextStyle(context)) // Assuming CC.descriptionTextStyle(context) is defined elsewhere
    }
    Column(
        modifier = Modifier
            .background(GlobalColors.primaryColor)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = GlobalColors.textColor,
                shape = RoundedCornerShape(10.dp)
            )
    ) {


        fontFamilies.forEach { (fontName, fontFamily) ->
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .border(
                        width = 1.dp,
                        color = GlobalColors.secondaryColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable { selectedFontFamily = fontFamily },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$fontName - Michael Odhiambo",
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    color = GlobalColors.textColor,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Selected Font Preview:",
                style = CC.titleTextStyle(context),
                fontSize = 18.sp
            )
        }

        Row(
            modifier = Modifier
                .padding(10.dp)
                .border(
                    width = 1.dp,
                    color = GlobalColors.secondaryColor,
                    shape = RoundedCornerShape(10.dp)
                )
                .fillMaxWidth()
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "This is a preview of the selected font.",
                fontFamily = selectedFontFamily,
                fontSize = 16.sp,
                color = GlobalColors.textColor,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                fontPrefs.saveSelectedFont(fontFamilies.entries.find { it.value == selectedFontFamily }?.key)
                selectedFontFamily?.let { onFontSelected(it) }
                fontUpdated = !fontUpdated // Trigger recomposition in parent
                Toast.makeText(context, "Font updated", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(GlobalColors.secondaryColor),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        ) {
            Text("Save", style = CC.descriptionTextStyle(context))
        }
    }
}


@Preview
@Composable
fun ColorSettingsPreview() {
    val context = LocalContext.current
//    Customize(
//        onColorSelected = { colors ->
//            // Handle the selected colors here
//        }
//    )
    // Load the color scheme when the composable is launched
    LaunchedEffect(Unit) {
        GlobalColors.currentScheme = GlobalColors.loadColorScheme(context)
    }
    ColorSettings(rememberNavController(), LocalContext.current)
}



