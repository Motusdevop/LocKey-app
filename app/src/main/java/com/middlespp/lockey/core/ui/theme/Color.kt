package com.middlespp.lockey.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LockeyBlue = Color(0xFF2E6BFF)
val LockeyBlueDark = Color(0xFFADC7FF)
val LockeyInk = Color(0xFF111820)
val LockeySlate = Color(0xFF607086)
val LockeySurface = Color(0xFFF7FAFF)
val LockeySurfaceDark = Color(0xFF071012)

val LightColorScheme = lightColorScheme(
    primary = LockeyBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE7FF),
    onPrimaryContainer = Color(0xFF001B47),
    secondary = LockeySlate,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE6F3),
    onSecondaryContainer = Color(0xFF14202D),
    tertiary = Color(0xFF46617E),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD2E5FF),
    onTertiaryContainer = Color(0xFF001D32),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    background = LockeySurface,
    onBackground = LockeyInk,
    surface = LockeySurface,
    onSurface = LockeyInk,
    surfaceVariant = Color(0xFFDDE5F0),
    onSurfaceVariant = Color(0xFF404954),
    outline = Color(0xFF707987)
)

val DarkColorScheme = darkColorScheme(
    primary = LockeyBlueDark,
    onPrimary = Color(0xFF002F70),
    primaryContainer = Color(0xFF074DBB),
    onPrimaryContainer = Color(0xFFDCE7FF),
    secondary = Color(0xFFBEC8D6),
    onSecondary = Color(0xFF283341),
    secondaryContainer = Color(0xFF3E4A58),
    onSecondaryContainer = Color(0xFFDCE6F3),
    tertiary = Color(0xFFB7CCE3),
    onTertiary = Color(0xFF203447),
    tertiaryContainer = Color(0xFF374B60),
    onTertiaryContainer = Color(0xFFD2E5FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    background = LockeySurfaceDark,
    onBackground = Color(0xFFE5EDF7),
    surface = LockeySurfaceDark,
    onSurface = Color(0xFFE5EDF7),
    surfaceVariant = Color(0xFF404954),
    onSurfaceVariant = Color(0xFFC0C8D3),
    outline = Color(0xFF8A94A2)
)
