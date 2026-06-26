package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.memobrain.memonow.R
import androidx.compose.material3.MaterialTheme


@Composable
fun EditNotebookScreen(
    cadernoId: String,
    viewModel: EditNotebookViewModel,
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    var mostrarConfirmacaoExclusao by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(cadernoId) {
        viewModel.carregarCaderno(cadernoId)
    }

    LaunchedEffect(
        state.isSavedSuccessfully,
        state.isDeletedSuccessfully,
    ) {
        if (state.isSavedSuccessfully || state.isDeletedSuccessfully) {
            viewModel.consumirResultado()
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp,
                ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(48.dp))

                Text(
                    text = "Editar Caderno",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                IconButton(
                    onClick = onNavigateBack,
                    enabled = !state.isSaving && !state.isDeleting,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                Text(
                    text = "Nome do Caderno",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.nome,
                    onValueChange = viewModel::onNomeAlterado,
                    placeholder = {
                        Text("Ex.: Ciência de Dados")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving && !state.isDeleting,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Descrição (opcional)",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.descricao,
                    onValueChange = viewModel::onDescricaoAlterada,
                    placeholder = {
                        Text("Ex.: Resumos, exercícios e materiais da disciplina.")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp),
                    enabled = !state.isSaving && !state.isDeleting,
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Imagem do Caderno (opcional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable(
                            enabled = !state.isSaving && !state.isDeleting,
                        ) {
                            // Seleção de imagem será implementada depois.
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Selecionar imagem",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adicionar imagem",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Cor (opcional)",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.coresDisponiveis) { cor ->
                        val selecionada = state.corSelecionada == cor

                        Box(
                            modifier =
                                Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(cor))
                                    .border(
                                        width = if (selecionada) 3.dp else 0.dp,
                                        color =
                                            if (selecionada) {
                                                MaterialTheme.colorScheme.onSurface
                                            } else {
                                                Color.Transparent
                                            },
                                        shape = CircleShape,
                                    )
                                    .clickable(
                                        enabled = !state.isSaving && !state.isDeleting,
                                    ) {
                                        viewModel.onCorSelecionada(cor)
                                    },
                        )
                    }
                }

                state.errorMessage?.let { mensagem ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = mensagem,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                    )
                }

                /*
                 * Única linha divisória:
                 * separa os campos de TODOS os botões.
                 */
                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = onNavigateBack,
                        enabled = !state.isSaving && !state.isDeleting,
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
                            text = "CANCELAR",
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.salvarCaderno(cadernoId)
                        },
                        enabled =
                            state.nome.isNotBlank() &&
                                    !state.isSaving &&
                                    !state.isDeleting,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(48.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        shape = RoundedCornerShape(24.dp),
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "SALVAR",
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        mostrarConfirmacaoExclusao = true
                    },
                    enabled = !state.isSaving && !state.isDeleting,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    if (state.isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onError,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = "EXCLUIR CADERNO",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (mostrarConfirmacaoExclusao) {
        DialogConfirmacaoExclusaoCaderno(
            onCancelar = {
                mostrarConfirmacaoExclusao = false
            },
            onConfirmar = {
                mostrarConfirmacaoExclusao = false
                viewModel.excluirCaderno(cadernoId)
            },
        )
    }
}

@Composable
private fun DialogConfirmacaoExclusaoCaderno(
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit,
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
                    .padding(horizontal = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(28.dp),
                    )
                    .padding(
                        horizontal = 24.dp,
                        vertical = 36.dp,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter =
                    painterResource(
                        id = R.drawable.ic_excluir,
                    ),
                contentDescription = "Excluir caderno",
                modifier = Modifier.size(74.dp),
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Tem certeza que deseja excluir\neste caderno?",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Esta ação não pode ser desfeita e o caderno será removido.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Button(
                    onClick = onCancelar,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(46.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = null,
                ) {
                    Text(
                        text = "Cancelar",
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Button(
                    onClick = onConfirmar,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(46.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = null,
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
