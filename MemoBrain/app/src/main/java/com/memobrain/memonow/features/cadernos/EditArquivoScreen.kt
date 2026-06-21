package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditArquivoScreen(
    viewModel: EditArquivoViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabeçalho
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text("Editar Arquivo", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Fechar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Título do Arquivo
        Text("Título do Arquivo", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.titulo,
            onValueChange = { viewModel.onEvent(EditArquivoEvent.OnTituloChanged(it)) },
            placeholder = { Text("Ex: Limpeza e Pré-processamento") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Código/Referência do PDF
        Text("Código/Referência do PDF", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.codigoPdf,
            onValueChange = { viewModel.onEvent(EditArquivoEvent.OnCodigoPdfChanged(it)) },
            placeholder = { Text("Ex: PDF 00") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Quantidade de Questões
        Text("Quantidade de Questões", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = if (state.qtdQuestoes == 0) "" else state.qtdQuestoes.toString(),
            onValueChange = { 
                val qtd = it.toIntOrNull() ?: 0
                viewModel.onEvent(EditArquivoEvent.OnQtdQuestoesChanged(qtd)) 
            },
            placeholder = { Text("Ex: 14") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botões
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                shape = RoundedCornerShape(50)
            ) {
                Text("CANCELAR", color = Color.White)
            }

            Button(
                onClick = { 
                    viewModel.onEvent(EditArquivoEvent.OnSaveClicked)
                    onNavigateBack()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F)),
                shape = RoundedCornerShape(50)
            ) {
                Text("SALVAR", color = Color.White)
            }
        }
    }
}
