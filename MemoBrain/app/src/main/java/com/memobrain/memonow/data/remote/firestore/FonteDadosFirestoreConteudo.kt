package com.memobrain.memonow.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
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
        val dados =
            hashMapOf<String, Any>(
                "tipo" to TipoConteudo.FLASHCARD,
                "pergunta" to pergunta.trim(),
                "resposta" to resposta.trim(),
                "alternativas" to emptyList<String>(),
                "indiceCorreto" to -1,
                "revisado" to false,
                "criadoEm" to FieldValue.serverTimestamp(),
                "atualizadoEm" to FieldValue.serverTimestamp(),
            )

        salvarConteudo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            dados = dados,
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
        val dados =
            hashMapOf<String, Any>(
                "tipo" to TipoConteudo.MULTIPLA_ESCOLHA,
                "pergunta" to pergunta.trim(),
                "resposta" to "",
                "alternativas" to alternativas.map { it.trim() },
                "indiceCorreto" to indiceCorreto,
                "revisado" to false,
                "criadoEm" to FieldValue.serverTimestamp(),
                "atualizadoEm" to FieldValue.serverTimestamp(),
            )

        salvarConteudo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            dados = dados,
            aoSucesso = aoSucesso,
            aoErro = aoErro,
        )
    }

    private fun salvarConteudo(
        cadernoId: String,
        arquivoId: String,
        dados: HashMap<String, Any>,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (!validarIds(cadernoId, arquivoId, aoErro)) return

        val referenciaArquivo =
            referenciaArquivo(
                cadernoId = cadernoId,
                arquivoId = arquivoId,
            )

        val referenciaConteudo =
            referenciaArquivo
                .collection("conteudos")
                .document()

        val lote = banco.batch()

        lote.set(referenciaConteudo, dados)

        lote.update(
            referenciaArquivo,
            mapOf(
                "atualizadoEm" to FieldValue.serverTimestamp(),
            ),
        )

        lote
            .commit()
            .addOnSuccessListener {
                sincronizarProgressoDoCaderno(
                    cadernoId = cadernoId,
                    aoSucesso = aoSucesso,
                    aoErro = aoErro,
                )
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível salvar o conteúdo.",
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

        return referenciaArquivo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
        ).collection("conteudos")
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
                    snapshot
                        ?.documents
                        .orEmpty()
                        .map { documento ->
                            documentoParaConteudo(documento)
                        }

                aoAtualizar(conteudos)
            }
    }

    fun concluirRevisao(
        cadernoId: String,
        arquivoId: String,
        conteudoIds: List<String>,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (!validarIds(cadernoId, arquivoId, aoErro)) return

        val idsValidos =
            conteudoIds
                .filter { it.isNotBlank() }
                .distinct()

        if (idsValidos.isEmpty()) {
            aoSucesso()
            return
        }

        if (idsValidos.size > 450) {
            aoErro("Há conteúdos demais para concluir nesta revisão.")
            return
        }

        val referenciaArquivo =
            referenciaArquivo(
                cadernoId = cadernoId,
                arquivoId = arquivoId,
            )

        val lote = banco.batch()

        idsValidos.forEach { conteudoId ->
            val referenciaConteudo =
                referenciaArquivo
                    .collection("conteudos")
                    .document(conteudoId)

            lote.update(
                referenciaConteudo,
                mapOf(
                    "revisado" to true,
                    "revisadoEm" to FieldValue.serverTimestamp(),
                    "atualizadoEm" to FieldValue.serverTimestamp(),
                ),
            )
        }

        lote
            .commit()
            .addOnSuccessListener {
                sincronizarProgressoDoCaderno(
                    cadernoId = cadernoId,
                    aoSucesso = aoSucesso,
                    aoErro = aoErro,
                )
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível concluir a revisão.",
                )
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

        referenciaArquivo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
        ).collection("conteudos")
            .document(conteudoId)
            .delete()
            .addOnSuccessListener {
                sincronizarProgressoDoCaderno(
                    cadernoId = cadernoId,
                    aoSucesso = aoSucesso,
                    aoErro = aoErro,
                )
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível excluir o conteúdo.",
                )
            }
    }

    private fun sincronizarProgressoDoCaderno(
        cadernoId: String,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        val referenciaCaderno =
            banco
                .collection("cadernos")
                .document(cadernoId)

        referenciaCaderno
            .collection("arquivos")
            .get()
            .addOnSuccessListener { arquivosSnapshot ->
                val arquivos = arquivosSnapshot.documents

                if (arquivos.isEmpty()) {
                    atualizarContadores(
                        referenciaCaderno = referenciaCaderno,
                        arquivos = emptyList(),
                        conteudosPorArquivo = emptyList(),
                        aoSucesso = aoSucesso,
                        aoErro = aoErro,
                    )
                    return@addOnSuccessListener
                }

                buscarConteudosDosArquivos(
                    referenciaCaderno = referenciaCaderno,
                    arquivos = arquivos,
                    indiceArquivo = 0,
                    conteudosPorArquivo = mutableListOf(),
                    aoSucesso = aoSucesso,
                    aoErro = aoErro,
                )
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível atualizar o progresso.",
                )
            }
    }

    private fun buscarConteudosDosArquivos(
        referenciaCaderno: DocumentReference,
        arquivos: List<DocumentSnapshot>,
        indiceArquivo: Int,
        conteudosPorArquivo: MutableList<List<DocumentSnapshot>>,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        if (indiceArquivo >= arquivos.size) {
            atualizarContadores(
                referenciaCaderno = referenciaCaderno,
                arquivos = arquivos,
                conteudosPorArquivo = conteudosPorArquivo,
                aoSucesso = aoSucesso,
                aoErro = aoErro,
            )
            return
        }

        arquivos[indiceArquivo]
            .reference
            .collection("conteudos")
            .get()
            .addOnSuccessListener { conteudosSnapshot ->
                conteudosPorArquivo.add(conteudosSnapshot.documents)

                buscarConteudosDosArquivos(
                    referenciaCaderno = referenciaCaderno,
                    arquivos = arquivos,
                    indiceArquivo = indiceArquivo + 1,
                    conteudosPorArquivo = conteudosPorArquivo,
                    aoSucesso = aoSucesso,
                    aoErro = aoErro,
                )
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Não foi possível atualizar o progresso.",
                )
            }
    }

    private fun atualizarContadores(
        referenciaCaderno: DocumentReference,
        arquivos: List<DocumentSnapshot>,
        conteudosPorArquivo: List<List<DocumentSnapshot>>,
        aoSucesso: () -> Unit,
        aoErro: (String) -> Unit,
    ) {
        val totalItens =
            conteudosPorArquivo.sumOf { conteudos ->
                conteudos.size
            }

        val totalRevisados =
            conteudosPorArquivo.sumOf { conteudos ->
                conteudos.count { conteudo ->
                    conteudo.getBoolean("revisado") == true
                }
            }

        val lote = banco.batch()

        arquivos.forEachIndexed { indice, arquivo ->
            val quantidadeItens =
                conteudosPorArquivo
                    .getOrNull(indice)
                    ?.size
                    ?: 0

            lote.update(
                arquivo.reference,
                mapOf(
                    "quantidadeItens" to quantidadeItens,
                    "atualizadoEm" to FieldValue.serverTimestamp(),
                ),
            )
        }

        lote.update(
            referenciaCaderno,
            mapOf(
                "revisados" to totalRevisados,
                "restantes" to (totalItens - totalRevisados),
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
                        ?: "Não foi possível atualizar o progresso.",
                )
            }
    }

    private fun referenciaArquivo(
        cadernoId: String,
        arquivoId: String,
    ) = banco
        .collection("cadernos")
        .document(cadernoId)
        .collection("arquivos")
        .document(arquivoId)

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
        val alternativasBrutas =
            documento.get("alternativas") as? List<*>

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
