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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.memobrain.memonow.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheCadernoScreen(
    cadernoId: String,
    nomeCaderno: String,
    modifier: Modifier = Modifier,
    viewModel: DetalheCadernoViewModel = viewModel(),
    onBackClick: () -> Unit,
    onNovoArquivoClick: () -> Unit,
    onTopicoClick: (TopicoExercicio) -> Unit,
    onEditarArquivoClick: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(cadernoId, nomeCaderno) {
        viewModel.carregarArquivos(
            cadernoId = cadernoId,
            nomeCaderno = nomeCaderno,
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Arquivos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D3748),
                    )
                },
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFF7FAFC),
                    ),
            )
        },
        bottomBar = {
            MenuInferiorMemonow(
                abaSelecionada = AbaMenu.CADERNOS,
                onAbaClick = { aba ->
                    if (aba == AbaMenu.INICIO) {
                        onBackClick()
                    }
                },
            )
        },
        containerColor = Color(0xFFF7FAFC),
    ) { innerPadding ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
        ) {
            SelectorAbasSimulado(
                quantidadeMeus = state.listaTopicos.size,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color(0xFF2D3748),
                    )
                }

                Text(
                    text = state.nomeCaderno.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 56.dp),
                )

                IconButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = Color(0xFF2D3748),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF1E466B),
                        )
                    }
                }

                state.mensagemErro != null -> {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = state.mensagemErro.orEmpty(),
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp,
                        )
                    }
                }

                state.listaTopicos.isEmpty() -> {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Nenhum arquivo cadastrado neste caderno.",
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
                            items = state.listaTopicos,
                            key = { it.id },
                        ) { topico ->
                            CardTopicoItem(
                                topico = topico,
                                onClick = {
                                    onTopicoClick(topico)
                                },
                                onEditarClick = {
                                    onEditarArquivoClick(topico.id)
                                },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNovoArquivoClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E466B),
                    ),
                shape = RoundedCornerShape(24.dp),
            ) {
                Text(
                    text = "NOVO ARQUIVO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CardTopicoItem(
    topico: TopicoExercicio,
    onClick: () -> Unit,
    onEditarClick: () -> Unit = {},
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color.White,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .background(
                            color = Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(8.dp),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter =
                        painterResource(
                            id = R.drawable.ic_cadernos_azul,
                        ),
                    contentDescription = "Arquivo",
                    modifier = Modifier.size(26.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = topico.titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3748),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = topico.descricao.ifBlank { "Sem descrição" },
                    fontSize = 11.sp,
                    color = Color(0xFF718096),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            IconButton(
                onClick = onEditarClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar arquivo",
                    tint = Color(0xFF9EA8B6),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun SelectorAbasSimulado(quantidadeMeus: Int) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    color = Color(0xFFEDF2F7),
                    shape = RoundedCornerShape(24.dp),
                ).padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Meus ($quantidadeMeus)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748),
            )
        }

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Públicos",
                fontSize = 13.sp,
                color = Color(0xFFA0AEC0),
            )
        }
    }
}
