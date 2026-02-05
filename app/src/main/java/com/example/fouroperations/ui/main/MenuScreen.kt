package com.example.fouroperations.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fouroperations.R
import com.example.fouroperations.model.Operation
import com.example.fouroperations.ui.components.ThreeDSurface
import com.example.fouroperations.ui.theme.FredokaFamily

@Composable
fun MenuScreen(
    onPick: (Operation) -> Unit
) {
    val playfulFont = FredokaFamily

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF2E9))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "TabuadadaHora",
            modifier = Modifier.height(200.dp)
        )

        Spacer(Modifier.height(24.dp))

        BannerLabel(text = "Adição e Subtração")

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OperationTile(
                symbol = "+",
                label = "Somar",
                color = Color(0xFF60D23A),
                fontFamily = playfulFont,
                symbolSize = 44.sp,
                labelSize = 18.sp,
                onClick = { onPick(Operation.ADD) }
            )
            OperationTile(
                symbol = "−",
                label = "Subtrair",
                color = Color(0xFF2FB8FF),
                fontFamily = playfulFont,
                symbolSize = 44.sp,
                labelSize = 18.sp,
                onClick = { onPick(Operation.SUB) }
            )
        }

        Spacer(Modifier.height(20.dp))

        BannerLabel(text = "Multiplicação e Divisão")

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OperationTile(
                symbol = "×",
                label = "Multiplicar",
                color = Color(0xFFFF9C3A),
                fontFamily = playfulFont,
                symbolSize = 44.sp,
                labelSize = 18.sp,
                onClick = { onPick(Operation.MUL) }
            )
            OperationTile(
                symbol = "÷",
                label = "Dividir",
                color = Color(0xFFFF5ED2),
                fontFamily = playfulFont,
                symbolSize = 44.sp,
                labelSize = 18.sp,
                onClick = { onPick(Operation.DIV) }
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Toque na resposta certa para ganhar ⭐",
            color = Color(0xFF6B2F0D),
            fontFamily = playfulFont,
            fontSize = 18.sp
        )

        Spacer(Modifier.height(16.dp))

        /*FooterCredits()*/
    }
}

@Composable
private fun RowScope.OperationTile(
    symbol: String,
    label: String,
    color: Color,
    fontFamily: FontFamily,
    symbolSize: TextUnit = 44.sp,
    labelSize: TextUnit = 18.sp,
    onClick: () -> Unit
) {
    ThreeDSurface(
        containerColor = color,
        onClick = onClick,
        modifier = Modifier.weight(1f),
        height = 132.dp
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = symbol,
                fontSize = symbolSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontFamily = fontFamily
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = labelSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
private fun BannerLabel(text: String) {
    Surface(
        color = Color(0xFFFF7A5C),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = FredokaFamily
        )
    }
}


@Composable
private fun SocialHandle(
    iconRes: Int,
    contentDescription: String,
    handle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.height(14.dp)
        )
        Text(
            text = handle,
            fontSize = 12.sp,
            color = Color.White,
            fontFamily = FredokaFamily
        )
    }
}
