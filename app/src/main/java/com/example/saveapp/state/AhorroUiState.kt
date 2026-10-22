package com.example.saveapp.state

import com.example.saveapp.data.model.CategoriaMeta
import com.example.saveapp.data.model.MetaAhorro

enum class ModoSimulador {
    MODO_CUOTA,
    MODO_SLIDER
}

enum class ErrorAhorro {
    META_INVALIDA,
    ABONO_INVALIDO
}

data class ResultadoSimulacion(
    val ahorroMensual: Double = 0.0,
    val ahorroSemanal: Double = 0.0,
    val ahorroDiario: Double = 0.0,
    val mesesEstimados: Int = 0
)

data class SimuladorUiState(
    val nombre: String = "",
    val montoObjetivo: String = "",
    val montoInicial: String = "",
    val categoria: CategoriaMeta = CategoriaMeta.CORTO_PLAZO,
    val mesesDeseados: String = "",
    val ahorroMensual: Float = 0f,
    val ahorroMinimo: Float = 0f,
    val ahorroMaximo: Float = 0f,
    val resultado: ResultadoSimulacion = ResultadoSimulacion()
) {
    /** Hay un monto restante válido sobre el cual simular. */
    val tieneSimulacion: Boolean
        get() = ahorroMaximo > 0f

    /** Solo se habilita si existe un rango real de ahorro. */
    val sliderHabilitado: Boolean
        get() = ahorroMaximo > ahorroMinimo
}

data class AhorroUiState(
    val isLoading: Boolean = false,
    val metas: List<MetaAhorro> = emptyList(),
    val categoriaFiltro: CategoriaMeta? = null,
    val totalAhorradoConsolidado: Double = 0.0,
    val totalObjetivoConsolidado: Double = 0.0,
    val porcentajeGlobal: Float = 0f,
    val modoSimulador: ModoSimulador = ModoSimulador.MODO_SLIDER,
    val simulador: SimuladorUiState = SimuladorUiState(),
    val montoAbono: String = "",
    val error: ErrorAhorro? = null
) {
    /** Metas visibles según la categoría. */
    val metasFiltradas: List<MetaAhorro>
        get() = if (categoriaFiltro == null) metas else metas.filter { it.categoria == categoriaFiltro }

    /** Avance global para indicadores de progreso. */
    val progresoGlobal: Float
        get() = porcentajeGlobal / 100f
}