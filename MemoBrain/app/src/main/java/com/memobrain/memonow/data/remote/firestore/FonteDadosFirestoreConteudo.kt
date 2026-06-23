package com.memobrain.memonow.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

object TipoConteudo {
    const val FLASHCARD = "FLASHCARD"
    const val MULTIPLA_ESCOLHA = "MULTIPLA_ESCOLHA"
}

data class ConteudoFirestore(
    val id: String,
    val tipo: String,
    val pergunta: String,
    val resposta: String,
    val alternativas: List<String>,
    val indiceCorreto: Int,
)

class FonteDadosFirestoreConteudo {
    private val banco = FirebaseFirestore.getInstance()
    private val autenticacao = FirebaseAuth.getInstance()

    fun criarFlashcard(
        cadernoId: String,
        arquivoId: String,
        pergunta: String,
        resposta: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (!validarIds(cadernoId, arquivoId, aoErro)) return

        val referenciaArquivo =
            banco
                .collection("cadernos")
                .document(cadernoId)
                .collection("arquivos")
                .document(arquivoId)

        val referenciaConteudo =
            referenciaArquivo
                .collection("conteudos")
                .document()

        val dados =
            hashMapOf<String, Any>(
                "tipo" to TipoConteudo.FLASHCARD,
                "pergunta" to pergunta.trim(),
                "resposta" to resposta.trim(),
                "alternativas" to emptyList<String>(),
                "indiceCorreto" to -1,
                "criadoEm" to FieldValue.serverTimestamp(),
                "atualizadoEm" to FieldValue.serverTimestamp(),
            )

        val lote = banco.batch()

        lote.set(referenciaConteudo, dados)

        lote.update(
            referenciaArquivo,
            mapOf(
                "quantidadeItens" to FieldValue.increment(1),
                "atualizadoEm" to FieldValue.serverTimestamp(),
            ),
        )

        lote
            .commit()
            .addOnSuccessListener {
                aoSucesso()
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível salvar o flashcard.",
                )
            }
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
        if (!validarIds(cadernoId, arquivoId, aoErro)) return

        val referenciaArquivo =
            banco
                .collection("cadernos")
                .document(cadernoId)
                .collection("arquivos")
                .document(arquivoId)

        val referenciaConteudo =
            referenciaArquivo
                .collection("conteudos")
                .document()

        val dados =
            hashMapOf<String, Any>(
                "tipo" to TipoConteudo.MULTIPLA_ESCOLHA,
                "pergunta" to pergunta.trim(),
                "resposta" to "",
                "alternativas" to alternativas.map { it.trim() },
                "indiceCorreto" to indiceCorreto,
                "criadoEm" to FieldValue.serverTimestamp(),
                "atualizadoEm" to FieldValue.serverTimestamp(),
            )

        val lote = banco.batch()

        lote.set(referenciaConteudo, dados)

        lote.update(
            referenciaArquivo,
            mapOf(
                "quantidadeItens" to FieldValue.increment(1),
                "atualizadoEm" to FieldValue.serverTimestamp(),
            ),
        )

        lote
            .commit()
            .addOnSuccessListener {
                aoSucesso()
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível salvar a questão.",
                )
            }
    }

    fun observarConteudosDoArquivo(
        cadernoId: String,
        arquivoId: String,
        aoAtualizar: (List<ConteudoFirestore>) -> Unit,
        aoErro: (String) -> Unit,
    ): ListenerRegistration? {
        if (!validarIds(cadernoId, arquivoId, aoErro)) return null

        return banco
            .collection("cadernos")
            .document(cadernoId)
            .collection("arquivos")
            .document(arquivoId)
            .collection("conteudos")
            .orderBy("criadoEm", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    aoErro(
                        exception.message
                            ?: "Não foi possível carregar o conteúdo.",
                    )
                    return@addSnapshotListener
                }

                val conteudos =
                    snapshot?.documents.orEmpty().map {
                        documentoParaConteudo(it)
                    }

                aoAtualizar(conteudos)
            }
    }

    fun excluirConteudo(
        cadernoId: String,
        arquivoId: String,
        conteudoId: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (!validarIds(cadernoId, arquivoId, aoErro)) return

        if (conteudoId.isBlank()) {
            aoErro("Não foi possível identificar o conteúdo.")
            return
        }

        val referenciaArquivo =
            banco
                .collection("cadernos")
                .document(cadernoId)
                .collection("arquivos")
                .document(arquivoId)

        val referenciaConteudo =
            referenciaArquivo
                .collection("conteudos")
                .document(conteudoId)

        val lote = banco.batch()

        lote.delete(referenciaConteudo)

        lote.update(
            referenciaArquivo,
            mapOf(
                "quantidadeItens" to FieldValue.increment(-1),
                "atualizadoEm" to FieldValue.serverTimestamp(),
            ),
        )

        lote
            .commit()
            .addOnSuccessListener {
                aoSucesso()
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível excluir o conteúdo.",
                )
            }
    }

    private fun validarIds(
        cadernoId: String,
        arquivoId: String,
        aoErro: (String) -> Unit,
    ): Boolean {
        if (autenticacao.currentUser?.uid.isNullOrBlank()) {
            aoErro("Sua sessão expirou. Faça login novamente.")
            return false
        }

        if (cadernoId.isBlank() || arquivoId.isBlank()) {
            aoErro("Não foi possível identificar o arquivo.")
            return false
        }

        return true
    }

    private fun documentoParaConteudo(documento: DocumentSnapshot): ConteudoFirestore {
        val alternativasBrutas = documento.get("alternativas") as? List<*>

        return ConteudoFirestore(
            id = documento.id,
            tipo = documento.getString("tipo").orEmpty(),
            pergunta = documento.getString("pergunta").orEmpty(),
            resposta = documento.getString("resposta").orEmpty(),
            alternativas =
                alternativasBrutas
                    .orEmpty()
                    .mapNotNull { alternativa ->
                        alternativa as? String
                    },
            indiceCorreto =
                documento
                    .getLong("indiceCorreto")
                    ?.toInt()
                    ?: -1,
        )
    }
}
