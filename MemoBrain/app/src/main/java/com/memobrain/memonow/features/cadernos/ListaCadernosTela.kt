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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.memobrain.memonow.R

data class Caderno(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val revisados: Int = 0,
    val restantes: Int = 0,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCadernosTela(
    modifier: Modifier = Modifier,
    onIrParaInicio: () -> Unit = {},
    onIrParaPerfil: () -> Unit = {},
    onCadernoClick: (Caderno) -> Unit = {},
    onEditarClick: (String) -> Unit = {},
    onNovoCadernoClick: () -> Unit = {},
    viewModel: CadernosViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var tabSelecionada by remember {
        mutableIntStateOf(0)
    }

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
            MenuInferiorMemonow(
                abaSelecionada = AbaMenu.CADERNOS,
                onAbaClick = { aba ->
                    when (aba) {
                        AbaMenu.INICIO -> {
                            onIrParaInicio()
                        }

                        AbaMenu.PERFIL -> {
                            onIrParaPerfil()
                        }

                        else -> {
                            Unit
                        }
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
                quantidadeMeus = uiState.listaCadernos.size,
                onTabSelected = { tabSelecionada = it },
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF22496E),
                        )
                    }
                }

                uiState.mensagemErro != null -> {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = uiState.mensagemErro.orEmpty(),
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp,
                        )
                    }
                }

                uiState.listaCadernos.isEmpty() -> {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Nenhum caderno cadastrado.",
                            color = Color(0xFF718096),
                            fontSize = 14.sp,
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        items(
                            items = uiState.listaCadernos,
                            key = { it.id },
                        ) { caderno ->
                            CardCaderno(
                                caderno = caderno,
                                onClick = {
                                    onCadernoClick(caderno)
                                },
                                onEditarClick = {
                                    onEditarClick(caderno.id)
                                },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNovoCadernoClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
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

            Spacer(modifier = Modifier.height(24.dp))
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
                .background(
                    color = Color(0xFFF1F3F5),
                    shape = RoundedCornerShape(24.dp),
                ).padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(
            "Meus ($quantidadeMeus)",
            "Públicos",
        ).forEachIndexed { index, texto ->
            val estaSelecionada = selecionado == index

            Button(
                onClick = {
                    onTabSelected(index)
                },
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (estaSelecionada) {
                                Color.White
                            } else {
                                Color.Transparent
                            },
                        contentColor =
                            if (estaSelecionada) {
                                Color(0xFF1A2536)
                            } else {
                                Color(0xFF6C757D)
                            },
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
    onClick: () -> Unit = {},
    onEditarClick: () -> Unit = {},
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
        colors =
            CardDefaults.cardColors(
                containerColor = Color.White,
            ),
        shape = RoundedCornerShape(16.dp),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp,
            ),
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
                            .background(
                                color = Color(0xFFE6F4F1),
                                shape = RoundedCornerShape(12.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter =
                            painterResource(
                                id = R.drawable.ic_livro_caderno,
                            ),
                        contentDescription = "Caderno",
                        modifier = Modifier.size(22.dp),
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = caderno.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A2536),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text =
                            caderno.descricao.ifBlank {
                                "Sem descrição"
                            },
                        fontSize = 12.sp,
                        color = Color(0xFF9EA8B6),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                IconButton(
                    onClick = onEditarClick,
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar caderno",
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

            val total =
                (
                    caderno.revisados + caderno.restantes
                ).toFloat()

            val progresso =
                if (total > 0f) {
                    caderno.revisados / total
                } else {
                    0f
                }

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
