package com.example.fouroperations.ui.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fouroperations.ui.components.ThreeDButton
import com.example.fouroperations.ui.components.ThreeDSurface
import com.example.fouroperations.ui.theme.FredokaFamily

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
            Text(
                "Preparando o jogo… 😊",
                fontSize = 22.sp,
                fontFamily = FredokaFamily
            )
        }
        return
    }

    val bgColor by animateColorAsState(
        targetValue = when (ui.lastWasCorrect) {
            true -> MaterialTheme.colorScheme.tertiaryContainer
            false -> MaterialTheme.colorScheme.errorContainer
            null -> MaterialTheme.colorScheme.secondaryContainer
        },
        label = "bg"
    )

    val question = ui.current
    val playfulFont = FredokaFamily

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ScoreBadge(
                label = "Acertos",
                value = ui.stars,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
            ScoreBadge(
                label = "Erros",
                value = ui.errors,
                color = Color(0xFFE53935),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f),
            contentAlignment = Alignment.Center
        ) {
            if (question != null) {
                val resultText = when (ui.lastWasCorrect) {
                    true -> question.answer.toString()
                    false -> "X"
                    null -> "?"
                }
                val resultColor = when (ui.lastWasCorrect) {
                    true -> Color(0xFF2E7D32)
                    false -> Color(0xFFD32F2F)
                    null -> MaterialTheme.colorScheme.onSecondaryContainer
                }

                Text(
                    text = buildAnnotatedString {
                        append("${question.a} ${question.op.symbol} ${question.b} = ")
                        withStyle(SpanStyle(color = resultColor)) {
                            append(resultText)
                        }
                    },
                    fontSize = 42.sp,
                    lineHeight = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = playfulFont
                )
            }
        }

        val optionColors = listOf(
            Color(0xFF39B8FF),
            Color(0xFFFF8A00),
            Color(0xFFFFC400),
            Color(0xFF7B6CF6)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.4f)
        ) {
            ui.options.chunked(2).forEachIndexed { rowIndex, rowOptions ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    rowOptions.forEachIndexed { colIndex, opt ->
                        val color = optionColors[(rowIndex * 2 + colIndex) % optionColors.size]
                        ThreeDButton(
                            text = opt.toString(),
                            onClick = {
                                onAnswer(opt)
                                if (opt == question?.answer) onCorrect() else onWrong()
                            },
                            enabled = ui.lastWasCorrect == null,
                            containerColor = color,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            height = null,
                            fontSize = 34.sp,
                            fontFamily = playfulFont
                        )
                    }
                    if (rowOptions.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            contentAlignment = Alignment.Center
        ) {
            when (ui.lastWasCorrect) {
                true -> Text(
                    "Parabéns, você acertou! 🎉",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = playfulFont
                )
                false -> Text(
                    "Que pena, Tenta de novo 🙂",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = playfulFont
                )
                null -> {}
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThreeDButton(
                text = "Sair",
                onClick = onQuit,
                containerColor = Color(0xFF6D4C41),
                modifier = Modifier.weight(1f),
                fontFamily = playfulFont,
                height = 80.dp,
                fontSize = 26.sp
            )

            ThreeDButton(
                text = "Próxima",
                onClick = onNext,
                enabled = ui.lastWasCorrect != null,
                containerColor = Color(0xFF009688),
                modifier = Modifier.weight(1f),
                fontFamily = playfulFont,
                height = 80.dp,
                fontSize = 26.sp
            )
        }
    }
}

@Composable
private fun ScoreBadge(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    ThreeDSurface(
        containerColor = color,
        modifier = modifier,
        height = 72.dp
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontFamily = FredokaFamily
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FredokaFamily
            )
        }
    }
}
