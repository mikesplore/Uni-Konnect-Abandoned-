package com.mike.studentportal

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.mike.studentportal.ui.theme.*

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
        Color(android.graphics.Color.parseColor(if (hex.startsWith("#")) hex else "#$hex"))
    } catch (e: IllegalArgumentException) {
        Color.Unspecified
    }
}

object GlobalColors {
    private const val PREFS_NAME = "color_scheme_prefs"
    private const val COLOR_SCHEME_KEY = "color_scheme"
    private const val THEME_MODE_KEY = "theme_mode"

    private val lightScheme = ColorScheme(
        primaryColor = "#FFFFFF",
        secondaryColor = "#EEEEEE",
        tertiaryColor = "#DDDDDD",
        textColor = "#000000",
        extraColor1 = "#007AFF",
        extraColor2 = "#34C759"
    )

    private val darkScheme = ColorScheme(
        primaryColor = "#000000",
        secondaryColor = "#333333",
        tertiaryColor = "#666666",
        textColor = "#FFFFFF",
        extraColor1 = "#007AFF",
        extraColor2 = "#34C759"
    )

    private var currentScheme by mutableStateOf(lightScheme)
    var isDarkMode by mutableStateOf(false)

    fun loadColorScheme(context: Context): ColorScheme {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(COLOR_SCHEME_KEY, null)
        val isDark = sharedPreferences.getBoolean(THEME_MODE_KEY, false)
        isDarkMode = isDark
        currentScheme = if (isDark) darkScheme else lightScheme
        return currentScheme
    }

    fun saveColorScheme(context: Context, isDark: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(THEME_MODE_KEY, isDark)
        editor.apply()
        isDarkMode = isDark
        currentScheme = if (isDark) darkScheme else lightScheme
    }

    fun resetToDefaultColors(context: Context) {
        saveColorScheme(context, false)
    }

    val primaryColor: Color
        get() = parseColor(currentScheme.primaryColor)

    val secondaryColor: Color
        get() = parseColor(currentScheme.secondaryColor)

    val tertiaryColor: Color
        get() = parseColor(currentScheme.tertiaryColor)

    val textColor: Color
        get() = parseColor(currentScheme.textColor)

    val extraColor1: Color
        get() = parseColor(currentScheme.extraColor1)

    val extraColor2: Color
        get() = parseColor(currentScheme.extraColor2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSettings(navController: NavController, context: Context) {
    var isDarkMode by remember { mutableStateOf(GlobalColors.isDarkMode) }
    var currentFont by remember { mutableStateOf<FontFamily?>(null) }
    var fontUpdated by remember { mutableStateOf(false) }

    // Load color scheme from SharedPreferences
    LaunchedEffect(Unit) {
        GlobalColors.loadColorScheme(context)
        isDarkMode = GlobalColors.isDarkMode
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
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text("Theme Settings", style = CC.titleTextStyle(context))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Use Dark Theme", style = CC.descriptionTextStyle(context))
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = {
                        isDarkMode = it
                        GlobalColors.saveColorScheme(context, it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GlobalColors.extraColor1,
                        uncheckedThumbColor = GlobalColors.extraColor2
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Use System Settings", style = CC.descriptionTextStyle(context))
                Switch(
                    checked = !isDarkMode,
                    onCheckedChange = {
                        isDarkMode = !it
                        GlobalColors.saveColorScheme(context, !it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GlobalColors.extraColor1,
                        uncheckedThumbColor = GlobalColors.extraColor2
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextStyle(context = LocalContext.current) { selectedFont ->
                currentFont = selectedFont
                fontUpdated = !fontUpdated // Toggle the state to trigger recomposition
            }
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

@Preview
@Composable
fun ColorSettingsPreview() {
    val context = LocalContext.current
    GlobalColors.loadColorScheme(context) // Ensure the color scheme is loaded for the preview
    ColorSettings(rememberNavController(), context)
}
