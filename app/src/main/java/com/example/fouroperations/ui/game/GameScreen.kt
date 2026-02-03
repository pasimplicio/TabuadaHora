package com.example.fouroperations.ui.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameScreen(
    ui: GameUiState,
    onAnswer: (Int) -> Unit,
    onCorrect: () -> Unit,
    onWrong: () -> Unit,
    onNext: () -> Unit,
    onQuit: () -> Unit
) {
    if (!ui.isGameReady) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Preparando o jogo… 😊", fontSize = 22.sp)
        }
        return
    }

    val scale by animateFloatAsState(
        targetValue = when (ui.lastWasCorrect) {
            true -> 1.05f
            false -> 0.97f
            null -> 1f
        },
        animationSpec = tween(180),
        label = "scale"
    )

    val bgColor by animateColorAsState(
        targetValue = when (ui.lastWasCorrect) {
            true -> MaterialTheme.colorScheme.tertiaryContainer
            false -> MaterialTheme.colorScheme.errorContainer
            null -> MaterialTheme.colorScheme.secondaryContainer
        },
        label = "bg"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(20.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("⭐ ${ui.stars}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("${ui.questionCount}/${ui.maxQuestions}", fontSize = 18.sp)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = ui.current!!.text(),
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ui.options.forEach { opt ->
                Button(
                    onClick = {
                        onAnswer(opt)
                        if (opt == ui.current.answer) onCorrect() else onWrong()
                    },
                    enabled = ui.lastWasCorrect == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(opt.toString(), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        when (ui.lastWasCorrect) {
            true -> Text("Muito bem! 🎉", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            false -> Text("Tenta de novo 🙂", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            null -> {}
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onQuit,
                modifier = Modifier.weight(1f)
            ) { Text("Sair") }

            Button(
                onClick = onNext,
                enabled = ui.lastWasCorrect != null,
                modifier = Modifier.weight(1f)
            ) { Text("Próxima") }
        }
    }
}
