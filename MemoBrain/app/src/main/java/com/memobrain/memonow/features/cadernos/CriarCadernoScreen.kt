package com.memobrain.memonow.features.cadernos

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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarCadernoScreen(
    modifier: Modifier = Modifier,
    viewModel: CriarCadernoViewModel = viewModel(),
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSalvoSucesso) {
        if (state.isSalvoSucesso) {
            viewModel.resetarEstado()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Novo Caderno",
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
                text = "Nome do Caderno",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D3748),
            )

            OutlinedTextField(
                value = state.nome,
                onValueChange = viewModel::onNomeAlterado,
                placeholder = {
                    Text(
                        text = "Ex.: Ciência de Dados",
                    )
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
                fontWeight = FontWeight.Normal,
                color = Color(0xFF718096),
            )

            OutlinedTextField(
                value = state.descricao,
                onValueChange = viewModel::onDescricaoAlterada,
                placeholder = {
                    Text(
                        text = "Ex.: Resumos, exercícios e materiais da disciplina.",
                    )
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(104.dp),
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
                text = "Imagem do Caderno (opcional)",
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
                            // A seleção de imagem será implementada depois.
                        },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Selecionar imagem",
                        color = Color(0xFFA0AEC0),
                        fontSize = 14.sp,
                    )

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
                            contentDescription = "Adicionar imagem",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            Text(
                text = "Cor (opcional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF718096),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                viewModel.listaCores.forEach { corHex ->
                    val cor = Color(corHex)
                    val selecionada = state.corSelecionada == corHex

                    Box(
                        modifier =
                            Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(cor)
                                .border(
                                    width = if (selecionada) 3.dp else 0.dp,
                                    color =
                                        if (selecionada) {
                                            Color(0xFF2D3748)
                                        } else {
                                            Color.Transparent
                                        },
                                    shape = CircleShape,
                                ).clickable(
                                    enabled = !state.isSalvando,
                                ) {
                                    viewModel.onCorSelecionada(corHex)
                                },
                    )
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
                        viewModel.salvarCaderno()
                    },
                    enabled = state.nome.isNotBlank() && !state.isSalvando,
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

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
