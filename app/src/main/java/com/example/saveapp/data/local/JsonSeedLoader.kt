package com.example.saveapp.data.local

import android.content.Context
import com.example.saveapp.R
import com.example.saveapp.data.model.CategoriaMeta
import com.example.saveapp.data.model.MetaAhorro
import org.json.JSONArray

class JsonSeedLoader(private val context: Context) {

    fun cargarMetasSemilla(): List<MetaAhorro> {
        val contenido = context.resources
            .openRawResource(R.raw.metas_base)
            .bufferedReader()
            .use { it.readText() }

        if (contenido.isBlank()) return emptyList()

        val arreglo = JSONArray(contenido)
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
        return metas
    }
}
