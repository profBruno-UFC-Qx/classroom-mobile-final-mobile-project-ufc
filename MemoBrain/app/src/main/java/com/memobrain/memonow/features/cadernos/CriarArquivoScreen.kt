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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarArquivoScreen(
    cadernoId: String,
    modifier: Modifier = Modifier,
    viewModel: CriarArquivoViewModel = viewModel(),
    onBackClick: () -> Unit,
    onArquivoCriado: (ArquivoCriado) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(state.arquivoCriado?.id) {
        val arquivo = state.arquivoCriado ?: return@LaunchedEffect

        viewModel.consumirArquivoCriado()
        viewModel.resetarEstado()
        onArquivoCriado(arquivo)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Novo Arquivo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !state.isSalvando,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color(0xFF2D3748),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFF8F9FA),
                    ),
            )
        },
        containerColor = Color(0xFFF8F9FA),
    ) { innerPadding ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Título do Arquivo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D3748),
            )

            OutlinedTextField(
                value = state.titulo,
                onValueChange = viewModel::onTituloAlterado,
                placeholder = {
                    Text("Ex.: Princípios Processuais Penais")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSalvando,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF1E466B),
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedTextColor = Color(0xFF2D3748),
                        unfocusedTextColor = Color(0xFF2D3748),
                        focusedPlaceholderColor = Color(0xFFA0AEC0),
                        unfocusedPlaceholderColor = Color(0xFFA0AEC0),
                        cursorColor = Color(0xFF1E466B),
                    ),
            )

            Text(
                text = "Descrição (opcional)",
                fontSize = 14.sp,
                color = Color(0xFF718096),
            )

            OutlinedTextField(
                value = state.descricao,
                onValueChange = viewModel::onDescricaoAlterada,
                placeholder = {
                    Text("Ex.: Conceitos e exercícios para revisão.")
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                enabled = !state.isSalvando,
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF1E466B),
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedTextColor = Color(0xFF2D3748),
                        unfocusedTextColor = Color(0xFF2D3748),
                        focusedPlaceholderColor = Color(0xFFA0AEC0),
                        unfocusedPlaceholderColor = Color(0xFFA0AEC0),
                        cursorColor = Color(0xFF1E466B),
                    ),
            )

            Text(
                text = "Método de Estudo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D3748),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFCBD5E1),
                            shape = RoundedCornerShape(12.dp),
                        ).background(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                        ).clickable(
                            enabled = !state.isSalvando,
                        ) {
                            viewModel.setMostrarBottomSheet(true)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text =
                            state.metodoSelecionado
                                ?: "Selecione o método de estudo",
                        color =
                            if (state.metodoSelecionado != null) {
                                Color(0xFF1E466B)
                            } else {
                                Color(0xFFA0AEC0)
                            },
                        fontWeight =
                            if (state.metodoSelecionado != null) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                        fontSize = 14.sp,
                    )

                    if (state.metodoSelecionado == null) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier =
                                Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E466B)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Selecionar método",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }

            state.mensagemErro?.let { erro ->
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
                    onClick = onBackClick,
                    enabled = !state.isSalvando,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE2E8F0),
                        ),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Text(
                        text = "CANCELAR",
                        color = Color(0xFF718096),
                        fontWeight = FontWeight.Bold,
                    )
                }

                Button(
                    onClick = {
                        viewModel.salvarArquivo(cadernoId)
                    },
                    enabled =
                        state.titulo.isNotBlank() &&
                            state.metodoSelecionado != null &&
                            !state.isSalvando,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E466B),
                        ),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    if (state.isSalvando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "SALVANDO",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    } else {
                        Text(
                            text = "SALVAR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }

    if (state.mostrarBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.setMostrarBottomSheet(false)
            },
            sheetState = sheetState,
            containerColor = Color.White,
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
                    color = Color(0xFF2D3748),
                )

                viewModel.listaMetodos.forEach { metodo ->
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selecionarMetodo(metodo.titulo)
                                },
                        colors =
                            CardDefaults.cardColors(
                                containerColor = Color(0xFFF7FAFC),
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
                                            color = Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter =
                                        painterResource(
                                            id = metodo.iconeResId,
                                        ),
                                    contentDescription = metodo.titulo,
                                    modifier = Modifier.size(24.dp),
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = metodo.titulo,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D3748),
                            )
                        }
                    }
                }
            }
        }
    }
}
