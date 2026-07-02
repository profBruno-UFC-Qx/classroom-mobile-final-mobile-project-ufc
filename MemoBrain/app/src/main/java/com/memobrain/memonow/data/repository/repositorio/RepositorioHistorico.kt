package com.memobrain.memonow.data.repository.repositorio

import com.google.firebase.firestore.ListenerRegistration
import com.memobrain.memonow.data.remote.firestore.FonteDadosFirestoreHistorico

data class HistoricoEstudo(
    val cadernoId: String,
    val cadernoTitulo: String,
    val arquivoId: String,
    val arquivoTitulo: String,
    val arquivoDescricao: String,
    val metodo: String,
)

class RepositorioHistorico(
    private val fonteDadosFirestore: FonteDadosFirestoreHistorico =
        FonteDadosFirestoreHistorico(),
) {
    fun registrarAtividade(
        cadernoId: String,
        cadernoTitulo: String,
        arquivoId: String,
        arquivoTitulo: String,
        arquivoDescricao: String,
        metodo: String,
        aoSucesso: () -> Unit = {},
        aoErro: (String) -> Unit = {},
    ) {
        fonteDadosFirestore.registrarAtividade(
            cadernoId = cadernoId,
            cadernoTitulo = cadernoTitulo,
            arquivoId = arquivoId,
            arquivoTitulo = arquivoTitulo,
            arquivoDescricao = arquivoDescricao,
            metodo = metodo,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }

    fun observarHistorico(
        aoAtualizar: (List<HistoricoEstudo>) -> Unit,
        aoErro: (String) -> Unit,
    ): ListenerRegistration? =
        fonteDadosFirestore.observarHistorico(
            aoAtualizar = { historicosFirestore ->
                val historicos =
                    historicosFirestore.map { historico ->
                        HistoricoEstudo(
                            cadernoId = historico.cadernoId,
                            cadernoTitulo = historico.cadernoTitulo,
                            arquivoId = historico.arquivoId,
                            arquivoTitulo = historico.arquivoTitulo,
                            arquivoDescricao = historico.arquivoDescricao,
                            metodo = historico.metodo,
                        )
                    }

                aoAtualizar(historicos)
            },
            aoErro = aoErro,
        )
}
