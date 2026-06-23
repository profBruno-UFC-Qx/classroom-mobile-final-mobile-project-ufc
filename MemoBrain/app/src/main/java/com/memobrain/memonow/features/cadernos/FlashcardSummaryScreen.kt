package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FlashcardSummaryScreen(viewModel: FlashcardSummaryViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFC))
            .padding(24.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Barra de Progresso no Topo (Totalmente preenchida)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(state.questionNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = { state.progressPercentage },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF4DB6AC),
                trackColor = Color(0xFFE0F2F1)
            )
        }

        // Conteúdo Central (Ilustração e Mensagens)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Placeholder para a Ilustração do Cérebro com Coroa
            Box(
                modifier = Modifier
                    .size(140.dp),
                contentAlignment = Alignment.Center,
            ) {
                // Aqui você usará o seu Image(painter = painterResource(id = ...))
                // Representação textual temporária da ilustração:
                Text("🧠👑", fontSize = 64.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mandou bem!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A5222)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Lição concluída · +${state.xpEarned} XP",
                fontSize = 14.sp,
                color = Color(0xFF4DB6AC),
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Grid de Estatísticas (Acertos e Tempo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Card de Acertos
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = state.accuracyPercentage,
                    label = "acertos",
                    iconBackgroundColor = Color(0xFFE8F5E9),
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Card de Tempo
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = state.timeSpent,
                    label = "tempo",
                    iconBackgroundColor = Color(0xFFE3F2FD),
                ) {
                    // Círculo simulando o ícone de relógio
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF1565C0), shape = CircleShape),
                    )
                }
            }
        }

        // Botão Fechar no Rodapé
        Button(
            onClick = { viewModel.onEvent(FlashcardSummaryEvent.OnCloseClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F)), // Azul escuro padrão
            shape = RoundedCornerShape(25.dp),
        ) {
            Text("Fechar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    iconBackgroundColor: Color,
    icon: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(iconBackgroundColor, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
        }
    }
}