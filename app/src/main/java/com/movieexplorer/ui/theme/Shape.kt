package com.movieexplorer.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CardShape = RoundedCornerShape(16.dp)
val FullyRoundedShape = CircleShape

val Shapes = Shapes(
    extraSmall = FullyRoundedShape,
    small = FullyRoundedShape,
    medium = CardShape,
    large = CardShape,
    extraLarge = RoundedCornerShape(28.dp)
)
