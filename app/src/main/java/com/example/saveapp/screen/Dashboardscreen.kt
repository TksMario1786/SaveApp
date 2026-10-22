package com.example.saveapp.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.saveapp.R
import com.example.saveapp.data.model.CategoriaMeta
import com.example.saveapp.data.model.EstadoMeta
import com.example.saveapp.data.model.MetaAhorro
import com.example.saveapp.state.AhorroUiState

@Composable
fun DashboardScreen(
    uiState: AhorroUiState,
    onFiltrarCategoria: (CategoriaMeta?) -> Unit,
    onNuevaMeta: () -> Unit,
    onAbonar: (MetaAhorro) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaMeta,
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.dashboard_new_goal)) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ResumenAhorroCard(
                    totalAhorrado = uiState.totalAhorradoConsolidado,
                    totalObjetivo = uiState.totalObjetivoConsolidado,
                    porcentaje = uiState.porcentajeGlobal,
                    progreso = uiState.progresoGlobal
                )
            }
            item { EncabezadoMetas(cantidad = uiState.metas.size) }
            item {
                FiltrosCategoria(
                    seleccion = uiState.categoriaFiltro,
                    onSeleccionar = onFiltrarCategoria
                )
            }

            when {
                uiState.isLoading && uiState.metas.isEmpty() -> item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.metas.isEmpty() -> item {
                    EstadoVacio(
                        titulo = stringResource(R.string.dashboard_empty_title),
                        mensaje = stringResource(R.string.dashboard_empty_message)
                    )
                }

                uiState.metasFiltradas.isEmpty() -> item {
                    EstadoVacio(titulo = stringResource(R.string.dashboard_empty_filter))
                }

                else -> items(uiState.metasFiltradas, key = { it.id }) { meta ->
                    MetaCard(meta = meta, onAbonar = { onAbonar(meta) })
                }
            }
        }
    }
}

@Composable
fun ResumenAhorroCard(
    totalAhorrado: Double,
    totalObjetivo: Double,
    porcentaje: Float,
    progreso: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_total_label),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = stringResource(R.string.formato_monto_moneda, totalAhorrado),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.dashboard_total_subtitle),
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.dashboard_global_of, totalObjetivo),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(R.string.formato_porcentaje, porcentaje),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EncabezadoMetas(cantidad: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.dashboard_my_goals),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.dashboard_goals_count, cantidad),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FiltrosCategoria(
    seleccion: CategoriaMeta?,
    onSeleccionar: (CategoriaMeta?) -> Unit,
    modifier: Modifier = Modifier
) {
    val descripcion = stringResource(R.string.dashboard_filter_label)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .semantics { contentDescription = descripcion },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChipSeleccionable(
            texto = stringResource(R.string.filter_all),
            seleccionado = seleccion == null,
            onClick = { onSeleccionar(null) }
        )
        CategoriaMeta.entries.forEach { categoria ->
            ChipSeleccionable(
                texto = categoria.etiqueta(),
                seleccionado = seleccion == categoria,
                onClick = { onSeleccionar(categoria) }
            )
        }
    }
}

@Composable
fun MetaCard(
    meta: MetaAhorro,
    onAbonar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completada = meta.estado == EstadoMeta.COMPLETADA

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meta.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = meta.categoria.etiqueta(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AssistChip(
                    onClick = {},
                    label = { Text(meta.estado.etiqueta()) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = stringResource(R.string.formato_monto, meta.montoActual),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.goal_amount_target, meta.montoObjetivo),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(R.string.formato_porcentaje, meta.porcentajeAvance),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            LinearProgressIndicator(
                progress = { meta.progreso },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!completada && meta.mesesRestantesSugeridos > 0) {
                    Text(
                        text = stringResource(
                            R.string.goal_remaining_months,
                            meta.mesesRestantesSugeridos
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = onAbonar, enabled = !completada) {
                    Text(stringResource(R.string.goal_action_deposit))
                }
            }
        }
    }
}

@Composable
fun EstadoVacio(
    titulo: String,
    modifier: Modifier = Modifier,
    mensaje: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (mensaje != null) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}