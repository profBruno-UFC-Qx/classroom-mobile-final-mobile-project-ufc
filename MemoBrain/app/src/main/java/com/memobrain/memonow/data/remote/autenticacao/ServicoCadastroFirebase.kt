package com.memobrain.memonow.data.remote.autenticacao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class ResultadoCadastro(
    val uid: String,
    val email: String,
    val telefone: String,
)

class ServicoCadastroFirebase {
    private val autenticacao = FirebaseAuth.getInstance()
    private val banco = FirebaseFirestore.getInstance()

    fun cadastrarUsuario(
        email: String,
        senha: String,
        telefone: String,
        aoSucesso: (ResultadoCadastro) -> Unit,
        aoError: (String) -> Unit,
    ) {
        autenticacao
            .createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->
                val usuario = authResult.user

                if (usuario == null) {
                    aoError("Usuário não encontrado.")
                    return@addOnSuccessListener
                }

                val uid = usuario.uid
                val emailUsuario = usuario.email ?: email

                val dados =
                    hashMapOf(
                        "uid" to uid,
                        "email" to emailUsuario,
                        "telefone" to telefone,
                    )

                banco
                    .collection("usuarios")
                    .document(uid)
                    .set(dados)
                    .addOnSuccessListener {
                        aoSucesso(
                            ResultadoCadastro(
                                uid = uid,
                                email = emailUsuario,
                                telefone = telefone,
                            ),
                        )
                    }.addOnFailureListener { exception -> aoError(exception.message ?: "Erro ao salvar dados do usuario.") }
            }.addOnFailureListener { exception -> aoError(exception.message ?: "Erro ao cadastrar usuario.") }
    }
}
