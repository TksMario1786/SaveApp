package com.example.saveapp.ui.theme.state

import com.example.saveapp.data.model.CategoriaMeta
import com.example.saveapp.data.model.MetaAhorro

enum class ModoSimulador {
    MODO_CUOTA,
    MODO_SLIDER
}

data class AhorroUiState(
    val isLoading: Boolean = false,
    val metas: List<MetaAhorro> = emptyList(),
    val categoriaFiltro: CategoriaMeta? = null,
    val totalAhorradoConsolidado: Double = 0.0,
    val porcentajeGlobal: Float = 0f,
    val modoSimulador: ModoSimulador = ModoSimulador.MODO_SLIDER,
    val mensajeError: String? = null
)