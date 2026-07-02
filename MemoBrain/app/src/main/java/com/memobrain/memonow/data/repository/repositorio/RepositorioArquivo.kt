package com.memobrain.memonow.data.repository.repositorio

import com.google.firebase.firestore.ListenerRegistration
import com.memobrain.memonow.data.remote.firestore.FonteDadosFirestoreArquivo

data class ArquivoCaderno(
    val id: String,
    val titulo: String,
    val descricao: String,
    val metodo: String,
    val quantidadeItens: Int,
)

class RepositorioArquivo(
    private val fonteDadosFirestore: FonteDadosFirestoreArquivo =
        FonteDadosFirestoreArquivo(),
) {
    fun criarArquivo(
        cadernoId: String,
        titulo: String,
        descricao: String,
        metodo: String,
        aoSucesso: (String) -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.criarArquivo(
            cadernoId = cadernoId,
            titulo = titulo,
            descricao = descricao,
            metodo = metodo,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }

    fun observarArquivosDoCaderno(
        cadernoId: String,
        aoAtualizar: (List<ArquivoCaderno>) -> Unit,
        aoErro: (String) -> Unit,
    ): ListenerRegistration? =
        fonteDadosFirestore.observarArquivosDoCaderno(
            cadernoId = cadernoId,
            aoAtualizar = { arquivosFirestore ->
                val arquivos =
                    arquivosFirestore.map { arquivo ->
                        ArquivoCaderno(
                            id = arquivo.id,
                            titulo = arquivo.titulo,
                            descricao = arquivo.descricao,
                            metodo = arquivo.metodo,
                            quantidadeItens = arquivo.quantidadeItens,
                        )
                    }

                aoAtualizar(arquivos)
            },
            aoErro = aoErro,
        )

    fun buscarArquivo(
        cadernoId: String,
        arquivoId: String,
        aoSucesso: (ArquivoCaderno) -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.buscarArquivo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            aoSucesso = { arquivo ->
                aoSucesso(
                    ArquivoCaderno(
                        id = arquivo.id,
                        titulo = arquivo.titulo,
                        descricao = arquivo.descricao,
                        metodo = arquivo.metodo,
                        quantidadeItens = arquivo.quantidadeItens,
                    ),
                )
            },
            aoErro = aoErro,
        )
    }

    fun atualizarArquivo(
        cadernoId: String,
        arquivoId: String,
        titulo: String,
        descricao: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.atualizarArquivo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            titulo = titulo,
            descricao = descricao,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }

    fun excluirArquivo(
        cadernoId: String,
        arquivoId: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.excluirArquivo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }
}
