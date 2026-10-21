package com.example.saveapp.data.repository

import com.example.saveapp.data.local.DataStoreManager
import com.example.saveapp.data.local.JsonSeedLoader
import com.example.saveapp.data.model.MetaAhorro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AhorroRepository(
    private val dataStoreManager: DataStoreManager,
    private val jsonSeedLoader: JsonSeedLoader
) {

    private val mutex = Mutex()

    fun obtenerMetas(): Flow<List<MetaAhorro>> = flow {
        mutex.withLock {
            if (!dataStoreManager.existenDatosGuardados()) {
                val metasIniciales = withContext(Dispatchers.IO) {
                    jsonSeedLoader.cargarMetasSemilla()
                }
                dataStoreManager.guardarMetas(metasIniciales)
            }
        }
        emitAll(dataStoreManager.metasFlow)
    }

    suspend fun agregarMeta(nuevaMeta: MetaAhorro) {
        mutex.withLock {
            val actuales = dataStoreManager.metasFlow.first()
            dataStoreManager.guardarMetas(actuales + nuevaMeta)
        }
    }

    suspend fun registrarAbono(idMeta: String, montoAbono: Double) {
        if (montoAbono <= 0.0) return

        mutex.withLock {
            val actuales = dataStoreManager.metasFlow.first()
            if (actuales.none { it.id == idMeta }) return

            val actualizadas = actuales.map { meta ->
                if (meta.id == idMeta) {
                    meta.copy(
                        montoActual = (meta.montoActual + montoAbono)
                            .coerceAtMost(meta.montoObjetivo)
                    )
                } else {
                    meta
                }
            }
            dataStoreManager.guardarMetas(actualizadas)
        }
    }
}