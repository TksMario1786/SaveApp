package com.example.saveapp.ui.theme.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saveapp.R

@Composable
fun DepositDialog(
    goalName: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    summaryBalance: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.deposit_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(text = goalName, style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text(stringResource(R.string.deposit_field_amount)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.deposit_summary_title),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(text = summaryBalance, style = MaterialTheme.typography.headlineSmall)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                OutlinedButton(onClick = onCancel) {
                    Text(stringResource(R.string.deposit_cancel))
                }
                Button(onClick = onConfirm) {
                    Text(stringResource(R.string.deposit_confirm))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DepositDialogPreview() {
    MaterialTheme {
        DepositDialog(
            goalName = "",
            amount = "",
            onAmountChange = {},
            summaryBalance = "",
            onCancel = {},
            onConfirm = {}
        )
    }
}


