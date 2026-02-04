package com.example.fouroperations.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.fouroperations.ui.theme.FredokaFamily

@Composable
fun ThreeDSurface(
    containerColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(26.dp),
    height: Dp? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val isInteractive = onClick != null

    val offsetY by animateDpAsState(
        targetValue = if (pressed && isInteractive) 3.dp else 0.dp,
        animationSpec = tween(120),
        label = "offsetY"
    )
    val elevation by animateDpAsState(
        targetValue = if (pressed && isInteractive) 6.dp else 14.dp,
        animationSpec = tween(140),
        label = "elevation"
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed && isInteractive) 0.97f else 1f,
        animationSpec = tween(140),
        label = "scale"
    )

    val darker = lerp(containerColor, Color.Black, 0.18f)
    val brush = Brush.verticalGradient(listOf(containerColor, darker))

    val baseModifier = modifier
        .then(if (height != null) Modifier.height(height) else Modifier)
        .offset(y = offsetY)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .shadow(elevation, shape)
        .clip(shape)
        .background(brush)

    val clickableModifier = if (onClick != null) {
        baseModifier.clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        baseModifier
    }

    Box(
        modifier = clickableModifier,
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun ThreeDButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    enabled: Boolean = true,
    height: Dp? = 72.dp,
    fontSize: TextUnit = 26.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    fontFamily: FontFamily = FredokaFamily
) {
    ThreeDSurface(
        containerColor = containerColor,
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        height = height
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = contentColor,
            fontFamily = fontFamily
        )
    }
}
