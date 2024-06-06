package com.mike.studentportal

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import java.io.File
import com.mike.studentportal.CommonComponents as CC

data class ColorScheme(
    val primaryColor: String,
    val secondaryColor: String,
    val tertiaryColor: String,
    val textColor: String
)

fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor("#$hex"))
    } catch (e: IllegalArgumentException) {
        Color.Unspecified
    }
}

object GlobalColors {
    private const val COLORS_FILE_NAME = "color_scheme.json"

    private val defaultScheme = ColorScheme("164863", "427D9D", "9BBEC8", "DDF2FD")

    var currentScheme by mutableStateOf(defaultScheme)

    fun loadColorScheme(context: Context): ColorScheme {
        val file = File(context.filesDir, COLORS_FILE_NAME)
        return if (file.exists()) {
            val json = file.readText()
            Gson().fromJson(json, ColorScheme::class.java)
        } else {
            defaultScheme
        }
    }

    fun saveColorScheme(context: Context, scheme: ColorScheme) {
        val file = File(context.filesDir, COLORS_FILE_NAME)
        if (file.exists()) {
            file.delete()  // Delete the old color scheme file
        }
        val json = Gson().toJson(scheme)
        file.writeText(json)
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

@Composable
fun ColorSettings(context: Context, onsave: () -> Unit, onrevert: () -> Unit) {
    var primaryColor by remember { mutableStateOf(GlobalColors.currentScheme.primaryColor) }
    var secondaryColor by remember { mutableStateOf(GlobalColors.currentScheme.secondaryColor) }
    var tertiaryColor by remember { mutableStateOf(GlobalColors.currentScheme.tertiaryColor) }
    var textColor by remember { mutableStateOf(GlobalColors.currentScheme.textColor) }

    // Listen to changes in global color scheme and update local states
    LaunchedEffect(GlobalColors.currentScheme) {
        primaryColor = GlobalColors.currentScheme.primaryColor
        secondaryColor = GlobalColors.currentScheme.secondaryColor
        tertiaryColor = GlobalColors.currentScheme.tertiaryColor
        textColor = GlobalColors.currentScheme.textColor
    }

    var refreshTrigger by remember { mutableStateOf(false) } // Trigger to force recomposition

    Column(
        modifier = Modifier.height(500.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedColorTextField(
            label = "Primary color",
            colorValue = primaryColor,
            onValueChange = { newValue ->
                primaryColor = newValue
            })

        OutlinedColorTextField(
            label = "Secondary color",
            colorValue = secondaryColor,
            onValueChange = { newValue ->
                secondaryColor = newValue
            })

        OutlinedColorTextField(
            label = "Tertiary color",
            colorValue = tertiaryColor,
            onValueChange = { newValue ->
                tertiaryColor = newValue
            })

        OutlinedColorTextField(
            label = "Text color",
            colorValue = textColor,
            onValueChange = { newValue ->
                textColor = newValue
            })

        Button(
            onClick = {
                val newScheme = ColorScheme(
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    tertiaryColor = tertiaryColor,
                    textColor = textColor
                )
                GlobalColors.saveColorScheme(context, newScheme)
                refreshTrigger = !refreshTrigger // Toggle the trigger to force recomposition
                onsave()
            },
            colors = ButtonDefaults.buttonColors(GlobalColors.primaryColor),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Save Colors", style = CC.descriptionTextStyle)
        }

        Button(
            onClick = {
                GlobalColors.resetToDefaultColors(context)
                refreshTrigger = !refreshTrigger // Toggle the trigger to force recomposition
                onrevert()
            },
            colors = ButtonDefaults.buttonColors(GlobalColors.primaryColor),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Revert to Default Colors", style = CC.descriptionTextStyle)
        }
    }
}

@Composable
fun OutlinedColorTextField(
    label: String,
    colorValue: String,
    onValueChange: (String) -> Unit,

    ) {
    var isValidColor by remember { mutableStateOf(true) } // State to track validity

    CC.SingleLinedTextField(
        label = label, value = colorValue, onValueChange = { newValue ->
            isValidColor = isValidHexColor(newValue)
            onValueChange(newValue)
        }, singleLine = true
    )
}

// Helper function to check if a string is a valid hex color code
fun isValidHexColor(colorString: String): Boolean {
    return try {
        Color(android.graphics.Color.parseColor("#$colorString")) // Try parsing with #
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}

@Preview
@Composable
fun ColorSettingsPreview() {
    val context = LocalContext.current

    // Load the color scheme when the composable is launched
    LaunchedEffect(Unit) {
        GlobalColors.currentScheme = GlobalColors.loadColorScheme(context)
    }
    ColorSettings(context = context, {}, {})
}
