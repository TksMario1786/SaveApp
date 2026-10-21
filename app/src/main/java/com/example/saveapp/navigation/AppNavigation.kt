package com.example.saveapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.saveapp.screen.DashboardScreen
import com.example.saveapp.screen.DepositDialog
import com.example.saveapp.screen.GoalItemUi
import com.example.saveapp.screen.NuevaMetaScreen
import java.util.Locale
import kotlin.math.ceil

private fun String.toNumericValue(): Float {
    return this.replace("$", "").replace(",", "").trim().toFloatOrNull() ?: 0f
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    // Lista de metas
    var goals by remember { mutableStateOf(emptyList<GoalItemUi>()) }

    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var initialAmount by remember { mutableStateOf("") }
    var isSliderMode by remember { mutableStateOf(true) }
    var sliderValue by remember { mutableFloatStateOf(500f) }

    var selectedGoalIndex by remember { mutableIntStateOf(-1) }
    var depositAmount by remember { mutableStateOf("") }

    // calculos dashboard
    val totalSavedVal = goals.sumOf { it.currentAmount.toNumericValue().toDouble() }.toFloat()
    val totalTargetVal = goals.sumOf { it.targetAmount.toNumericValue().toDouble() }.toFloat()
    val totalProgress = if (totalTargetVal > 0f) (totalSavedVal / totalTargetVal).coerceIn(0f, 1f) else 0f

    val totalSavedStr = String.format(Locale.US, "$%.2f", totalSavedVal)
    val totalProgressLabelStr = "${(totalProgress * 100).toInt()}% completado"

    // calculos simulator
    val targetVal = targetAmount.toNumericValue()
    val initialVal = initialAmount.toNumericValue()
    val remainingVal = (targetVal - initialVal).coerceAtLeast(0f)

    val estimatedMonths = if (sliderValue > 0f && remainingVal > 0f) {
        ceil(remainingVal / sliderValue).toInt()
    } else 0

    val estimatedTimeStr = when {
        targetVal <= 0f -> "Ingresa un monto objetivo"
        remainingVal <= 0f -> "¡Monto objetivo alcanzado!"
        else -> "$estimatedMonths meses estimados"
    }

    val dailyVal = if (sliderValue > 0f) sliderValue / 30f else 0f
    val weeklyVal = if (sliderValue > 0f) sliderValue / 4.333f else 0f

    val amountPerDayStr = String.format(Locale.US, "$%.2f / día", dailyVal)
    val amountPerWeekStr = String.format(Locale.US, "$%.2f / semana", weeklyVal)

    // calculos deposit
    val selectedGoal = goals.getOrNull(selectedGoalIndex)
    val currentGoalAmountVal = selectedGoal?.currentAmount?.toNumericValue() ?: 0f
    val depositVal = depositAmount.toNumericValue()
    val newEstimatedBalanceVal = currentGoalAmountVal + depositVal
    val summaryBalanceStr = String.format(Locale.US, "$%.2f", newEstimatedBalanceVal)

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                totalSaved = totalSavedStr,
                totalProgress = totalProgress,
                totalProgressLabel = totalProgressLabelStr,
                goals = goals,
                selectedFilterIndex = selectedFilterIndex,
                onFilterSelected = { selectedFilterIndex = it },
                onNavigateToSimulator = { navController.navigate("simulator") },
                onNavigateToDeposit = { goal ->
                    selectedGoalIndex = goals.indexOf(goal)
                    depositAmount = ""
                    navController.navigate("deposit")
                }
            )
        }

        composable("simulator") {
            NuevaMetaScreen(
                goalName = goalName,
                onGoalNameChange = { goalName = it },
                targetAmount = targetAmount,
                onTargetAmountChange = { targetAmount = it },
                initialAmount = initialAmount,
                onInitialAmountChange = { initialAmount = it },
                isSliderMode = isSliderMode,
                onModeChange = { isSliderMode = it },
                sliderValue = sliderValue,
                onSliderValueChange = { sliderValue = it },
                estimatedTime = estimatedTimeStr,
                amountPerDay = amountPerDayStr,
                amountPerWeek = amountPerWeekStr,
                onNavigateBack = { navController.popBackStack() },
                onSaveGoal = {
                    if (goalName.isNotBlank() && targetVal > 0f) {
                        val progressVal = (initialVal / targetVal).coerceIn(0f, 1f)
                        val newGoal = GoalItemUi(
                            name = goalName,
                            status = if (progressVal >= 1f) "Completado" else "En progreso",
                            currentAmount = String.format(Locale.US, "$%.2f", initialVal),
                            targetAmount = String.format(Locale.US, "$%.2f", targetVal),
                            progress = progressVal
                        )
                        goals = goals + newGoal

                        // Reset input fields
                        goalName = ""
                        targetAmount = ""
                        initialAmount = ""
                        sliderValue = 500f
                    }
                    navController.popBackStack()
                }
            )
        }

        dialog("deposit") {
            DepositDialog(
                goalName = selectedGoal?.name ?: "",
                amount = depositAmount,
                onAmountChange = { depositAmount = it },
                summaryBalance = summaryBalanceStr,
                onCancel = { navController.popBackStack() },
                onConfirm = {
                    if (selectedGoalIndex in goals.indices && depositVal > 0f) {
                        val updatedGoal = goals[selectedGoalIndex].let { oldGoal ->
                            val newAmountVal = oldGoal.currentAmount.toNumericValue() + depositVal
                            val targetNum = oldGoal.targetAmount.toNumericValue()
                            val newProgress = if (targetNum > 0f) (newAmountVal / targetNum).coerceIn(0f, 1f) else 0f
                            oldGoal.copy(
                                currentAmount = String.format(Locale.US, "$%.2f", newAmountVal),
                                progress = newProgress,
                                status = if (newProgress >= 1f) "Completado" else "En progreso"
                            )
                        }
                        goals = goals.toMutableList().apply {
                            set(selectedGoalIndex, updatedGoal)
                        }
                    }
                    depositAmount = ""
                    navController.popBackStack()
                }
            )
        }
    }
}
