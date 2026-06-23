package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memobrain.memonow.R

data class MetodoEstudo(
    val titulo: String,
    val iconeRes: Int,
)

data class AtividadeRecente(
    val titulo: String,
    val subtitulo: String,
)

data class CadernoAndamento(
    val titulo: String,
)

enum class AbaMenu {
    INICIO,
    CADERNOS,
    PROGRESSO,
    PERFIL,
}

@Composable
fun DashboardCadernosTela(
    modifier: Modifier = Modifier,
    onIrParaCadernos: () -> Unit = {},
    onIrParaPerfil: () -> Unit
) {
    val chipsFiltros = listOf("Todos", "Revisar Hoje", "Em andamento", "Concluídos")
    var chipSelecionado by remember { mutableStateOf("Todos") }

    val metodosEstudo =
        listOf(
            MetodoEstudo("Arrastar e Soltar", R.drawable.ic_arrastar_e_soltar),
            MetodoEstudo("Resposta Aberta", R.drawable.ic_resposta_aberta),
            MetodoEstudo("Oclusão de Imagem", R.drawable.ic_oclusao_imagem),
        )

    val cadernosEmAndamento =
        listOf(
            CadernoAndamento("Direito Administrativo"),
            CadernoAndamento("Ciência de Dados"),
            CadernoAndamento("Direito Processual Penal"),
        )

    val atividadesRecentes =
        listOf(
            AtividadeRecente("Direito Administrativo", "Atos Administrativos"),
            AtividadeRecente("Português", "Morfologia"),
            AtividadeRecente("Ciência de Dados", "Mineração de Dados e Machine Learning"),
        )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            MenuInferiorMemonow(
                abaSelecionada = AbaMenu.INICIO,
                onAbaClick = { aba ->
                    when (aba) {
                        AbaMenu.CADERNOS -> {
                            onIrParaCadernos()
                        }
                        AbaMenu.PERFIL -> {
                            onIrParaPerfil()
                        }
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
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 10.dp),
        ) {
            HeaderUsuario(nome = "Allyson Novaes!")

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                chipsFiltros.forEach { filtro ->
                    FilterChipMemonow(
                        texto = filtro,
                        isSelected = filtro == chipSelecionado,
                        onClick = { chipSelecionado = filtro },
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Métodos de estudo disponíveis",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A2536),
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                metodosEstudo.forEach { metodo ->
                    CardMetodoEstudo(
                        metodo = metodo,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionHeader(
                titulo = "Cadernos em andamento",
                onVerMaisClick = {},
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                cadernosEmAndamento.forEach { caderno ->
                    CardCadernoAndamento(caderno = caderno)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionHeader(
                titulo = "Atividades recentes",
                onVerMaisClick = {},
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                atividadesRecentes.forEach { atividade ->
                    CardAtividadeRecente(atividade = atividade)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun HeaderUsuario(nome: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.foto_cerebro),
            contentDescription = "Foto do usuário",
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Olá, $nome!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A2536),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Vamos revisar hoje?",
                fontSize = 12.sp,
                color = Color(0xFF7B8794),
            )
        }
    }
}

@Composable
fun FilterChipMemonow(
    texto: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = if (isSelected) Color(0xFF22496E) else Color(0xFFF0F3F7),
        shadowElevation = 0.dp,
    ) {
        Text(
            text = texto,
            color = if (isSelected) Color.White else Color(0xFF5F6B7A),
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

@Composable
fun CardMetodoEstudo(
    metodo: MetodoEstudo,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.height(88.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = metodo.iconeRes),
                contentDescription = metodo.titulo,
                modifier = Modifier.size(22.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = metodo.titulo,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A2536),
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                maxLines = 2,
            )
        }
    }
}

@Composable
fun SectionHeader(
    titulo: String,
    onVerMaisClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = titulo,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A2536),
        )
        Text(
            text = "Ver mais",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4EB6A6),
            modifier = Modifier.clickable { onVerMaisClick() },
        )
    }
}

@Composable
fun CardCadernoAndamento(caderno: CadernoAndamento) {
    Card(
        modifier =
            Modifier
                .width(128.dp)
                .height(118.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_memobrain_logo),
                    contentDescription = caderno.titulo,
                    modifier = Modifier.size(72.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            Text(
                text = caderno.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A2536),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun CardAtividadeRecente(atividade: AtividadeRecente) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(60.dp)
                        .background(Color(0xFFE9F6F2), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_livro_caderno),
                    contentDescription = "Atividade",
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = atividade.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A2536),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = atividade.subtitulo,
                    fontSize = 13.sp,
                    color = Color(0xFF8A94A6),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = "⋮",
                fontSize = 22.sp,
                color = Color(0xFF9EA8B6),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
fun MenuInferiorMemonow(
    abaSelecionada: AbaMenu,
    onAbaClick: (AbaMenu) -> Unit = {},
) {
    val azulPrincipal = Color(0xFF22496E)
    val cinza = Color(0xFF8E98A8)
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val itens =
        listOf(
            Triple(AbaMenu.INICIO, "Início", Pair(R.drawable.ic_inicio, R.drawable.ic_inicio_azul)),
            Triple(AbaMenu.CADERNOS, "Cadernos", Pair(R.drawable.ic_cadernos, R.drawable.ic_cadernos_azul)),
            Triple(AbaMenu.PROGRESSO, "Progresso", Pair(R.drawable.ic_desempenho, R.drawable.ic_desempenho_azul)),
            Triple(AbaMenu.PERFIL, "Perfil", Pair(R.drawable.ic_usuario, R.drawable.ic_usuario_azul)),
        )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Color.White),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                        .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
            ) {
                itens.forEach { (aba, titulo, icones) ->
                    val selecionado = aba == abaSelecionada
                    val iconeAtual = if (selecionado) icones.second else icones.first

                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(82.dp)
                                .clickable { onAbaClick(aba) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .padding(top = 0.dp)
                                    .width(34.dp)
                                    .height(3.dp)
                                    .background(
                                        color = if (selecionado) azulPrincipal else Color.Transparent,
                                        shape = RoundedCornerShape(50),
                                    ),
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Image(
                            painter = painterResource(id = iconeAtual),
                            contentDescription = titulo,
                            modifier = Modifier.size(20.dp),
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = titulo,
                            fontSize = 11.sp,
                            fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selecionado) azulPrincipal else cinza,
                        )
                    }
                }
            }

            if (bottomInset > 0.dp) {
                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(bottomInset)
                            .background(Color.White),
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun DashboardCadernosTelaPreview() {
    DashboardCadernosTela(
        onIrParaCadernos = {},
        onIrParaPerfil = {}
    )
}
