package com.memobrain.memonow.features.cadernos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel(),
    onFecharClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val questao = state.questaoAtual

    val corPrimaria = Color(0xFF4FA393)
    val corErro = Color(0xFFFF5A5F)
    val corFundo = Color(0xFFF8F9FA)

    LaunchedEffect(Unit) {
        viewModel.resetarQuiz()
    }

    if (questao == null) return

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Machine Learning",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = corPrimaria
                        )
                        Text(
                            text = "PDF 00",
                            fontSize = 12.sp,
                            color = Color(0xFF2D3748)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetarQuiz()
                        onFecharClick()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color(0xFF2D3748)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = corFundo)
            )
        },
        containerColor = corFundo,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = state.numeroQuestaoDisplay, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = { state.progresso },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp),
                        color = corPrimaria,
                        trackColor = Color(0xFFE2E8F0),
                        strokeCap = StrokeCap.Round,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = state.porcentagemDisplay, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = questao.texto,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3748),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                questao.opcoes.forEachIndexed { index, textoOpcao ->
                    val isSelecionada = state.opcaoSelecionada == index
                    val isCorreta = questao.indiceCorreto == index

                    val corBorda = when {
                        !state.jaRespondeu && isSelecionada -> corPrimaria
                        !state.jaRespondeu && !isSelecionada -> Color(0xFFE2E8F0)
                        state.jaRespondeu && isCorreta -> corPrimaria
                        state.jaRespondeu && isSelecionada && !isCorreta -> corErro
                        else -> Color(0xFFE2E8F0)
                    }

                    val corTexto = if (state.jaRespondeu && isSelecionada && !isCorreta) corErro else corPrimaria

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clickable(enabled = !state.jaRespondeu) {
                                viewModel.selecionarOpcao(index)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, corBorda)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelecionada || (state.jaRespondeu && isCorreta),
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = corTexto,
                                    unselectedColor = Color(0xFFCBD5E1)
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = textoOpcao,
                                fontSize = 14.sp,
                                fontWeight = if (isSelecionada) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelecionada || (state.jaRespondeu && isCorreta)) corTexto else Color(0xFF4A5568)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (state.opcaoSelecionada != null && !state.jaRespondeu) {
                    Button(
                        onClick = { viewModel.confirmarResposta() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22496E)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("CONFIRMAR", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedVisibility(
                visible = state.jaRespondeu,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                val corFeedback = if (state.isRespostaCorreta) corPrimaria else corErro
                val tituloFeedback = if (state.isRespostaCorreta) "Mandou bem!" else "Quase!"
                val textoFeedback = if (state.isRespostaCorreta) {
                    "Parabéns pela resposta correta!"
                } else {
                    "Resposta correta: ${questao.opcoes[questao.indiceCorreto]}"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = corFeedback,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = corFeedback,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = tituloFeedback,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = textoFeedback,
                            color = Color.White,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (state.questaoAtualIndex < state.questoes.size - 1) {
                                    viewModel.proximaQuestao()
                                } else {
                                    viewModel.resetarQuiz()
                                    onFecharClick()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(
                                text = "Continuar",
                                color = corFeedback,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}