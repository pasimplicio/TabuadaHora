package com.example.fouroperations.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fouroperations.model.Operation

@Composable
fun MenuScreen(
    onPick: (Operation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = "TabuadaHora!",
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Escolha uma operação 😊",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(Modifier.height(32.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OperationButton("Somar  +") { onPick(Operation.ADD) }
            OperationButton("Subtrair  −") { onPick(Operation.SUB) }
            OperationButton("Multiplicar  ×") { onPick(Operation.MUL) }
            OperationButton("Dividir  ÷") { onPick(Operation.DIV) }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "Toque na resposta certa para ganhar ⭐",
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun OperationButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
