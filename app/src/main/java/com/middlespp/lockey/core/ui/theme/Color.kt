package com.middlespp.lockey.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LockeyBlue = Color(0xFF0B57D0)
val LockeyBlueDark = Color(0xFFAECBFA)
val LockeyInk = Color(0xFF111318)
val LockeySurface = Color(0xFFFBF8FF)
val LockeySurfaceDark = Color(0xFF111318)

val LightColorScheme = lightColorScheme(
    primary = LockeyBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E2FF),
    onPrimaryContainer = Color(0xFF001A41),
    secondary = Color(0xFF5A5F71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDFE2F9),
    onSecondaryContainer = Color(0xFF171B2C),
    tertiary = Color(0xFF745470),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD7F6),
    onTertiaryContainer = Color(0xFF2B122A),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    background = LockeySurface,
    onBackground = LockeyInk,
    surface = LockeySurface,
    onSurface = LockeyInk,
    surfaceVariant = Color(0xFFE2E2EC),
    onSurfaceVariant = Color(0xFF45464F),
    outline = Color(0xFF767680)
)

val DarkColorScheme = darkColorScheme(
    primary = LockeyBlueDark,
    onPrimary = Color(0xFF002E69),
    primaryContainer = Color(0xFF004493),
    onPrimaryContainer = Color(0xFFD8E2FF),
    secondary = Color(0xFFC3C6DC),
    onSecondary = Color(0xFF2C3042),
    secondaryContainer = Color(0xFF424659),
    onSecondaryContainer = Color(0xFFDFE2F9),
    tertiary = Color(0xFFE2BBDD),
    onTertiary = Color(0xFF422741),
    tertiaryContainer = Color(0xFF5A3D58),
    onTertiaryContainer = Color(0xFFFFD7F6),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    background = LockeySurfaceDark,
    onBackground = Color(0xFFE4E2E9),
    surface = LockeySurfaceDark,
    onSurface = Color(0xFFE4E2E9),
    surfaceVariant = Color(0xFF45464F),
    onSurfaceVariant = Color(0xFFC6C6D0),
    outline = Color(0xFF90909A)
)
