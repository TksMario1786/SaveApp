package com.example.saveapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.saveapp.data.model.CategoriaMeta
import com.example.saveapp.data.model.MetaAhorro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "saveapp_prefs")

class DataStoreManager(private val context: Context) {

    private val dataStore = context.applicationContext.dataStore

    private companion object {
        val KEY_METAS_JSON = stringPreferencesKey("KEY_METAS_JSON")
    }

    val metasFlow: Flow<List<MetaAhorro>> = dataStore.data.map { preferencias ->
        deserializarMetas(preferencias[KEY_METAS_JSON])
    }

    suspend fun existenDatosGuardados(): Boolean =
        dataStore.data.first()[KEY_METAS_JSON] != null

    suspend fun guardarMetas(metas: List<MetaAhorro>) {
        val json = serializarMetas(metas)
        dataStore.edit { preferencias ->
            preferencias[KEY_METAS_JSON] = json
        }
    }

    private fun serializarMetas(metas: List<MetaAhorro>): String {
        val arreglo = JSONArray()
        metas.forEach { meta ->
            val objeto = JSONObject().apply {
                put("id", meta.id)
                put("nombre", meta.nombre)
                put("montoObjetivo", meta.montoObjetivo)
                put("montoActual", meta.montoActual)
                put("ahorroMensualSugerido", meta.ahorroMensualSugerido)
                put("categoria", meta.categoria.name)
            }
            arreglo.put(objeto)
        }
        return arreglo.toString()
    }

    private fun deserializarMetas(json: String?): List<MetaAhorro> {
        if (json.isNullOrBlank()) return emptyList()

        return try {
            val arreglo = JSONArray(json)
            val metas = mutableListOf<MetaAhorro>()
            for (i in 0 until arreglo.length()) {
                val objeto = arreglo.getJSONObject(i)
                metas.add(
                    MetaAhorro(
                        id = objeto.getString("id"),
                        nombre = objeto.getString("nombre"),
                        montoObjetivo = objeto.getDouble("montoObjetivo"),
                        montoActual = objeto.getDouble("montoActual"),
                        ahorroMensualSugerido = objeto.getDouble("ahorroMensualSugerido"),
                        categoria = CategoriaMeta.valueOf(objeto.getString("categoria"))
                    )
                )
            }
            metas
        } catch (e: JSONException) {
            emptyList()
        } catch (e: IllegalArgumentException) {
            emptyList()
        }
    }
}