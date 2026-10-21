package com.example.saveapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saveapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaMetaScreen(
    goalName: String,
    onGoalNameChange: (String) -> Unit,
    targetAmount: String,
    onTargetAmountChange: (String) -> Unit,
    initialAmount: String,
    onInitialAmountChange: (String) -> Unit,
    isSliderMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    sliderValue: Float,
    onSliderValueChange: (Float) -> Unit,
    estimatedTime: String,
    amountPerDay: String,
    amountPerWeek: String,
    onNavigateBack: () -> Unit,
    onSaveGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.newGoal_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.simulator_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = goalName,
                onValueChange = onGoalNameChange,
                label = { Text(stringResource(R.string.simulator_field_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = targetAmount,
                onValueChange = onTargetAmountChange,
                label = { Text(stringResource(R.string.simulator_field_target)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = initialAmount,
                onValueChange = onInitialAmountChange,
                label = { Text(stringResource(R.string.simulator_field_initial)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !isSliderMode,
                    onClick = { onModeChange(false) },
                    label = { Text(stringResource(R.string.simulator_mode_a)) },
                    leadingIcon = if (!isSliderMode) {
                        { Icon(Icons.Filled.Check, contentDescription = null) }
                    } else null
                )
                FilterChip(
                    selected = isSliderMode,
                    onClick = { onModeChange(true) },
                    label = { Text(stringResource(R.string.simulator_mode_b)) },
                    leadingIcon = if (isSliderMode) {
                        { Icon(Icons.Filled.Check, contentDescription = null) }
                    } else null
                )
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.simulator_slider_question),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.simulator_slider_value, sliderValue.toInt()),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Slider(
                        value = sliderValue,
                        onValueChange = onSliderValueChange,
                        valueRange = 500f..5000f
                    )
                    Text(text = estimatedTime, style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = amountPerDay)
                        Text(text = amountPerWeek)
                    }
                }
            }

            Button(
                onClick = onSaveGoal,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.simulator_save))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NuevaMetaScreenPreview() {
    MaterialTheme {
        NuevaMetaScreen(
            goalName = "",
            onGoalNameChange = {},
            targetAmount = "",
            onTargetAmountChange = {},
            initialAmount = "",
            onInitialAmountChange = {},
            isSliderMode = true,
            onModeChange = {},
            sliderValue = 500f,
            onSliderValueChange = {},
            estimatedTime = "",
            amountPerDay = "",
            amountPerWeek = "",
            onNavigateBack = {},
            onSaveGoal = {}
        )
    }
}


