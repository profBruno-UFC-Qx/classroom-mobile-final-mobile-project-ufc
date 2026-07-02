package com.memobrain.memonow.data.remote.autenticacao

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

data class ResultadoCadastro(
    val uid: String,
    val nome: String,
    val email: String,
    val telefone: String,
)

class ServicoCadastroFirebase {
    private val autenticacao = FirebaseAuth.getInstance()
    private val banco = FirebaseFirestore.getInstance()

    fun cadastrarUsuario(
        nome: String,
        email: String,
        senha: String,
        telefone: String,
        aoSucesso: (ResultadoCadastro) -> Unit,
        aoErro: (String) -> Unit,
    ) {
        val nomeLimpo = nome.trim()
        val emailLimpo = email.trim()
        val telefoneLimpo = telefone.trim()

        autenticacao
            .createUserWithEmailAndPassword(
                emailLimpo,
                senha,
            ).addOnSuccessListener { authResult ->
                val usuario = authResult.user

                if (usuario == null) {
                    aoErro("Usuário não encontrado.")
                    return@addOnSuccessListener
                }

                val uid = usuario.uid
                val emailUsuario = usuario.email ?: emailLimpo

                val perfil =
                    UserProfileChangeRequest
                        .Builder()
                        .setDisplayName(nomeLimpo)
                        .build()

                usuario.updateProfile(perfil)

                val dados =
                    hashMapOf(
                        "uid" to uid,
                        "nome" to nomeLimpo,
                        "email" to emailUsuario,
                        "telefone" to telefoneLimpo,
                        "plano" to "Free",
                    )

                banco
                    .collection("usuarios")
                    .document(uid)
                    .set(dados)
                    .addOnSuccessListener {
                        autenticacao.signOut()

                        aoSucesso(
                            ResultadoCadastro(
                                uid = uid,
                                nome = nomeLimpo,
                                email = emailUsuario,
                                telefone = telefoneLimpo,
                            ),
                        )
                    }.addOnFailureListener { exception ->
                        autenticacao.signOut()

                        aoErro(
                            exception.message
                                ?: "Erro ao salvar dados do usuário.",
                        )
                    }
            }.addOnFailureListener { exception ->
                aoErro(
                    exception.message
                        ?: "Erro ao cadastrar usuário.",
                )
            }
    }
}
