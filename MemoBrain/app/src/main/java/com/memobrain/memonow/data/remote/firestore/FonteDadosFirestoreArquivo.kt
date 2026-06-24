package com.memobrain.memonow.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

data class ArquivoFirestore(
    val id: String,
    val titulo: String,
    val descricao: String,
    val metodo: String,
    val quantidadeItens: Int,
)

class FonteDadosFirestoreArquivo {
    private val banco = FirebaseFirestore.getInstance()
    private val autenticacao = FirebaseAuth.getInstance()

    fun criarArquivo(
        cadernoId: String,
        titulo: String,
        descricao: String,
        metodo: String,
        aoSucesso: (String) -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (!validarCaderno(cadernoId, aoErro)) return

        val usuarioId = autenticacao.currentUser?.uid ?: return

        val dadosArquivo =
            hashMapOf<String, Any>(
                "usuarioId" to usuarioId,
                "cadernoId" to cadernoId,
                "titulo" to titulo.trim(),
                "descricao" to descricao.trim(),
                "metodo" to metodo,
                "quantidadeItens" to 0,
                "criadoEm" to FieldValue.serverTimestamp(),
                "atualizadoEm" to FieldValue.serverTimestamp(),
            )

        banco
            .collection("cadernos")
            .document(cadernoId)
            .collection("arquivos")
            .add(dadosArquivo)
            .addOnSuccessListener { documento ->
                aoSucesso(documento.id)
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível salvar o arquivo.",
                )
            }
    }

    fun observarArquivosDoCaderno(
        cadernoId: String,
        aoAtualizar: (List<ArquivoFirestore>) -> Unit,
        aoErro: (String) -> Unit,
    ): ListenerRegistration? {
        if (!validarCaderno(cadernoId, aoErro)) return null

        return banco
            .collection("cadernos")
            .document(cadernoId)
            .collection("arquivos")
            .orderBy("criadoEm", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    aoErro(
                        exception.message
                            ?: "Não foi possível carregar os arquivos.",
                    )
                    return@addSnapshotListener
                }

                val arquivos =
                    snapshot?.documents.orEmpty().map { documento ->
                        documentoParaArquivo(documento)
                    }

                aoAtualizar(arquivos)
            }
    }

    fun buscarArquivo(
        cadernoId: String,
        arquivoId: String,
        aoSucesso: (ArquivoFirestore) -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (
            !validarCaderno(cadernoId, aoErro) ||
            !validarArquivo(arquivoId, aoErro)
        ) {
            return
        }

        banco
            .collection("cadernos")
            .document(cadernoId)
            .collection("arquivos")
            .document(arquivoId)
            .get()
            .addOnSuccessListener { documento ->
                if (!documento.exists()) {
                    aoErro("Arquivo não encontrado.")
                    return@addOnSuccessListener
                }

                aoSucesso(documentoParaArquivo(documento))
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível carregar o arquivo.",
                )
            }
    }

    fun atualizarArquivo(
        cadernoId: String,
        arquivoId: String,
        titulo: String,
        descricao: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (
            !validarCaderno(cadernoId, aoErro) ||
            !validarArquivo(arquivoId, aoErro)
        ) {
            return
        }

        val dadosAtualizados =
            hashMapOf<String, Any>(
                "titulo" to titulo.trim(),
                "descricao" to descricao.trim(),
                "atualizadoEm" to FieldValue.serverTimestamp(),
            )

        banco
            .collection("cadernos")
            .document(cadernoId)
            .collection("arquivos")
            .document(arquivoId)
            .update(dadosAtualizados)
            .addOnSuccessListener {
                aoSucesso()
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível atualizar o arquivo.",
                )
            }
    }

    fun excluirArquivo(
        cadernoId: String,
        arquivoId: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (
            !validarCaderno(cadernoId, aoErro) ||
            !validarArquivo(arquivoId, aoErro)
        ) {
            return
        }

        banco
            .collection("cadernos")
            .document(cadernoId)
            .collection("arquivos")
            .document(arquivoId)
            .delete()
            .addOnSuccessListener {
                aoSucesso()
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível excluir o arquivo.",
                )
            }
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

    private fun validarArquivo(
        arquivoId: String,
        aoErro: (String) -> Unit,
    ): Boolean {
        if (arquivoId.isBlank()) {
            aoErro("Não foi possível identificar o arquivo.")
            return false
        }

        return true
    }

    private fun documentoParaArquivo(documento: DocumentSnapshot): ArquivoFirestore =
        ArquivoFirestore(
            id = documento.id,
            titulo = documento.getString("titulo").orEmpty(),
            descricao = documento.getString("descricao").orEmpty(),
            metodo = documento.getString("metodo").orEmpty(),
            quantidadeItens =
                documento.getLong("quantidadeItens")?.toInt() ?: 0,
        )
}
