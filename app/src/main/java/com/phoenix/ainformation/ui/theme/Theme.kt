package com.phoenix.ainformation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkOutlineColor,
    background = DarkBackground,
    surface = DarkSearchBarBackground,
    surfaceVariant = DarkDialogContainerColor,
    onBackground = DarkTextColor,
    onSurface = DarkTextColor,
    secondary = DarkButtonColor,
    onSecondary = DarkButtonTextColor,
    onSurfaceVariant = DarkTextColor,
    outline = DarkOutlineColor
)

private val LightColorScheme = lightColorScheme(
    primary = LightOutlineColor,
    background = LightBackground,
    surface = LightSearchBarBackground,
    surfaceVariant = LightDialogContainerColor,
    onBackground = LightTextColor,
    onSurface = LightTextColor,
    secondary = LightButtonColor,
    onSecondary = LightButtonTextColor,
    onSurfaceVariant = LightTextColor,
    outline = LightOutlineColor
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    if (darkTheme) {
        SearchBarColors(
            containerColor = Color(0xFF242B31),
            dividerColor = Color(0xFF20D16E)
        )
    } else {
        SearchBarColors(
            containerColor = Color.Gray,
            dividerColor = Color.Gray
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}