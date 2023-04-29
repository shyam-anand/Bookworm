package com.shyamanand.bookworm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shyamanand.bookworm.R

val CormorantGaramond = FontFamily(
    Font(R.font.cormorantgaramond_300_light, FontWeight.W300),
    Font(R.font.cormorantgaramond_300_lightitalic, FontWeight.W300),
    Font(R.font.cormorantgaramond_400_regular, FontWeight.W400),
    Font(R.font.cormorantgaramond_400_italic, FontWeight.W400),
    Font(R.font.cormorantgaramond_500_medium, FontWeight.W500),
    Font(R.font.cormorantgaramond_500_mediumitalic, FontWeight.W500),
    Font(R.font.cormorantgaramond_600_semibold, FontWeight.W600),
    Font(R.font.cormorantgaramond_600_semibolditalic, FontWeight.W600),
    Font(R.font.cormorantgaramond_700_bold, FontWeight.W700),
    Font(R.font.cormorantgaramond_700_bolditalic, FontWeight.W700)
)

val NanumGothic = FontFamily(
    Font(R.font.nanumgothic_regular),
    Font(R.font.nanumgothic_bold, FontWeight.W700),
    Font(R.font.nanumgothic_extrabold, FontWeight.W800)
)

// Set of Material typography styles to start with
val BookshelfTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.W500,
        fontSize = 30.sp
    ),
    displayMedium = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.W500,
        fontSize = 24.sp
    ),
    displaySmall = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.W500,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.W400,
        fontSize = 28.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.W400,
        fontSize = 24.sp
    ),
    bodySmall = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.W400,
        fontSize = 20.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = CormorantGaramond,
        fontSize = 30.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = CormorantGaramond,
    ),
    headlineSmall = TextStyle(
        fontFamily = CormorantGaramond
    ),
    titleLarge = TextStyle(
        fontFamily = CormorantGaramond
    ),
    titleMedium = TextStyle(
        fontFamily = CormorantGaramond
    ),
    titleSmall = TextStyle(
        fontFamily = CormorantGaramond
    ),
    labelLarge = TextStyle(
        fontFamily = NanumGothic,
        fontSize = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = NanumGothic,
        fontSize = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = NanumGothic,
        fontSize = 16.sp
    )
)