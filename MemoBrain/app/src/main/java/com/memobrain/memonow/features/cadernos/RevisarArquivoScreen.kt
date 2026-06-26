package com.memobrain.memonow.features.cadernos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.memobrain.memonow.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisarArquivoScreen(
    cadernoId: String,
    cadernoTitulo: String,
    arquivoId: String,
    tituloArquivo: String,
    descricaoArquivo: String,
    modifier: Modifier = Modifier,
    viewModel: RevisarArquivoViewModel = viewModel(),
    onFecharClick: () -> Unit,
    onAdicionarPerguntaAbertaClick: () -> Unit,
    onAdicionarMultiplaEscolhaClick: () -> Unit,
    onFinalizado: (Int, Int, Long) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    var mostrarExcluir by remember {
        mutableStateOf(false)
    }

    var mostrarMetodos by remember {
        mutableStateOf(false)
    }

    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

    // Cores baseadas no MaterialTheme ou mantendo feedbacks lógicos padrão (Acerto/Erro)
    val corAtiva = MaterialTheme.colorScheme.primary
    val corAcerto = Color(0xFF1DBF84) // Verde feedback
    val corErro = Color(0xFFFF5A5F)   // Vermelho feedback
    val corFundo = MaterialTheme.colorScheme.background

    LaunchedEffect(cadernoId, arquivoId) {
        viewModel.carregarConteudos(
            cadernoId = cadernoId,
            cadernoTitulo = cadernoTitulo,
            arquivoId = arquivoId,
            arquivoTitulo = tituloArquivo,
            arquivoDescricao = descricaoArquivo,
        )
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(corFundo),
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = corAtiva,
                    )
                }
            }

            state.conteudoAtual == null -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconButton(
                            onClick = onFecharClick,
                            modifier = Modifier.align(Alignment.CenterStart),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                        Text(
                            text = tituloArquivo,
                            modifier =
                                Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 52.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Nenhum conteúdo cadastrado neste arquivo.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            TextButton(
                                onClick = {
                                    mostrarMetodos = true
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Adicionar",
                                    tint = corAtiva,
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "Adicionar",
                                    color = corAtiva,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }

            else -> {
                val conteudo = state.conteudoAtual
                val alternativas = conteudo?.alternativas.orEmpty()
                val indiceCorreto = conteudo?.indiceCorreto ?: -1

                val espacoFeedback =
                    if (state.feedbackVisivel) {
                        205.dp
                    } else {
                        24.dp
                    }

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(
                                start = 24.dp,
                                end = 24.dp,
                                top = 24.dp,
                                bottom = espacoFeedback,
                            ),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconButton(
                            onClick = onFecharClick,
                            modifier = Modifier.align(Alignment.CenterStart),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                        Column(
                            modifier =
                                Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = tituloArquivo,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )

                            Text(
                                text =
                                    descricaoArquivo.ifBlank {
                                        if (state.ehPerguntaAberta) {
                                            "Pergunta Aberta"
                                        } else {
                                            "Múltipla Escolha"
                                        }
                                    },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = state.numeroQuestao,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        LinearProgressIndicator(
                            progress = { state.progresso },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(CircleShape),
                            color = corAtiva,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = state.porcentagem,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "PERGUNTA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = conteudo?.pergunta.orEmpty(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    if (state.ehPerguntaAberta) {
                        Text(
                            text = "SUA RESPOSTA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.respostaAberta,
                            onValueChange = viewModel::atualizarRespostaAberta,
                            placeholder = {
                                Text("Digite sua resposta aqui...")
                            },
                            enabled = !state.respondeu,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(108.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors =
                                OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedBorderColor = corAtiva,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    cursorColor = corAtiva,
                                ),
                        )
                    } else {
                        alternativas.forEachIndexed { indice, alternativa ->
                            val selecionada = state.opcaoSelecionada == indice
                            val correta = indiceCorreto == indice

                            val corBorda =
                                when {
                                    !state.respondeu && selecionada -> corAtiva
                                    state.respondeu && correta -> corAcerto
                                    state.respondeu && selecionada && !correta -> corErro
                                    else -> MaterialTheme.colorScheme.outline
                                }

                            val corTexto =
                                when {
                                    state.respondeu && selecionada && !correta -> corErro
                                    state.respondeu && correta -> corAcerto
                                    selecionada -> corAtiva
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                            Card(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                        .clickable(
                                            enabled = !state.respondeu,
                                        ) {
                                            viewModel.selecionarOpcao(indice)
                                        },
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                    ),
                                border =
                                    BorderStroke(
                                        width = 1.dp,
                                        color = corBorda,
                                    ),
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected =
                                            selecionada ||
                                                    (state.respondeu && correta),
                                        onClick = null,
                                        colors =
                                            RadioButtonDefaults.colors(
                                                selectedColor = corTexto,
                                                unselectedColor = MaterialTheme.colorScheme.outline,
                                            ),
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = alternativa,
                                        fontSize = 14.sp,
                                        fontWeight =
                                            if (selecionada) {
                                                FontWeight.Bold
                                            } else {
                                                FontWeight.Normal
                                            },
                                        color = corTexto,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = {
                                // Implementaremos a edição do conteúdo depois.
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Editar",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        TextButton(
                            onClick = {
                                mostrarExcluir = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error,
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Deletar",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }

                        TextButton(
                            onClick = {
                                mostrarMetodos = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adicionar",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onBackground,
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Adicionar",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!state.respondeu) {
                        Button(
                            onClick = viewModel::confirmarResposta,
                            enabled = state.podeConfirmar,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = corAtiva,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            shape = RoundedCornerShape(25.dp),
                        ) {
                            Text(
                                text =
                                    if (state.ehPerguntaAberta) {
                                        "Checar  >"
                                    } else {
                                        "CONFIRMAR"
                                    },
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    state.mensagemErro?.let { erro ->
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = erro,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                AnimatedVisibility(
                    visible = state.feedbackVisivel,
                    enter =
                        slideInVertically(
                            initialOffsetY = { it },
                        ),
                    exit =
                        slideOutVertically(
                            targetOffsetY = { it },
                        ),
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    PainelFeedback(
                        acertou = state.respostaCorreta,
                        respostaCorreta = state.feedbackRespostaCorreta,
                        temProximo = state.temProximo,
                        onContinuar = {
                            viewModel.continuarParaProximo(
                                onFinalizado = onFinalizado,
                            )
                        },
                    )
                }
            }
        }
    }

    if (mostrarExcluir) {
        DialogConfirmacaoExcluirConteudo(
            onCancelar = {
                mostrarExcluir = false
            },
            onExcluir = {
                mostrarExcluir = false
                viewModel.excluirConteudoAtual()
            },
        )
    }

    if (mostrarMetodos) {
        ModalBottomSheet(
            onDismissRequest = {
                mostrarMetodos = false
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = "Métodos de Estudo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                MetodoEscolhaItem(
                    texto = "Múltipla Escolha",
                    icone = R.drawable.ic_resposta_aberta,
                    onClick = {
                        mostrarMetodos = false
                        onAdicionarMultiplaEscolhaClick()
                    },
                )

                MetodoEscolhaItem(
                    texto = "Pergunta Aberta",
                    icone = R.drawable.ic_oclusao_imagem,
                    onClick = {
                        mostrarMetodos = false
                        onAdicionarPerguntaAbertaClick()
                    },
                )
            }
        }
    }
}

@Composable
private fun PainelFeedback(
    acertou: Boolean,
    respostaCorreta: String,
    temProximo: Boolean,
    onContinuar: () -> Unit,
) {
    val corFeedback = if (acertou) Color(0xFF1DBF84) else Color(0xFFFF5A5F)
    val titulo = if (acertou) "Mandou bem!" else "Quase!"
    val mensagem = if (acertou) "Parabéns pela resposta correta!" else "Resposta correta: $respostaCorreta"

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    color = corFeedback,
                    shape =
                        RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                        ),
                ).padding(24.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(24.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (acertou) "✓" else "×",
                    color = corFeedback,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = titulo,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mensagem,
            color = Color.White,
            fontSize = 14.sp,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onContinuar,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(46.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                ),
            shape = RoundedCornerShape(24.dp),
        ) {
            Text(
                text = if (temProximo) "Continuar" else "Finalizado",
                color = corFeedback,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun MetodoEscolhaItem(
    texto: String,
    icone: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = icone),
                    contentDescription = texto,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = texto,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun DialogConfirmacaoExcluirConteudo(
    onCancelar: () -> Unit,
    onExcluir: () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancelar,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(28.dp),
                    ).padding(
                        horizontal = 26.dp,
                        vertical = 34.dp,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_excluir,
                    ),
                contentDescription = "Excluir",
                modifier = Modifier.size(100.dp),
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Tem certeza que deseja excluir\neste card?",
                fontSize = 22.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Esta ação não pode ser desfeita e o card será removido",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = onCancelar,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Text(
                        text = "Cancelar",
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Button(
                    onClick = onExcluir,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Text(
                        text = "Excluir",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}