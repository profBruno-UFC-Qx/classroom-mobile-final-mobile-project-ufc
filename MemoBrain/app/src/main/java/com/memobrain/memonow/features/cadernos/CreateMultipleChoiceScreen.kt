package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.material3.MaterialTheme

@Composable
fun CreateMultipleChoiceScreen(
    cadernoId: String,
    arquivoId: String,
    viewModel: CreateMultipleChoiceViewModel,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.salvoComSucesso) {
        if (state.salvoComSucesso) {
            viewModel.consumirSucesso()
            onNavigateBack()
        }
    }

    val corFoco = MaterialTheme.colorScheme.primary
    val corBorda = MaterialTheme.colorScheme.outline

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 680.dp),
            shape = RoundedCornerShape(18.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !state.isSalvando,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = MaterialTheme.colorScheme.onSurface                        )
                    }
                }

                Text(
                    text = "Criar Múltipla Escolha",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Cadastre a pergunta e marque a alternativa correta.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "PERGUNTA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.pergunta,
                    onValueChange = viewModel::onPerguntaChange,
                    placeholder = {
                        Text("Ex.: Qual algoritmo utiliza hiperplanos?")
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(88.dp),
                    enabled = !state.isSalvando,
                    shape = RoundedCornerShape(14.dp),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = corFoco,
                            unfocusedBorderColor = corBorda,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = corFoco,
                        ),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ALTERNATIVAS (Marque a correta)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant                )

                Spacer(modifier = Modifier.height(10.dp))

                state.opcoes.forEachIndexed { indice, opcao ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = state.indiceCorreto == indice,
                            onClick = {
                                viewModel.selecionarCorreta(indice)
                            },
                            enabled = !state.isSalvando,
                            colors =
                                RadioButtonDefaults.colors(
                                    selectedColor = corFoco,
                                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant                                ),
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = opcao.texto,
                            onValueChange = {
                                viewModel.onOpcaoChange(
                                    indice = indice,
                                    valor = it,
                                )
                            },
                            placeholder = {
                                Text("Opção ${indice + 1}")
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !state.isSalvando,
                            shape = RoundedCornerShape(14.dp),
                            colors =
                                OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedBorderColor = corFoco,
                                    unfocusedBorderColor = corBorda,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    cursorColor = corFoco,
                                ),
                        )

                        if (state.opcoes.size > 2) {
                            IconButton(
                                onClick = {
                                    viewModel.removerOpcao(indice)
                                },
                                enabled = !state.isSalvando,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remover alternativa",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (state.podeAdicionarOpcao) {
                    TextButton(
                        onClick = viewModel::adicionarOpcao,
                        enabled = !state.isSalvando,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar alternativa",
                            tint = corFoco,
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Adicionar alternativa",
                            color = corFoco,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                state.mensagemErro?.let { erro ->
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = erro,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 16.dp,
                            ),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Button(
                        onClick = onNavigateBack,
                        enabled = !state.isSalvando,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(48.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        shape = RoundedCornerShape(24.dp),
                    ) {
                        Text(
                            text = "Cancelar",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.salvarQuestao(
                                cadernoId = cadernoId,
                                arquivoId = arquivoId,
                            )
                        },
                        enabled = !state.isSalvando,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(48.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = corFoco,
                            ),
                        shape = RoundedCornerShape(24.dp),
                    ) {
                        if (state.isSalvando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Salvar",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}
