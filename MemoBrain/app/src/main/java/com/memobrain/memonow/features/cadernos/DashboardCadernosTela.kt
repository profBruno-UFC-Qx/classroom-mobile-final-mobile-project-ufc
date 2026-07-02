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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.memobrain.memonow.R
import androidx.compose.material3.MaterialTheme

data class MetodoEstudo(
    val titulo: String,
    val iconeRes: Int,
)

data class AtividadeRecente(
    val idArquivo: String = "",
    val titulo: String = "",
    val subtitulo: String = "",
    val metodo: String = "",
)

data class CadernoAndamento(
    val id: String = "",
    val titulo: String = "",
    val capaUrl: String = ""
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
    onIrParaPerfil: () -> Unit = {},
    onMetodoClick: (String) -> Unit = {},
) {
    val usuarioId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    val viewModel: HomeViewModel = viewModel()

    LaunchedEffect(usuarioId) {
        viewModel.carregarDadosDoUsuario(usuarioId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            MenuInferiorMemonow(
                abaSelecionada = AbaMenu.INICIO,
                onAbaClick = { aba ->
                    when (aba) {
                        AbaMenu.CADERNOS -> onIrParaCadernos()
                        AbaMenu.PERFIL -> onIrParaPerfil()
                        else -> Unit
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 18.dp,
                    vertical = 10.dp,
                ),
        ) {
            HeaderUsuario(nome = uiState.nomeUsuario)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                uiState.chipsFiltros.forEach { filtro ->
                    FilterChipMemonow(
                        texto = filtro,
                        isSelected = filtro == uiState.chipSelecionado,
                        onClick = {
                            viewModel.selecionarFiltro(filtro)
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Métodos de estudo disponíveis",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                uiState.metodosEstudo.forEach { metodo ->
                    CardMetodoEstudo(
                        metodo = metodo,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onMetodoClick(metodo.titulo)
                            },
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionHeader(
                titulo = "Cadernos em andamento",
                onVerMaisClick = onIrParaCadernos,
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.cadernosEmAndamento.isEmpty()) {
                Text(
                    text = "Nenhum caderno cadastrado ainda.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    uiState.cadernosEmAndamento
                        .take(5)
                        .forEach { caderno ->
                            CardCadernoAndamento(
                                caderno = caderno,
                            )
                        }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionHeader(
                titulo = "Atividades recentes",
                onVerMaisClick = {},
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.atividadesRecentes.isEmpty()) {
                Text(
                    text = "Nenhuma atividade realizada ainda.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    uiState.atividadesRecentes
                        .take(5)
                        .forEach { atividade ->
                            CardAtividadeRecente(
                                atividade = atividade,
                            )
                        }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun HeaderUsuario(nome: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.foto_cerebro),
            contentDescription = "Foto do usuário",
            modifier = Modifier
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
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Vamos revisar hoje?",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        modifier = Modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(22.dp),

        color = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        shadowElevation = 0.dp,
    ) {
        Text(
            text = texto,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontSize = 13.sp,
            fontWeight = if (isSelected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Medium
            },
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 10.dp,
            ),
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 8.dp,
                    vertical = 10.dp,
                ),
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
                color = MaterialTheme.colorScheme.onSurface,
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
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = "Ver mais",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                onVerMaisClick()
            },
        )
    }
}

@Composable
fun CardCadernoAndamento(
    caderno: CadernoAndamento,
) {
    Card(
        modifier = Modifier
            .width(128.dp)
            .height(142.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 10.dp,
                    vertical = 10.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                // Se houver uma capaUrl válida, carrega via Coil, senão usa o logo padrão
                val imagemPainter = if (caderno.capaUrl.isNotBlank()) {
                    coil.compose.rememberAsyncImagePainter(model = caderno.capaUrl)
                } else {
                    painterResource(id = R.drawable.ic_memobrain_logo)
                }

                Image(
                    painter = imagemPainter,
                    contentDescription = "Capa do caderno",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = if (caderno.capaUrl.isNotBlank()) ContentScale.Crop else ContentScale.Fit,
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = limitarTituloCaderno(caderno.titulo),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
            )
        }
    }
}

@Composable
fun CardAtividadeRecente(
    atividade: AtividadeRecente,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.ic_livro_caderno,
                    ),
                    contentDescription = "Atividade",
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = atividade.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = atividade.subtitulo,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = "⋮",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    val azulPrincipal = MaterialTheme.colorScheme.primary
    val cinza = MaterialTheme.colorScheme.onSurfaceVariant

    val bottomInset = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    val itens = listOf(
        Triple(
            AbaMenu.INICIO,
            "Início",
            Pair(
                R.drawable.ic_inicio,
                R.drawable.ic_inicio_azul,
            ),
        ),
        Triple(
            AbaMenu.CADERNOS,
            "Cadernos",
            Pair(
                R.drawable.ic_cadernos,
                R.drawable.ic_cadernos_azul,
            ),
        ),
        Triple(
            AbaMenu.PROGRESSO,
            "Progresso",
            Pair(
                R.drawable.ic_desempenho,
                R.drawable.ic_desempenho_azul,
            ),
        ),
        Triple(
            AbaMenu.PERFIL,
            "Perfil",
            Pair(
                R.drawable.ic_usuario,
                R.drawable.ic_usuario_azul,
            ),
        ),
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
        ),
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
            ) {
                itens.forEach { (aba, titulo, icones) ->
                    val selecionado = aba == abaSelecionada

                    val iconeAtual = if (selecionado) {
                        icones.second
                    } else {
                        icones.first
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp)
                            .clickable {
                                onAbaClick(aba)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(34.dp)
                                .height(3.dp)
                                .background(
                                    color = if (selecionado) {
                                        azulPrincipal
                                    } else {
                                        Color.Transparent
                                    },
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
                            fontWeight = if (selecionado) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                            color = if (selecionado) {
                                azulPrincipal
                            } else {
                                cinza
                            },
                        )
                    }
                }
            }

            if (bottomInset > 0.dp) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bottomInset)
                        .background(MaterialTheme.colorScheme.surface),
                )
            }
        }
    }
}

private fun limitarTituloCaderno(
    titulo: String,
    limite: Int = 18,
): String {
    val tituloLimpo = titulo
        .trim()
        .replace(Regex("\\s+"), " ")

    return if (tituloLimpo.length <= limite) {
        tituloLimpo
    } else {
        tituloLimpo
            .take(limite - 1)
            .trimEnd() + "…"
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun DashboardCadernosTelaPreview() {
    DashboardCadernosTela()
}

