package com.memobrain.memonow.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

data class CadernoFirestore(
    val id: String,
    val titulo: String,
    val descricao: String,
    val cor: Long,
    val revisados: Int,
    val restantes: Int,
)

class FonteDadosFirestoreCaderno {
    private val banco = FirebaseFirestore.getInstance()
    private val autenticacao = FirebaseAuth.getInstance()

    fun criarCaderno(
        titulo: String,
        descricao: String,
        cor: Long,
        aoSucesso: (String) -> Unit,
        aoErro: (String) -> Unit,
    ) {
        val usuarioId = autenticacao.currentUser?.uid

        if (usuarioId.isNullOrBlank()) {
            aoErro("Sua sessão expirou. Faça login novamente.")
            return
        }

        val dadosCaderno =
            hashMapOf<String, Any>(
                "usuarioId" to usuarioId,
                "titulo" to titulo.trim(),
                "descricao" to descricao.trim(),
                "cor" to cor,
                "revisados" to 0,
                "restantes" to 0,
                "criadoEm" to FieldValue.serverTimestamp(),
                "atualizadoEm" to FieldValue.serverTimestamp(),
            )

        banco
            .collection("cadernos")
            .add(dadosCaderno)
            .addOnSuccessListener { documento ->
                aoSucesso(documento.id)
            }.addOnFailureListener { exception ->
                aoErro(exception.message ?: "Não foi possível salvar o caderno.")
            }
    }

    fun observarCadernosDoUsuario(
        aoAtualizar: (List<CadernoFirestore>) -> Unit,
        aoErro: (String) -> Unit,
    ): ListenerRegistration? {
        val usuarioId = autenticacao.currentUser?.uid

        if (usuarioId.isNullOrBlank()) {
            aoErro("Usuário não autenticado.")
            return null
        }

        return banco
            .collection("cadernos")
            .whereEqualTo("usuarioId", usuarioId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    aoErro(
                        exception.message
                            ?: "Não foi possível carregar os cadernos.",
                    )
                    return@addSnapshotListener
                }

                val cadernos =
                    snapshot?.documents.orEmpty().map { documento ->
                        documentoParaCaderno(documento)
                    }

                aoAtualizar(cadernos)
            }
    }

    fun buscarCaderno(
        cadernoId: String,
        aoSucesso: (CadernoFirestore) -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (!validarCaderno(cadernoId, aoErro)) return

        banco
            .collection("cadernos")
            .document(cadernoId)
            .get()
            .addOnSuccessListener { documento ->
                if (!documento.exists()) {
                    aoErro("Caderno não encontrado.")
                    return@addOnSuccessListener
                }

                aoSucesso(documentoParaCaderno(documento))
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível carregar o caderno.",
                )
            }
    }

    fun atualizarCaderno(
        cadernoId: String,
        titulo: String,
        descricao: String,
        cor: Long,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (!validarCaderno(cadernoId, aoErro)) return

        val dadosAtualizados =
            hashMapOf<String, Any>(
                "titulo" to titulo.trim(),
                "descricao" to descricao.trim(),
                "cor" to cor,
                "atualizadoEm" to FieldValue.serverTimestamp(),
            )

        banco
            .collection("cadernos")
            .document(cadernoId)
            .update(dadosAtualizados)
            .addOnSuccessListener {
                aoSucesso()
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível atualizar o caderno.",
                )
            }
    }

    fun excluirCaderno(
        cadernoId: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (!validarCaderno(cadernoId, aoErro)) return

        val referenciaCaderno =
            banco
                .collection("cadernos")
                .document(cadernoId)

        referenciaCaderno
            .collection("arquivos")
            .get()
            .addOnSuccessListener { snapshot ->
                excluirArquivosECaderno(
                    referenciaCaderno = referenciaCaderno,
                    arquivos = snapshot.documents,
                    aoSucesso = aoSucesso,
                    aoErro = aoErro,
                )
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível excluir o caderno.",
                )
            }
    }

    private fun excluirArquivosECaderno(
        referenciaCaderno: DocumentReference,
        arquivos: List<DocumentSnapshot>,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        val lotes = arquivos.chunked(450)

        fun excluirLote(indice: Int) {
            if (indice >= lotes.size) {
                referenciaCaderno
                    .delete()
                    .addOnSuccessListener {
                        aoSucesso()
                    }.addOnFailureListener { exception ->
                        aoErro(
                            exception.message
                                ?: "Não foi possível excluir o caderno.",
                        )
                    }

                return
            }

            val lote = banco.batch()

            lotes[indice].forEach { documento ->
                lote.delete(documento.reference)
            }

            lote
                .commit()
                .addOnSuccessListener {
                    excluirLote(indice + 1)
                }.addOnFailureListener { exception ->
                    aoErro(
                        exception.message
                            ?: "Não foi possível excluir os arquivos.",
                    )
                }
        }

        excluirLote(0)
    }

    private fun validarCaderno(
        cadernoId: String,
        aoErro: (String) -> Unit,
    ): Boolean {
        if (autenticacao.currentUser?.uid.isNullOrBlank()) {
            aoErro("Sua sessão expirou. Faça login novamente.")
            return false
        }

        if (cadernoId.isBlank()) {
            aoErro("Não foi possível identificar o caderno.")
            return false
        }

        return true
    }

    private fun documentoParaCaderno(documento: DocumentSnapshot): CadernoFirestore =
        CadernoFirestore(
            id = documento.id,
            titulo = documento.getString("titulo").orEmpty(),
            descricao = documento.getString("descricao").orEmpty(),
            cor = documento.getLong("cor") ?: 0xFF264653L,
            revisados = documento.getLong("revisados")?.toInt() ?: 0,
            restantes = documento.getLong("restantes")?.toInt() ?: 0,
        )
}
