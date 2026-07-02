package com.memobrain.memonow.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

data class HistoricoFirestore(
    val cadernoId: String,
    val cadernoTitulo: String,
    val arquivoId: String,
    val arquivoTitulo: String,
    val arquivoDescricao: String,
    val metodo: String,
)

class FonteDadosFirestoreHistorico {
    private val banco = FirebaseFirestore.getInstance()
    private val autenticacao = FirebaseAuth.getInstance()

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
        val usuarioId = autenticacao.currentUser?.uid

        if (usuarioId.isNullOrBlank()) {
            aoErro("Usuário não autenticado.")
            return
        }

        if (cadernoId.isBlank() || arquivoId.isBlank()) {
            aoErro("Não foi possível identificar a atividade.")
            return
        }

        val idHistorico = "${cadernoId}_$arquivoId"

        val dadosHistorico =
            hashMapOf<String, Any>(
                "cadernoId" to cadernoId,
                "cadernoTitulo" to cadernoTitulo,
                "arquivoId" to arquivoId,
                "arquivoTitulo" to arquivoTitulo,
                "arquivoDescricao" to arquivoDescricao,
                "metodo" to metodo,
                "ultimaAtividadeEm" to FieldValue.serverTimestamp(),
            )

        banco
            .collection("usuarios")
            .document(usuarioId)
            .collection("historico")
            .document(idHistorico)
            .set(dadosHistorico, SetOptions.merge())
            .addOnSuccessListener {
                aoSucesso()
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível registrar a atividade.",
                )
            }
    }

    fun observarHistorico(
        aoAtualizar: (List<HistoricoFirestore>) -> Unit,
        aoErro: (String) -> Unit,
    ): ListenerRegistration? {
        val usuarioId = autenticacao.currentUser?.uid

        if (usuarioId.isNullOrBlank()) {
            aoErro("Usuário não autenticado.")
            return null
        }

        return banco
            .collection("usuarios")
            .document(usuarioId)
            .collection("historico")
            .orderBy("ultimaAtividadeEm", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    aoErro(
                        exception.message
                            ?: "Não foi possível carregar o histórico.",
                    )
                    return@addSnapshotListener
                }

                val historico =
                    snapshot
                        ?.documents
                        .orEmpty()
                        .mapNotNull { documento ->
                            val cadernoId =
                                documento.getString("cadernoId").orEmpty()

                            val arquivoId =
                                documento.getString("arquivoId").orEmpty()

                            if (cadernoId.isBlank() || arquivoId.isBlank()) {
                                return@mapNotNull null
                            }

                            HistoricoFirestore(
                                cadernoId = cadernoId,
                                cadernoTitulo =
                                    documento.getString("cadernoTitulo").orEmpty(),
                                arquivoId = arquivoId,
                                arquivoTitulo =
                                    documento.getString("arquivoTitulo").orEmpty(),
                                arquivoDescricao =
                                    documento.getString("arquivoDescricao").orEmpty(),
                                metodo =
                                    documento.getString("metodo").orEmpty(),
                            )
                        }

                aoAtualizar(historico)
            }
    }
}
