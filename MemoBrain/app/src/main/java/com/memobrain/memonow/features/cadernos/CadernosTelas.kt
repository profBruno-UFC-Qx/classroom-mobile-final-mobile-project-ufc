package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// 1. MODELO DE DADOS
data class Caderno(
    val id: String = "",
    val titulo: String = "",
    val revisados: Int = 0,
    val restantes: Int = 0
)

// 2. TELA PRINCIPAL (FRONT-END)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadernosTelas(
    modifier: Modifier = Modifier,
    viewModel: CadernosViewModel = viewModel(),
    onInicioClick: () -> Unit = {},
    onCadernoClick: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var tabSelecionada by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Cadernos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A2536)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF8F9FA)
                )
            )
        },
        bottomBar = {
            MenuInferiorExemplo(onInicioClick = onInicioClick)
        },
        containerColor = Color(0xFFF8F9FA),
        contentWindowInsets = WindowInsets.systemBars
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            TabSelector(
                selecionado = tabSelecionada,
                quantidadeMeus = state.listaCadernos.size,
                onTabSelected = { novaTab ->
                    tabSelecionada = novaTab
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4FA393))
                }
            } else {
                // Se o usuário selecionar a aba 0 (Meus), mostra os dados, se for a aba 1 (Públicos), mostra vazio por enquanto
                val listaFiltrada = if (tabSelecionada == 0) state.listaCadernos else emptyList()

                if (listaFiltrada.isEmpty() && tabSelecionada == 1) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Nenhum caderno público disponível.", color = Color(0xFF6C757D), fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(listaFiltrada) { caderno ->
                            CardCaderno(
                                caderno = caderno,
                                onClick = { onCadernoClick(caderno.id) }
                            )
                        }
                    }
                }
            }

            // Botão "NOVO CADERNO" com margem interna para não colar no menu inferior
            Button(
                onClick = { /* Ação criar caderno */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22496E)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "NOVO CADERNO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// 3. SELETOR DE ABAS
@Composable
fun TabSelector(selecionado: Int, quantidadeMeus: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color(0xFFF1F3F5), RoundedCornerShape(24.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val abas = listOf("Meus ($quantidadeMeus)", "Públicos")
        abas.forEachIndexed { index, texto ->
            val eOSelecionado = selecionado == index
            Button(
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (eOSelecionado) Color.White else Color.Transparent,
                    contentColor = if (eOSelecionado) Color(0xFF1A2536) else Color(0xFF6C757D)
                ),
                elevation = if (eOSelecionado) ButtonDefaults.buttonElevation(2.dp) else null,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(text = texto, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// 4. CARD INDIVIDUAL DO CADERNO
@Composable
fun CardCaderno(caderno: Caderno, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFE6F4F1), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📖", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = caderno.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A2536)
                    )
                    Text(
                        text = "Clique para abrir",
                        fontSize = 12.sp,
                        color = Color(0xFF9EA8B6)
                    )
                }

                IconButton(onClick = { /* Ação editar */ }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF9EA8B6),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${caderno.revisados} revisados",
                    fontSize = 11.sp,
                    color = Color(0xFF6C757D)
                )
                Text(
                    text = "${caderno.restantes} restantes",
                    fontSize = 11.sp,
                    color = Color(0xFF6C757D)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val total = (caderno.revisados + caderno.restantes).toFloat()
            val progresso = if (total > 0) caderno.revisados / total else 0f

            LinearProgressIndicator(
                progress = { progresso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFF4FA393),
                trackColor = Color(0xFFE9ECEF),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

// 5. MENU INFERIOR
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuInferiorExemplo(onInicioClick: () -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.navigationBarsPadding(),
        tonalElevation = 8.dp
    ) {
        val itens = listOf("Início", "Cadernos", "Progresso", "Perfil")
        itens.forEachIndexed { index, item ->
            val isSelected = index == 1
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (index == 0) onInicioClick()
                },
                label = { Text(text = item, fontSize = 11.sp) },
                icon = {
                    val emoji = when(index) {
                        0 -> "🏠"
                        1 -> "📖"
                        2 -> "📊"
                        else -> "👤"
                    }
                    Text(text = emoji, fontSize = 20.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF22496E),
                    selectedTextColor = Color(0xFF22496E),
                    indicatorColor = Color(0xFFE6EDF5)
                )
            )
        }
    }
}