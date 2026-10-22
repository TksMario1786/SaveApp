package com.example.saveapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saveapp.data.model.CategoriaMeta
import com.example.saveapp.data.model.MetaAhorro
import com.example.saveapp.data.repository.AhorroRepository
import com.example.saveapp.state.AhorroUiState
import com.example.saveapp.state.ModoSimulador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AhorroViewModel(
    private val repository: AhorroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AhorroUiState())
    val uiState: StateFlow<AhorroUiState> = _uiState.asStateFlow()

    init {
        observarMetas()
    }

    private fun observarMetas() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.obtenerMetas()
                .catch {
                    _uiState.update { estado ->
                        estado.copy(isLoading = false)
                    }
                }
                .collect { metas ->
                    val totalAhorrado = metas.sumOf { it.montoActual }
                    val totalObjetivo = metas.sumOf { it.montoObjetivo }

                    _uiState.update { estado ->
                        estado.copy(
                            isLoading = false,
                            metas = metas,
                            totalAhorradoConsolidado = totalAhorrado,
                            porcentajeGlobal = calcularPorcentajeGlobal(totalAhorrado, totalObjetivo)
                        )
                    }
                }
        }
    }

    private fun calcularPorcentajeGlobal(totalAhorrado: Double, totalObjetivo: Double): Float =
        if (totalObjetivo > 0.0) {
            ((totalAhorrado / totalObjetivo) * 100.0).toFloat().coerceIn(0f, 100f)
        } else {
            0f
        }

    fun filtrarPorCategoria(categoria: CategoriaMeta?) {
        _uiState.update { it.copy(categoriaFiltro = categoria) }
    }

    fun cambiarModoSimulador(modo: ModoSimulador) {
        _uiState.update { it.copy(modoSimulador = modo) }
    }

    fun agregarNuevaMeta(
        nombre: String,
        montoObjetivoStr: String,
        montoInicialStr: String,
        categoria: CategoriaMeta
    ) {
        val nombreLimpio = nombre.trim()
        val montoObjetivo = convertirMonto(montoObjetivoStr)
        val montoInicial = if (montoInicialStr.isBlank()) 0.0 else convertirMonto(montoInicialStr)

        val datosValidos = nombreLimpio.isNotEmpty() &&
                montoObjetivo != null && montoObjetivo > 0.0 &&
                montoInicial != null && montoInicial >= 0.0 && montoInicial <= montoObjetivo

        if (!datosValidos) {
            _uiState.update { it.copy(mensajeError = MENSAJE_ERROR_META) }
            return
        }

        val nuevaMeta = MetaAhorro(
            id = UUID.randomUUID().toString(),
            nombre = nombreLimpio,
            montoObjetivo = montoObjetivo!!,
            montoActual = montoInicial!!,
            ahorroMensualSugerido = 0.0,
            categoria = categoria
        )

        viewModelScope.launch {
            repository.agregarMeta(nuevaMeta)
            _uiState.update { it.copy(mensajeError = null) }
        }
    }

    fun registrarAbono(idMeta: String, montoAbonoStr: String) {
        val montoAbono = convertirMonto(montoAbonoStr)

        if (montoAbono == null || montoAbono <= 0.0) {
            _uiState.update { it.copy(mensajeError = MENSAJE_ERROR_ABONO) }
            return
        }

        viewModelScope.launch {
            repository.registrarAbono(idMeta, montoAbono)
            _uiState.update { it.copy(mensajeError = null) }
        }
    }

    fun limpiarMensajeError() {
        _uiState.update { it.copy(mensajeError = null) }
    }

    private fun convertirMonto(texto: String): Double? {
        val valor = texto.trim().replace(',', '.').toDoubleOrNull() ?: return null
        return if (valor.isFinite()) valor else null
    }

    private companion object {
        const val MENSAJE_ERROR_META = "Por favor ingresa un nombre y montos válidos."
        const val MENSAJE_ERROR_ABONO = "Ingresa un monto de abono válido mayor a \$0"
    }
}

class AhorroViewModelFactory(
    private val repository: AhorroRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AhorroViewModel::class.java)) {
            return AhorroViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}