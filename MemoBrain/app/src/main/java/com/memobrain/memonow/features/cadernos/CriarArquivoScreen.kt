package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarArquivoScreen(
    modifier: Modifier = Modifier,
    viewModel: CriarArquivoViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

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
                        text = "Novo Arquivo",
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
            // Título do Arquivo
            Text("Título do Arquivo", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2D3748))
            OutlinedTextField(
                value = state.titulo,
                onValueChange = { viewModel.onTituloAlterado(it) },
                placeholder = { Text("Ex.: Algoritmos de Classificação", color = Color(0xFFA0AEC0)) },
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
                placeholder = { Text("Digite a descrição", color = Color(0xFFA0AEC0)) },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // Método de Estudo
            Text("Método de Estudo", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2D3748))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable { viewModel.setMostrarBottomSheet(true) },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.metodoSelecionado ?: "Selecione o método de estudo",
                        color = if (state.metodoSelecionado != null) Color(0xFF1E466B) else Color(0xFFA0AEC0),
                        fontWeight = if (state.metodoSelecionado != null) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                    if (state.metodoSelecionado == null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF1E466B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botões de Ação
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
                    onClick = { viewModel.salvarArquivo() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E466B)),
                    shape = RoundedCornerShape(24.dp),
                    enabled = state.titulo.isNotBlank() && state.metodoSelecionado != null
                ) {
                    Text("SALVAR", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Bottom Sheet de Métodos de Estudo
    if (state.mostrarBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setMostrarBottomSheet(false) },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Métodos de Estudo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )

                viewModel.listaMetodos.forEach { metodoItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selecionarMetodo(metodoItem.titulo) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFC)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = metodoItem.iconeResId),
                                    contentDescription = metodoItem.titulo,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = metodoItem.titulo,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D3748)
                            )
                        }
                    }
                }
            }
        }
    }
}