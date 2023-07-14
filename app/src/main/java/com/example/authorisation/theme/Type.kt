package com.example.authorisation.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.authorisation.R

// Set of Material typography styles to start with
val Typography = Typography(

    body1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    )
)