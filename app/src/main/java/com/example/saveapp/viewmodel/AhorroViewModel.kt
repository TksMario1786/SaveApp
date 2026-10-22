package com.example.saveapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saveapp.data.model.CategoriaMeta
import com.example.saveapp.data.model.MetaAhorro
import com.example.saveapp.data.repository.AhorroRepository
import com.example.saveapp.state.AhorroUiState
import com.example.saveapp.state.ErrorAhorro
import com.example.saveapp.state.ModoSimulador
import com.example.saveapp.state.ResultadoSimulacion
import com.example.saveapp.state.SimuladorUiState
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
                    _uiState.update { estado -> estado.copy(isLoading = false) }
                }
                .collect { metas ->
                    val totalAhorrado = metas.sumOf { it.montoActual }
                    val totalObjetivo = metas.sumOf { it.montoObjetivo }

                    _uiState.update { estado ->
                        estado.copy(
                            isLoading = false,
                            metas = metas,
                            totalAhorradoConsolidado = totalAhorrado,
                            totalObjetivoConsolidado = totalObjetivo,
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

    /** Cambia entre el Modo A (cuota) y el Modo B (slider) y recalcula la simulación. */
    fun cambiarModoSimulador(modo: ModoSimulador) {
        _uiState.update { estado ->
            estado.copy(
                modoSimulador = modo,
                simulador = calcularSimulacion(estado.simulador, modo)
            )
        }
    }

    fun actualizarNombre(nombre: String) {
        actualizarSimulador { it.copy(nombre = nombre) }
    }

    fun actualizarMontoObjetivo(texto: String) {
        actualizarSimulador { it.copy(montoObjetivo = filtrarDecimal(texto)) }
    }

    fun actualizarMontoInicial(texto: String) {
        actualizarSimulador { it.copy(montoInicial = filtrarDecimal(texto)) }
    }

    fun actualizarCategoria(categoria: CategoriaMeta) {
        actualizarSimulador { it.copy(categoria = categoria) }
    }

    fun actualizarMesesDeseados(texto: String) {
        actualizarSimulador { it.copy(mesesDeseados = texto.filter { caracter -> caracter.isDigit() }) }
    }

    fun actualizarAhorroMensual(valor: Float) {
        actualizarSimulador { it.copy(ahorroMensual = valor) }
    }

    /** Deja vacia el campo para añadir una meta nueva. */
    fun reiniciarSimulador() {
        _uiState.update { it.copy(simulador = SimuladorUiState(), error = null) }
    }

    private fun actualizarSimulador(transformar: (SimuladorUiState) -> SimuladorUiState) {
        _uiState.update { estado ->
            estado.copy(
                simulador = calcularSimulacion(transformar(estado.simulador), estado.modoSimulador),
                error = null
            )
        }
    }

    /** Recalcula rango del slider y resultados a partir de lo que el usuario ha escrito. */
    private fun calcularSimulacion(
        simulador: SimuladorUiState,
        modo: ModoSimulador
    ): SimuladorUiState {
        val objetivo = convertirMonto(simulador.montoObjetivo) ?: 0.0
        val inicial = convertirMonto(simulador.montoInicial) ?: 0.0

        val referencia = MetaAhorro(
            id = "",
            nombre = simulador.nombre,
            montoObjetivo = objetivo,
            montoActual = inicial,
            ahorroMensualSugerido = 0.0,
            categoria = simulador.categoria
        )

        val restante = referencia.montoRestante
        val minimo = (restante / MAX_MESES_SIMULACION).toFloat()
        val maximo = (restante / FRACCION_MAXIMA_AHORRO).toFloat()
        val ahorroElegido = simulador.ahorroMensual.coerceIn(minimo, maximo)

        val resultado = when (modo) {
            ModoSimulador.MODO_CUOTA -> {
                val meses = simulador.mesesDeseados.toIntOrNull() ?: 0
                val cuota = referencia.calcularCuotaMensual(meses)
                ResultadoSimulacion(
                    ahorroMensual = cuota,
                    ahorroSemanal = cuota / SEMANAS_POR_MES,
                    ahorroDiario = cuota / DIAS_POR_MES,
                    mesesEstimados = meses
                )
            }

            ModoSimulador.MODO_SLIDER -> {
                val ahorro = ahorroElegido.toDouble()
                ResultadoSimulacion(
                    ahorroMensual = ahorro,
                    ahorroSemanal = ahorro / SEMANAS_POR_MES,
                    ahorroDiario = ahorro / DIAS_POR_MES,
                    mesesEstimados = referencia.calcularMesesRestantes(ahorro)
                )
            }
        }

        return simulador.copy(
            ahorroMensual = ahorroElegido,
            ahorroMinimo = minimo,
            ahorroMaximo = maximo,
            resultado = resultado
        )
    }

    /** Valida los datos ingresados antes de agregar la meta. */
    fun agregarNuevaMeta(
        nombre: String,
        montoObjetivoStr: String,
        montoInicialStr: String,
        categoria: CategoriaMeta,
        onExito: () -> Unit = {}
    ) {
        val nombreLimpio = nombre.trim()
        val montoObjetivo = convertirMonto(montoObjetivoStr)
        val montoInicial = if (montoInicialStr.isBlank()) 0.0 else convertirMonto(montoInicialStr)

        if (nombreLimpio.isEmpty() ||
            montoObjetivo == null || montoObjetivo <= 0.0 ||
            montoInicial == null || montoInicial < 0.0 || montoInicial > montoObjetivo
        ) {
            _uiState.update { it.copy(error = ErrorAhorro.META_INVALIDA) }
            return
        }

        val nuevaMeta = MetaAhorro(
            id = UUID.randomUUID().toString(),
            nombre = nombreLimpio,
            montoObjetivo = montoObjetivo,
            montoActual = montoInicial,
            ahorroMensualSugerido = _uiState.value.simulador.resultado.ahorroMensual,
            categoria = categoria
        )

        viewModelScope.launch {
            repository.agregarMeta(nuevaMeta)
            _uiState.update { it.copy(error = null, simulador = SimuladorUiState()) }
            onExito()
        }
    }


    //Abonos
    fun actualizarMontoAbono(texto: String) {
        _uiState.update { it.copy(montoAbono = filtrarDecimal(texto), error = null) }
    }

    /** Vacia los campos despues de añadir un abono. */
    fun reiniciarAbono() {
        _uiState.update { it.copy(montoAbono = "", error = null) }
    }

    /** valida los datos ingresados antes de registrar el abono. */
    fun registrarAbono(idMeta: String, montoAbonoStr: String, onExito: () -> Unit = {}) {
        val montoAbono = convertirMonto(montoAbonoStr)

        if (montoAbono == null || montoAbono <= 0.0) {
            _uiState.update { it.copy(error = ErrorAhorro.ABONO_INVALIDO) }
            return
        }

        viewModelScope.launch {
            repository.registrarAbono(idMeta, montoAbono)
            _uiState.update { it.copy(error = null, montoAbono = "") }
            onExito()
        }
    }

    //Errores

    fun limpiarMensajeError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun filtrarDecimal(texto: String): String =
        texto.filter { it.isDigit() || it == '.' || it == ',' }

    private fun convertirMonto(texto: String): Double? {
        val valor = texto.trim().replace(',', '.').toDoubleOrNull() ?: return null
        return if (valor.isFinite()) valor else null
    }

    private companion object {
        const val MAX_MESES_SIMULACION = 120
        const val FRACCION_MAXIMA_AHORRO = 3
        const val SEMANAS_POR_MES = 4.0
        const val DIAS_POR_MES = 28.0
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