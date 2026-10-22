package com.example.saveapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.saveapp.R
import com.example.saveapp.data.model.MetaAhorro
import com.example.saveapp.state.AhorroUiState
import com.example.saveapp.state.ErrorAhorro

@Composable
fun DepositDialog(
    meta: MetaAhorro,
    uiState: AhorroUiState,
    onMontoChange: (String) -> Unit,
    onConfirmar: (String, String) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hayError = uiState.error == ErrorAhorro.ABONO_INVALIDO

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.deposit_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Column {
                Text(
                    text = meta.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(
                        R.string.deposit_balance_summary,
                        meta.montoActual,
                        meta.montoObjetivo
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = uiState.montoAbono,
                onValueChange = onMontoChange,
                label = { Text(stringResource(R.string.deposit_field)) },
                prefix = { Text(stringResource(R.string.field_currency_prefix)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = hayError,
                supportingText = {
                    Text(
                        text = stringResource(
                            if (hayError) R.string.error_invalid_deposit else R.string.deposit_helper
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(onClick = onCancelar) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(onClick = { onConfirmar(meta.id, uiState.montoAbono) }) {
                    Text(stringResource(R.string.action_confirm_deposit))
                }
            }
        }
    }
}