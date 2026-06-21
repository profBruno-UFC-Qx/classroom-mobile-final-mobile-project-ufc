package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.* // 🟢 Garante o import de Scaffold, Button, Text, etc.
import androidx.compose.material3.Card // 🟢 CORREÇÃO: Import explícito do Card do Material 3
import androidx.compose.material3.CardDefaults // 🟢 CORREÇÃO: Import explícito das propriedades do Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheCadernoScreen(
    modifier: Modifier = Modifier,
    viewModel: DetalheCadernoViewModel = viewModel(),
    onBackClick: () -> Unit,
    onNovoArquivoClick: () -> Unit,
    onTopicoClick: (String) -> Unit,
    onEditarArquivoClick: (String) -> Unit = {} // 🟢 Adicionado callback para edição
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Cadernos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D3748)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF7FAFC)
                )
            )
        },
        bottomBar = {
            MenuInferiorMemonow(
                abaSelecionada = AbaMenu.CADERNOS,
                onAbaClick = { aba ->
                    if (aba == AbaMenu.INICIO) {
                        onBackClick() // 🟢 Retorna para o painel principal ao clicar no Início
                    }
                }
            )
        },
        containerColor = Color(0xFFF7FAFC),
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            SelectorAbasSimulado()

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color(0xFF2D3748)
                    )
                }

                Text(
                    text = state.nomeCaderno.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )

                IconButton(onClick = { /* Opções extras */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = Color(0xFF2D3748)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1E466B))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(state.listaTopicos) { topico ->
                        CardTopicoItem(
                            topico = topico,
                            onClick = { onTopicoClick(topico.titulo) },
                            onEditarClick = { onEditarArquivoClick(topico.titulo) }
                        )
                    }
                }
            }

            Button(
                onClick = onNovoArquivoClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E466B)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "NOVO ARQUIVO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CardTopicoItem(
    topico: TopicoExercicio, 
    onClick: () -> Unit,
    onEditarClick: () -> Unit = {} // 🟢 Novo callback
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Doc", fontSize = 14.sp, color = Color(0xFF718096), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = topico.titulo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748)
                    )
                    IconButton(onClick = onEditarClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF718096),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = topico.codigoPdf,
                        fontSize = 11.sp,
                        color = Color(0xFF718096)
                    )
                    Text(
                        text = "${topico.qtdQuestoes} questões",
                        fontSize = 11.sp,
                        color = Color(0xFF718096)
                    )
                }
            }
        }
    }
}

@Composable
fun SelectorAbasSimulado() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color(0xFFEDF2F7), RoundedCornerShape(24.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color.White, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Meus (6)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748))
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text("Públicos", fontSize = 13.sp, color = Color(0xFFA0AEC0))
        }
    }
}