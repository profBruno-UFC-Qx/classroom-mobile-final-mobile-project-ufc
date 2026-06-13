package com.memobrain.memonow.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sessao_usuario")

data class SessaoUsuario(
    val uid: String,
    val email: String,
)

class ArmazenamentoSessao(
    private val context: Context,
) {
    companion object {
        private val CHAVE_UID = stringPreferencesKey("uid")
        private val CHAVE_EMAIL = stringPreferencesKey("email")
    }

    val sessaoFlow: Flow<SessaoUsuario?> =
        context.dataStore.data.map { preferencias ->

            val uid = preferencias[CHAVE_UID]
            val email = preferencias[CHAVE_EMAIL]

            if (uid.isNullOrBlank() || email.isNullOrBlank()) {
                null
            } else {
                SessaoUsuario(uid, email)
            }
        }

    suspend fun salvarSessao(
        uid: String,
        email: String,
    ) {
        context.dataStore.edit { preferencias ->
            preferencias[CHAVE_UID] = uid
            preferencias[CHAVE_EMAIL] = email
        }
    }

    suspend fun limparSessao() {
        context.dataStore.edit { preferencias ->
            preferencias.clear()
        }
    }
}
