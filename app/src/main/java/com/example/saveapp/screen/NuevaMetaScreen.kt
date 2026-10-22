package com.example.saveapp.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.saveapp.R
import com.example.saveapp.data.model.CategoriaMeta
import com.example.saveapp.state.AhorroUiState
import com.example.saveapp.state.ErrorAhorro
import com.example.saveapp.state.ModoSimulador
import com.example.saveapp.state.ResultadoSimulacion
import com.example.saveapp.state.SimuladorUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaMetaScreen(
    uiState: AhorroUiState,
    onNombreChange: (String) -> Unit,
    onMontoObjetivoChange: (String) -> Unit,
    onMontoInicialChange: (String) -> Unit,
    onCategoriaChange: (CategoriaMeta) -> Unit,
    onModoChange: (ModoSimulador) -> Unit,
    onMesesDeseadosChange: (String) -> Unit,
    onAhorroMensualChange: (Float) -> Unit,
    onGuardarMeta: (String, String, String, CategoriaMeta) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val simulador = uiState.simulador

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_goal_title)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.new_goal_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.new_goal_headline),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.new_goal_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = simulador.nombre,
                onValueChange = onNombreChange,
                label = { Text(stringResource(R.string.field_goal_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            CampoMonto(
                valor = simulador.montoObjetivo,
                etiqueta = R.string.field_goal_target,
                onValorChange = onMontoObjetivoChange
            )
            CampoMonto(
                valor = simulador.montoInicial,
                etiqueta = R.string.field_goal_initial,
                onValorChange = onMontoInicialChange
            )

            Text(
                text = stringResource(R.string.category_label),
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoriaMeta.entries.forEach { categoria ->
                    ChipSeleccionable(
                        texto = categoria.etiqueta(),
                        seleccionado = simulador.categoria == categoria,
                        onClick = { onCategoriaChange(categoria) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipSeleccionable(
                    texto = stringResource(R.string.mode_cuota),
                    seleccionado = uiState.modoSimulador == ModoSimulador.MODO_CUOTA,
                    onClick = { onModoChange(ModoSimulador.MODO_CUOTA) }
                )
                ChipSeleccionable(
                    texto = stringResource(R.string.mode_slider),
                    seleccionado = uiState.modoSimulador == ModoSimulador.MODO_SLIDER,
                    onClick = { onModoChange(ModoSimulador.MODO_SLIDER) }
                )
            }

            when (uiState.modoSimulador) {
                ModoSimulador.MODO_SLIDER -> SimuladorSlider(
                    simulador = simulador,
                    onAhorroMensualChange = onAhorroMensualChange
                )

                ModoSimulador.MODO_CUOTA -> SimuladorCuota(
                    simulador = simulador,
                    onMesesDeseadosChange = onMesesDeseadosChange
                )
            }

            if (simulador.tieneSimulacion) {
                ResultadoSimulacionCard(
                    modo = uiState.modoSimulador,
                    resultado = simulador.resultado
                )
            } else {
                Text(
                    text = stringResource(R.string.simulator_empty_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (uiState.error == ErrorAhorro.META_INVALIDA) {
                Text(
                    text = stringResource(R.string.error_invalid_goal),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    onGuardarMeta(
                        simulador.nombre,
                        simulador.montoObjetivo,
                        simulador.montoInicial,
                        simulador.categoria
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_save_goal))
            }
        }
    }
}

@Composable
fun CampoMonto(
    valor: String,
    @StringRes etiqueta: Int,
    onValorChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(stringResource(etiqueta)) },
        prefix = { Text(stringResource(R.string.field_currency_prefix)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun SimuladorSlider(
    simulador: SimuladorUiState,
    onAhorroMensualChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.simulator_slider_question),
            style = MaterialTheme.typography.titleMedium
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = stringResource(R.string.formato_monto, simulador.ahorroMensual),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.simulator_per_month),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )
        }
        Slider(
            value = simulador.ahorroMensual,
            onValueChange = onAhorroMensualChange,
            valueRange = simulador.ahorroMinimo..simulador.ahorroMaximo,
            enabled = simulador.sliderHabilitado
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.formato_monto, simulador.ahorroMinimo),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = stringResource(R.string.formato_monto, simulador.ahorroMaximo),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun SimuladorCuota(
    simulador: SimuladorUiState,
    onMesesDeseadosChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.simulator_months_question),
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = simulador.mesesDeseados,
            onValueChange = onMesesDeseadosChange,
            label = { Text(stringResource(R.string.field_months)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ResultadoSimulacionCard(
    modo: ModoSimulador,
    resultado: ResultadoSimulacion,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val titulo = when (modo) {
                ModoSimulador.MODO_SLIDER ->
                    stringResource(R.string.simulator_result_months, resultado.mesesEstimados)

                ModoSimulador.MODO_CUOTA ->
                    stringResource(R.string.simulator_result_monthly, resultado.ahorroMensual)
            }
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ValorEtiquetado(
                    monto = resultado.ahorroDiario,
                    etiqueta = R.string.simulator_per_day
                )
                ValorEtiquetado(
                    monto = resultado.ahorroSemanal,
                    etiqueta = R.string.simulator_per_week
                )
            }
            Text(
                text = stringResource(R.string.simulator_note),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun ValorEtiquetado(
    monto: Double,
    @StringRes etiqueta: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.formato_monto, monto),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(etiqueta),
            style = MaterialTheme.typography.bodySmall
        )
    }
}