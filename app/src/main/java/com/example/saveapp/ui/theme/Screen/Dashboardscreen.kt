package com.example.saveapp.ui.theme.Screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saveapp.R

/** Modelo visual, contiene lo que la tarjeta necesita mostrar. */
data class GoalItemUi(
    val name: String,
    val status: String,
    val currentAmount: String,
    val targetAmount: String,
    val progress: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    totalSaved: String,
    totalProgress: Float,
    totalProgressLabel: String,
    goals: List<GoalItemUi>,
    selectedFilterIndex: Int,
    onFilterSelected: (Int) -> Unit,
    onNavigateToSimulator: () -> Unit,
    onNavigateToDeposit: (GoalItemUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToSimulator,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.dashboard_new_goal)) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SummaryHeader(
                    totalSaved = totalSaved,
                    progress = totalProgress,
                    progressLabel = totalProgressLabel
                )
            }
            item {
                Text(
                    text = stringResource(R.string.dashboard_my_goals),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                CategoryFilterBar(
                    selectedIndex = selectedFilterIndex,
                    onSelected = onFilterSelected
                )
            }
            items(goals) { goal ->
                GoalItemCard(
                    goal = goal,
                    onDeposit = { onNavigateToDeposit(goal) },
                    onSimulate = onNavigateToSimulator
                )
            }
        }
    }
}

@Composable
private fun SummaryHeader(
    totalSaved: String,
    progress: Float,
    progressLabel: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_total_label),
                style = MaterialTheme.typography.labelLarge
            )
            Text(text = totalSaved, style = MaterialTheme.typography.displaySmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = progressLabel, style = MaterialTheme.typography.labelMedium)
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CategoryFilterBar(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = listOf(
        stringResource(R.string.filter_all),
        stringResource(R.string.filter_short),
        stringResource(R.string.filter_medium),
        stringResource(R.string.filter_long)
    )
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            FilterChip(
                selected = selected,
                onClick = { onSelected(index) },
                label = { Text(label) },
                leadingIcon = if (selected) {
                    { Icon(imageVector = Icons.Filled.Check, contentDescription = null) }
                } else null
            )
        }
    }
}

@Composable
private fun GoalItemCard(
    goal: GoalItemUi,
    onDeposit: () -> Unit,
    onSimulate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = goal.name, style = MaterialTheme.typography.titleMedium)
                AssistChip(onClick = {}, label = { Text(goal.status) })
            }
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${goal.currentAmount} / ${goal.targetAmount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                OutlinedButton(onClick = onSimulate) {
                    Text(stringResource(R.string.goal_action_simulate))
                }
                Button(onClick = onDeposit) {
                    Text(stringResource(R.string.goal_action_deposit))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreen(
            totalSaved = "",
            totalProgress = 0f,
            totalProgressLabel = "",
            goals = emptyList(),
            selectedFilterIndex = 0,
            onFilterSelected = {},
            onNavigateToSimulator = {},
            onNavigateToDeposit = {}
        )
    }
}