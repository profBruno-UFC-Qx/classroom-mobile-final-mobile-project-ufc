package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

data class Caderno(
    val id: String = "",
    val titulo: String = "",
    val revisados: Int = 0,
    val restantes: Int = 0,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCadernosTela(
    modifier: Modifier = Modifier,
    onIrParaInicio: () -> Unit = {},
    onCadernoClick: (String) -> Unit = {},
    onNovoCadernoClick: () -> Unit = {}, // 🟢 Adicionado o parâmetro de clique aqui
    viewModel: CadernosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listaCadernos = uiState.listaCadernos

    var tabSelecionada by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Cadernos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A2536),
                    )
                },
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFF6F8FB),
                    ),
            )
        },
        bottomBar = {
            // Mantendo o seu componente padrão de menu inferior
            MenuInferiorMemonow(
                abaSelecionada = AbaMenu.CADERNOS,
                onAbaClick = { aba ->
                    when (aba) {
                        AbaMenu.INICIO -> onIrParaInicio()
                        else -> {}
                    }
                },
            )
        },
        containerColor = Color(0xFFF6F8FB),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
        ) {
            TabSelector(
                selecionado = tabSelecionada,
                quantidadeMeus = listaCadernos.size,
                onTabSelected = { novaTab ->
                    tabSelecionada = novaTab
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(uiState.listaCadernos) { caderno ->
                    CardCaderno(
                        caderno = caderno,
                        onClick = { onCadernoClick(caderno.id) }
                    )
                }
            }

            Button(
                onClick = onNovoCadernoClick, // 🟢 Vinculado à ação para abrir a tela do formulário
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 8.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22496E),
                    ),
                shape = RoundedCornerShape(28.dp),
            ) {
                Text(
                    text = "NOVO CADERNO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }
        }
    }
}

@Composable
fun TabSelector(
    selecionado: Int,
    quantidadeMeus: Int,
    onTabSelected: (Int) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFFF1F3F5), RoundedCornerShape(24.dp))
                .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val abas = listOf("Meus ($quantidadeMeus)", "Públicos")

        abas.forEachIndexed { index, texto ->
            val selecionadoAgora = selecionado == index

            Button(
                onClick = { onTabSelected(index) },
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = if (selecionadoAgora) Color.White else Color.Transparent,
                        contentColor = if (selecionadoAgora) Color(0xFF1A2536) else Color(0xFF6C757D),
                    ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = texto,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
fun CardCaderno(
    caderno: Caderno,
    onClick: () -> Unit = {}
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .background(Color(0xFFE6F4F1), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = com.memobrain.memonow.R.drawable.ic_livro_caderno),
                        contentDescription = "Caderno",
                        modifier = Modifier.size(22.dp),
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = caderno.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A2536),
                    )
                    Text(
                        text = "Clique para abrir",
                        fontSize = 12.sp,
                        color = Color(0xFF9EA8B6),
                    )
                }

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF9EA8B6),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${caderno.revisados} revisados",
                    fontSize = 11.sp,
                    color = Color(0xFF6C757D),
                )
                Text(
                    text = "${caderno.restantes} restantes",
                    fontSize = 11.sp,
                    color = Color(0xFF6C757D),
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val total = (caderno.revisados + caderno.restantes).toFloat()
            val progresso = if (total > 0) caderno.revisados / total else 0f

            LinearProgressIndicator(
                progress = { progresso },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                color = Color(0xFF4FA393),
                trackColor = Color(0xFFE9ECEF),
                strokeCap = StrokeCap.Round,
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
fun ListaCadernosTelaPreview() {
    ListaCadernosTela()
}