package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditNotebookScreen(
    viewModel: EditNotebookViewModel,
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
            Text("Editar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Fechar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nome
        Text("Nome do Caderno", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.name,
            onValueChange = { viewModel.onEvent(EditNotebookEvent.OnNameChanged(it)) },
            placeholder = { Text("Ex: Ciência de Dados") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descrição
        Text("Descrição", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Text("(opcional)", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.description,
            onValueChange = { viewModel.onEvent(EditNotebookEvent.OnDescriptionChanged(it)) },
            placeholder = { Text("Digite a descrição") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Imagem
        Text("Imagem do Caderno", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Text("(opcional)", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .clickable { viewModel.onEvent(EditNotebookEvent.OnImageClicked) },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Selecionar imagem", color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Imagem",
                    tint = Color.White,
                    modifier = Modifier.background(Color(0xFF1E3A5F), CircleShape).padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cores
        Text("Cor", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Text("(opcional)", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.availableColors) { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (state.selectedColor == color) 3.dp else 0.dp,
                            color = if (state.selectedColor == color) Color.Black else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { viewModel.onEvent(EditNotebookEvent.OnColorSelected(color)) }
                )
            }
        }

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
                    viewModel.onEvent(EditNotebookEvent.OnSaveClicked)
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
