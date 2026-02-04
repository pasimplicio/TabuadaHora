package com.example.fouroperations.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fouroperations.R
import com.example.fouroperations.ui.components.ThreeDButton
import com.example.fouroperations.ui.theme.FredokaFamily
import com.example.fouroperations.util.UserProfile

@Composable
fun UserGateScreen(
    users: List<UserProfile>,
    activeUserId: String,
    maxScore: Int,
    onAddUser: (String) -> Unit,
    onSelectUser: (String) -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    val normalizedName = nameInput.trim().uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF2E9))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "TabuadadaHora",
            modifier = Modifier.height(72.dp)
        )

        Spacer(Modifier.height(24.dp))

        ThreeDButton(
            text = "Cadastrar novo usuário",
            onClick = { isDialogOpen = true },
            containerColor = Color(0xFF009688),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Escolha um usuário",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B2F0D),
            fontFamily = FredokaFamily
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (users.isEmpty()) {
                item {
                    Text(
                        text = "Nenhum usuário cadastrado.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B2F0D),
                        fontFamily = FredokaFamily
                    )
                }
            } else {
                items(users, key = { it.id }) { user ->
                    UserCard(
                        user = user,
                        maxScore = maxScore,
                        selected = user.id == activeUserId,
                        onClick = { onSelectUser(user.id) }
                    )
                }
            }
        }

        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    isDialogOpen = false
                    nameInput = ""
                    nameError = null
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                title = {
                    Text(
                        text = "Novo usuário",
                        fontFamily = FredokaFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                nameError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    text = "Nome do usuário",
                                    fontFamily = FredokaFamily
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FredokaFamily
                            )
                        )
                        if (nameError != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = nameError.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontFamily = FredokaFamily
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val exists = users.any { it.name == normalizedName }
                            when {
                                normalizedName.isBlank() -> {
                                    nameError = "Digite um nome."
                                }
                                exists -> {
                                    nameError = "Esse usuário já existe. Escolha outro nome."
                                }
                                else -> {
                                    onAddUser(normalizedName)
                                    isDialogOpen = false
                                    nameInput = ""
                                    nameError = null
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "Cadastrar",
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FredokaFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isDialogOpen = false
                            nameInput = ""
                            nameError = null
                        }
                    ) {
                        Text(
                            text = "Cancelar",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FredokaFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun UserCard(
    user: UserProfile,
    maxScore: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val addScore = user.bestScores["ADD"] ?: 0
    val subScore = user.bestScores["SUB"] ?: 0
    val mulScore = user.bestScores["MUL"] ?: 0
    val divScore = user.bestScores["DIV"] ?: 0

    ThreeDButton(
        text = user.name,
        onClick = onClick,
        containerColor = if (selected) Color(0xFF009688) else Color(0xFF6B2F0D),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFF7A5C), shape = RoundedCornerShape(14.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScorePill(label = "+", score = addScore, maxScore = maxScore)
        ScorePill(label = "−", score = subScore, maxScore = maxScore)
        ScorePill(label = "×", score = mulScore, maxScore = maxScore)
        ScorePill(label = "÷", score = divScore, maxScore = maxScore)
    }
}

@Composable
private fun ScorePill(
    label: String,
    score: Int,
    maxScore: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            fontFamily = FredokaFamily
        )
        Text(
            text = "$score/$maxScore",
            fontSize = 12.sp,
            color = Color.White,
            fontFamily = FredokaFamily
        )
    }
}
