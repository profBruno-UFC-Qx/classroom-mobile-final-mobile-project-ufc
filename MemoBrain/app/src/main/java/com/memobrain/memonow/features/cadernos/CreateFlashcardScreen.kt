package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateFlashcardScreen(
    cadernoId: String,
    arquivoId: String,
    viewModel: CreateFlashcardViewModel,
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.salvoComSucesso) {
        if (state.salvoComSucesso) {
            viewModel.consumirSucesso()
            viewModel.limparFormulario()
            onNavigateBack()
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFF666666)),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 8.dp,
                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
            ) {
                Text(
                    text = "Criar Pergunta Aberta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A2536),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Organize seu conhecimento com precisão e clareza.",
                    fontSize = 14.sp,
                    color = Color(0xFF7B8794),
                    lineHeight = 20.sp,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "PERGUNTA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8A94A6),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.questionText,
                    onValueChange = viewModel::onPerguntaAlterada,
                    placeholder = {
                        Text(
                            text = "Ex.: O que é Machine Learning?",
                            color = Color(0xFFB0B8C1),
                            fontSize = 14.sp,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFB0B8C1),
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF22496E),
                        ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "RESPOSTA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8A94A6),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.answerText,
                    onValueChange = viewModel::onRespostaAlterada,
                    placeholder = {
                        Text(
                            text = "Ex.: Área da IA que permite analisar dados.",
                            color = Color(0xFFB0B8C1),
                            fontSize = 14.sp,
                        )
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                    enabled = !state.isSaving,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxHeight()
                                    .padding(top = 16.dp),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null,
                                tint = Color(0xFFB0B8C1),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    },
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFF22496E),
                        ),
                )

                state.mensagemErro?.let { erro ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = erro,
                        color = Color(0xFFD32F2F),
                        fontSize = 13.sp,
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    thickness = 1.dp,
                    color = Color(0xFFE2E8F0),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = {
                            viewModel.limparFormulario()
                            onNavigateBack()
                        },
                        enabled = !state.isSaving,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(52.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE9ECEF),
                            ),
                        shape = RoundedCornerShape(26.dp),
                    ) {
                        Text(
                            text = "Cancelar",
                            color = Color(0xFF495057),
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.salvarFlashcard(
                                cadernoId = cadernoId,
                                arquivoId = arquivoId,
                            )
                        },
                        enabled = state.isValido && !state.isSaving,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(52.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF22496E),
                            ),
                        shape = RoundedCornerShape(26.dp),
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Salvar",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}
