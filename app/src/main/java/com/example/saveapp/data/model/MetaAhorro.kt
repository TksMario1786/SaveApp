package com.example.saveapp.data.model

import kotlin.math.ceil

enum class CategoriaMeta {
    CORTO_PLAZO,
    MEDIANO_PLAZO,
    LARGO_PLAZO
}
enum class EstadoMeta {
    EN_PROGRESO,
    CASI_COMPLETADA,
    COMPLETADA
}

data class MetaAhorro(
    val id: String,
    val nombre: String,
    val montoObjetivo: Double,
    val montoActual: Double,
    val ahorroMensualSugerido: Double,
    val categoria: CategoriaMeta
) {

    /** Monto que falta para alcanzar el objetivo (nunca negativo). */
    val montoRestante: Double
        get() = (montoObjetivo - montoActual).coerceAtLeast(0.0)

    /** Porcentaje de avance entre 0 y 100, seguro ante división entre cero. */
    val porcentajeAvance: Float
        get() = if (montoObjetivo > 0.0) {
            ((montoActual / montoObjetivo) * 100.0).toFloat().coerceIn(0f, 100f)
        } else {
            0f
        }
    val progreso: Float
        get() = porcentajeAvance / 100f
    /** Estado de la meta según el avance */
    val estado: EstadoMeta
        get() = when {
            montoObjetivo > 0.0 && montoActual >= montoObjetivo -> EstadoMeta.COMPLETADA
            porcentajeAvance >= UMBRAL_CASI_COMPLETADA -> EstadoMeta.CASI_COMPLETADA
            else -> EstadoMeta.EN_PROGRESO
        }
    /** meses restantes según el monto inicial. */
    val mesesRestantesSugeridos: Int
        get() = calcularMesesRestantes(ahorroMensualSugerido)

    /** Cuota mensual necesaria para cubrir el monto restante en [mesesDeseados]. */
    fun calcularCuotaMensual(mesesDeseados: Int): Double =
        if (mesesDeseados > 0) montoRestante / mesesDeseados else 0.0

    /** Meses necesarios (redondeados hacia arriba) ahorrando [ahorroMensual] cada mes. */
    fun calcularMesesRestantes(ahorroMensual: Double): Int =
        if (ahorroMensual > 0.0) ceil(montoRestante / ahorroMensual).toInt() else 0
    private companion object {
        const val UMBRAL_CASI_COMPLETADA = 80f
    }
}