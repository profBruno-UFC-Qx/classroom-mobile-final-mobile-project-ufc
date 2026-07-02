package com.memobrain.memonow.data.repository.repositorio

import com.google.firebase.firestore.ListenerRegistration
import com.memobrain.memonow.data.remote.firestore.ConteudoFirestore
import com.memobrain.memonow.data.remote.firestore.FonteDadosFirestoreConteudo

data class ConteudoEstudo(
    val id: String,
    val tipo: String,
    val pergunta: String,
    val resposta: String,
    val alternativas: List<String>,
    val indiceCorreto: Int,
)

class RepositorioConteudo(
    private val fonteDadosFirestore: FonteDadosFirestoreConteudo =
        FonteDadosFirestoreConteudo(),
) {
    fun criarFlashcard(
        cadernoId: String,
        arquivoId: String,
        pergunta: String,
        resposta: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.criarFlashcard(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            pergunta = pergunta,
            resposta = resposta,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }

    fun criarMultiplaEscolha(
        cadernoId: String,
        arquivoId: String,
        pergunta: String,
        alternativas: List<String>,
        indiceCorreto: Int,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.criarMultiplaEscolha(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            pergunta = pergunta,
            alternativas = alternativas,
            indiceCorreto = indiceCorreto,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }

    fun observarConteudosDoArquivo(
        cadernoId: String,
        arquivoId: String,
        aoAtualizar: (List<ConteudoEstudo>) -> Unit,
        aoErro: (String) -> Unit,
    ): ListenerRegistration? =
        fonteDadosFirestore.observarConteudosDoArquivo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            aoAtualizar = { conteudosFirestore ->
                aoAtualizar(
                    conteudosFirestore.map { conteudo ->
                        conteudoParaEstudo(conteudo)
                    },
                )
            },
            aoErro = aoErro,
        )

    fun concluirRevisao(
        cadernoId: String,
        arquivoId: String,
        conteudoIds: List<String>,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.concluirRevisao(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            conteudoIds = conteudoIds,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }

    fun excluirConteudo(
        cadernoId: String,
        arquivoId: String,
        conteudoId: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        fonteDadosFirestore.excluirConteudo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            conteudoId = conteudoId,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }

    private fun conteudoParaEstudo(conteudo: ConteudoFirestore): ConteudoEstudo =
        ConteudoEstudo(
            id = conteudo.id,
            tipo = conteudo.tipo,
            pergunta = conteudo.pergunta,
            resposta = conteudo.resposta,
            alternativas = conteudo.alternativas,
            indiceCorreto = conteudo.indiceCorreto,
        )
}
