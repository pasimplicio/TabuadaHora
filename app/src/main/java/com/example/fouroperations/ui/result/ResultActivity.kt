package com.example.fouroperations.ui.result

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fouroperations.ui.theme.FredokaFamily

@Composable
fun ResultScreen(
    stars: Int,
    max: Int,
    isMusicMuted: Boolean,
    onToggleMusicMuted: () -> Unit,
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
        stars in 5..6 -> "Tá chegando lá!"
        stars >= (max * 0.4) -> "Muito bom! 😊"
        else -> "Vamos treinar mais! 💪🙂"
    }
    val musicLabel = if (isMusicMuted) "Desligado" else "Ligado"
    val musicIconRes = if (isMusicMuted) {
        android.R.drawable.ic_lock_silent_mode
    } else {
        android.R.drawable.ic_lock_silent_mode_off
    }
    val musicTint = if (isMusicMuted) {
        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable(onClick = onToggleMusicMuted)
            ) {
                Icon(
                    painter = painterResource(id = musicIconRes),
                    contentDescription = "Som de fundo",
                    tint = musicTint
                )
                Text(
                    text = musicLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = musicTint,
                    fontFamily = FredokaFamily,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(30.dp))

        Text(
            text = "Resultado",
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontFamily = FredokaFamily
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "⭐ $stars / $max",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.scale(scale),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontFamily = FredokaFamily
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = message,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontFamily = FredokaFamily
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                "Jogar de novo",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFamily
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}
