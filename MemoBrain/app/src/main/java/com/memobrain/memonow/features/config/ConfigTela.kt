package com.memobrain.memonow.features.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Importado para a seta de voltar correta
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memobrain.memonow.R
import com.memobrain.memonow.features.cadernos.MenuInferiorMemonow
import com.memobrain.memonow.features.cadernos.AbaMenu

@Composable
fun ConfigTela(
    onIrParaInicio: () -> Unit = {},
    onIrParaCadernos: () -> Unit = {},
) {
    Scaffold(
        containerColor = Color(0xFFF8F9FA),

                bottomBar = {
            MenuInferiorMemonow(
                abaSelecionada = AbaMenu.PERFIL, // Indica que a aba Perfil está ativa nesta tela
                onAbaClick = { aba ->
                    when (aba) {
                        AbaMenu.INICIO -> onIrParaInicio()
                        AbaMenu.CADERNOS -> onIrParaCadernos()
                        else -> {} // Já está no Perfil, não faz nada
                    }
                }
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {


            Text(
                text = "Minha Conta",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.foto_cerebro),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Allyson Novaes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Conta criada em Junho-2026",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Conta Free",
                        fontSize = 14.sp,
                        color = Color(0xFF70A19F),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = "Configurações",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column {
                    ConfigItem("Configurações da conta")
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ConfigItem("Configurar Notificações")
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ConfigItem("Planos")
                }
            }

            Text(
                text = "Outros",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Column {
                    ConfigItem("Privacidade e Confidencialidade")
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ConfigItem("Sobre o app")
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ConfigItem("Ajuda")
                }
            }

            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Deletar minha conta")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {},
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC75A43)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Sair", color = Color.White)
            }
        }
    }
}

@Composable
fun ConfigItem(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 15.sp, color = Color.Gray)
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(18.dp)
        )
    }
}

// 3. ATUALIZADO: Ajustado o Preview para passar o onVoltar vazio
@Preview(showBackground = true)
@Composable
fun PreviewConfigTela() {}