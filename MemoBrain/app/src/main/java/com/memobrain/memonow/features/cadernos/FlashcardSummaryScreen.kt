package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memobrain.memonow.R

@Composable
fun FlashcardSummaryScreen(viewModel: FlashcardSummaryViewModel) {
    val state by viewModel.state.collectAsState()

    // Mapeamento dinâmico usando o MaterialTheme do projeto
    val corFundo = MaterialTheme.colorScheme.background
    val corProgresso = MaterialTheme.colorScheme.primary // ou secondary, dependendo do design do app
    val corTrackProgresso = MaterialTheme.colorScheme.surfaceVariant
    val corTitulo = MaterialTheme.colorScheme.onBackground
    val corTextoSecundario = MaterialTheme.colorScheme.onSurfaceVariant
    val corBotao = MaterialTheme.colorScheme.primary
    val corTextoBotao = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(corFundo)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 36.dp),
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.questionNumber,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = corTextoSecundario,
            )

            Spacer(modifier = Modifier.width(10.dp))

            LinearProgressIndicator(
                progress = { state.progressPercentage },
                modifier =
                    Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(CircleShape),
                color = corProgresso,
                trackColor = corTrackProgresso,
            )
        }

        /*
         * Todo o conteúdo principal fica centralizado
         * na área disponível abaixo da barra de progresso.
         */
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(170.dp)
                            .offset(x = 15.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter =
                            painterResource(
                                id = R.drawable.ic_finalizando_flashcard,
                            ),
                        contentDescription = "Lição concluída",
                        modifier =
                            Modifier
                                .size(170.dp)
                                .graphicsLayer(
                                    scaleX = 2.8f,
                                    scaleY = 2.8f,
                                ),
                        contentScale = ContentScale.Fit,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mandou bem!",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = corTitulo,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Lição concluída",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = corProgresso,
                )

                Spacer(modifier = Modifier.height(26.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CartaoEstatistica(
                        modifier = Modifier.weight(1f),
                        valor = state.accuracyPercentage,
                        legenda = "acertos",
                        icone = R.drawable.ic_score,
                        escalaIcone = 0.9f,
                    )

                    CartaoEstatistica(
                        modifier = Modifier.weight(1f),
                        valor = state.timeSpent,
                        legenda = "tempo",
                        icone = R.drawable.ic_time,
                        escalaIcone = 3.2f,
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        viewModel.onEvent(
                            FlashcardSummaryEvent.OnCloseClicked,
                        )
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = corBotao,
                            contentColor = corTextoBotao
                        ),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Text(
                        text = "Fechar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun CartaoEstatistica(
    modifier: Modifier = Modifier,
    valor: String,
    legenda: String,
    icone: Int,
    escalaIcone: Float,
) {
    Card(
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(8.dp),
        border =
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant, // Borda adaptável
            ),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface, // Fundo do card adaptável
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 5.dp,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.size(17.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = icone),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(17.dp)
                            .graphicsLayer(
                                scaleX = escalaIcone,
                                scaleY = escalaIcone,
                            ),
                    contentScale = ContentScale.Fit,
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = valor,
                fontSize = 12.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface, // Texto principal adaptável
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = legenda,
                fontSize = 9.sp,
                lineHeight = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Texto secundário adaptável
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}