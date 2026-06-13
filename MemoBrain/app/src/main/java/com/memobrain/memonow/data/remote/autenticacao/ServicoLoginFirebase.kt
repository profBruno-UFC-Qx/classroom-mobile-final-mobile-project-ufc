package com.memobrain.memonow.data.remote.autenticacao

import com.google.firebase.auth.FirebaseAuth

data class ResultadoLogin(
    val uid: String,
    val email: String,
)

class ServicoLoginFirebase {
    private val autenticacao = FirebaseAuth.getInstance()

    fun loginUsuario(
        email: String,
        senha: String,
        aoSucesso: (ResultadoLogin) -> Unit,
        aoErro: (String) -> Unit,
    ) {
        autenticacao
            .signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->
                val usuario = authResult.user

                if (usuario != null) {
                    val resultado =
                        ResultadoLogin(
                            uid = usuario.uid,
                            email = usuario.email ?: "",
                        )
                    aoSucesso(resultado)
                } else {
                    aoErro("Usuário não encontrado.")
                }
            }.addOnFailureListener { exception ->
                aoErro(exception.message ?: "Erro ao fazer login.")
            }
    }

    fun verificarUsuarioLogado(): Boolean = autenticacao.currentUser != null

    fun obterUsuarioAtual() = autenticacao.currentUser

    fun logout() {
        autenticacao.signOut()
    }
}
