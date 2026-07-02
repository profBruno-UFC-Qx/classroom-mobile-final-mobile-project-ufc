package com.memobrain.memonow.data.repository.repositorio

import android.net.Uri
import com.google.firebase.firestore.ListenerRegistration
import com.memobrain.memonow.data.remote.firestore.FonteDadosFirestoreCaderno
import com.memobrain.memonow.features.cadernos.Caderno

data class CadernoParaEdicao(
    val id: String,
    val titulo: String,
    val descricao: String,
    val cor: Long,
)

class RepositorioCaderno(
    private val fonteDadosFirestore: FonteDadosFirestoreCaderno =
        FonteDadosFirestoreCaderno(),
) {
    fun criarCaderno(
        titulo: String,
        descricao: String,
        cor: Long,
        imagemUri: Uri?,
        aoSucesso: (String) -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.criarCaderno(
            titulo = titulo,
            descricao = descricao,
            cor = cor,
            imagemUri = imagemUri,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }

    fun observarCadernosDoUsuario(
        aoAtualizar: (List<Caderno>) -> Unit,
        aoErro: (String) -> Unit,
    ): ListenerRegistration? =
        fonteDadosFirestore.observarCadernosDoUsuario(
            aoAtualizar = { cadernosFirestore ->
                val cadernos =
                    cadernosFirestore.map { caderno ->
                        Caderno(
                            id = caderno.id,
                            titulo = caderno.titulo,
                            descricao = caderno.descricao,
                            revisados = caderno.revisados,
                            restantes = caderno.restantes,
                            capaUrl = caderno.capaUrl // <--- ADICIONE ESTA LINHA PARA A FOTO CHEGAR À HOME
                        )
                    }

                aoAtualizar(cadernos)
            },
            aoErro = aoErro,
        )

    fun buscarCaderno(
        cadernoId: String,
        aoSucesso: (CadernoParaEdicao) -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.buscarCaderno(
            cadernoId = cadernoId,
            aoSucesso = { caderno ->
                aoSucesso(
                    CadernoParaEdicao(
                        id = caderno.id,
                        titulo = caderno.titulo,
                        descricao = caderno.descricao,
                        cor = caderno.cor,
                    ),
                )
            },
            aoErro = aoErro,
        )
    }

    fun atualizarCaderno(
        cadernoId: String,
        titulo: String,
        descricao: String,
        cor: Long,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.atualizarCaderno(
            cadernoId = cadernoId,
            titulo = titulo,
            descricao = descricao,
            cor = cor,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }

    fun excluirCaderno(
        cadernoId: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.excluirCaderno(
            cadernoId = cadernoId,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }
}