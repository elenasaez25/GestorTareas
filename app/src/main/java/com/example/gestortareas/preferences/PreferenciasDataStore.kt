package com.example.gestortareas.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "preferencias_usuario")

class PreferenciasDataStore(private val context: Context) {

    companion object {
        val SOLO_PENDIENTES_KEY = booleanPreferencesKey("solo_pendientes")
    }

    val soloPendientesFlow: Flow<Boolean> = context.dataStore.data
        .map { preferencias ->
            preferencias[SOLO_PENDIENTES_KEY] ?: false
        }

    suspend fun guardarSoloPendientes(soloPendientes: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[SOLO_PENDIENTES_KEY] = soloPendientes
        }
    }
}
