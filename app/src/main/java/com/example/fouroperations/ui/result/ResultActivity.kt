package com.example.fouroperations.ui.result

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultScreen(
    stars: Int,
    max: Int,
    onPlayAgain: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1.05f,
        animationSpec = tween(450),
        label = "resultScale"
    )

    val message = when {
        stars == max -> "Perfeito! 🌟🌟🌟"
        stars >= (max * 0.7) -> "Mandou bem! 🎉"
        stars >= (max * 0.4) -> "Muito bom! 😊"
        else -> "Vamos treinar mais! 💪🙂"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))

        Text(
            text = "Resultado",
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "⭐ $stars / $max",
            fontSize = 44.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.scale(scale),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = message,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Jogar de novo", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(12.dp))
    }
}
