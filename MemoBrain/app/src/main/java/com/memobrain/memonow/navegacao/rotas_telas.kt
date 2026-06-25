package com.memobrain.memonow.navegacao

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RotaTela : NavKey {
    @Serializable
    data object Inicial : RotaTela

    @Serializable
    data object Login : RotaTela

    @Serializable
    data object Registrar : RotaTela

    @Serializable
    data object InicioApp : RotaTela

    @Serializable
    data object Cadernos : RotaTela

    @Serializable
    data object Perfil : RotaTela

    @Serializable
    data class DetalheCaderno(
        val cadernoId: String,
        val nomeCaderno: String,
    ) : RotaTela

    @Serializable
    data class EditarCaderno(
        val cadernoId: String,
    ) : RotaTela

    @Serializable
    data object CriarCaderno : RotaTela

    @Serializable
    data class CriarArquivo(
        val cadernoId: String,
    ) : RotaTela

    @Serializable
    data class EditarArquivo(
        val cadernoId: String,
        val arquivoId: String,
    ) : RotaTela

    @Serializable
    data class CriarPerguntaAberta(
        val cadernoId: String,
        val arquivoId: String,
    ) : RotaTela

    @Serializable
    data class CriarMultiplaEscolha(
        val cadernoId: String,
        val arquivoId: String,
    ) : RotaTela

    @Serializable
    data class RevisarArquivo(
        val cadernoId: String,
        val cadernoTitulo: String,
        val arquivoId: String,
        val tituloArquivo: String,
        val descricaoArquivo: String,
    ) : RotaTela

    @Serializable
    data class ResumoFlashcard(
        val acertos: Int,
        val totalQuestoes: Int,
        val tempoMillis: Long,
    ) : RotaTela
}
