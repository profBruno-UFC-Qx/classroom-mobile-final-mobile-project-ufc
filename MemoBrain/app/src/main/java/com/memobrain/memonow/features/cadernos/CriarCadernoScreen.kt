package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Monitora se salvou com sucesso para voltar de tela automaticamente
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
                        color = Color(0xFF2D3748)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color(0xFF2D3748)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF8F9FA)
                )
            )
        },
        containerColor = Color(0xFFF8F9FA),
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Seção de Imagem (Opcional)
            Text("Imagem do Caderno (opcional)", fontSize = 14.sp, color = Color(0xFF718096))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable { /* Seleção de imagem */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Selecionar imagem", color = Color(0xFFA0AEC0), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF1E466B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Nome do Caderno
            Text("Nome do Caderno", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2D3748))
            OutlinedTextField(
                value = state.nome,
                onValueChange = { viewModel.onNomeAlterado(it) },
                placeholder = { Text("Digite o nome do caderno", color = Color(0xFFA0AEC0)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // Descrição
            Text("Descrição (opcional)", fontSize = 14.sp, color = Color(0xFF718096))
            OutlinedTextField(
                value = state.descricao,
                onValueChange = { viewModel.onDescricaoAlterada(it) },
                placeholder = { Text("Adicione uma descrição", color = Color(0xFFA0AEC0)) },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // Seleção de Cor
            Text("Cor (opcional)", fontSize = 14.sp, color = Color(0xFF718096))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                viewModel.listaCores.forEach { corHex ->
                    val color = Color(corHex)
                    val isSelected = state.corSelecionada == corHex
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { viewModel.onCorSelecionada(corHex) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botões de Ação Inferiores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("CANCELAR", color = Color(0xFF718096), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.salvarCaderno() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E466B)),
                    shape = RoundedCornerShape(24.dp),
                    enabled = state.nome.isNotBlank()
                ) {
                    Text("SALVAR", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}