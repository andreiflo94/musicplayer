package com.example.musicplayer.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Default = Typography()

val Typography = Typography(
    displaySmall = Default.displaySmall.copy(
        fontWeight = FontWeight.Black,
        letterSpacing = (-1).sp
    ),
    headlineLarge = Default.headlineLarge.copy(
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.8).sp
    ),
    headlineMedium = Default.headlineMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = Default.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.3).sp
    ),
    titleLarge = Default.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp),
    titleMedium = Default.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = Default.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = Default.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    labelMedium = Default.labelMedium.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.6.sp
    ),
)
