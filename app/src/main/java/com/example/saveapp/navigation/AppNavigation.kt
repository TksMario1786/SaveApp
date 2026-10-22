package com.example.saveapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.saveapp.data.local.DataStoreManager
import com.example.saveapp.data.local.JsonSeedLoader
import com.example.saveapp.data.repository.AhorroRepository
import com.example.saveapp.screen.DashboardScreen
import com.example.saveapp.screen.DepositDialog
import com.example.saveapp.screen.NuevaMetaScreen
import com.example.saveapp.viewmodel.AhorroViewModel
import com.example.saveapp.viewmodel.AhorroViewModelFactory

object Rutas {
    const val DASHBOARD = "dashboard"
    const val NUEVA_META = "nueva_meta"
    const val ARG_ID_META = "idMeta"
    const val ABONO = "abono/{$ARG_ID_META}"

    fun abono(idMeta: String): String = "abono/$idMeta"
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val contexto = LocalContext.current.applicationContext
    val viewModel: AhorroViewModel = viewModel(
        factory = remember(contexto) {
            AhorroViewModelFactory(
                AhorroRepository(
                    dataStoreManager = DataStoreManager(contexto),
                    jsonSeedLoader = JsonSeedLoader(contexto)
                )
            )
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.DASHBOARD,
        modifier = modifier
    ) {
        composable(Rutas.DASHBOARD) {
            DashboardScreen(
                uiState = uiState,
                onFiltrarCategoria = viewModel::filtrarPorCategoria,
                onNuevaMeta = {
                    viewModel.reiniciarSimulador()
                    navController.navigate(Rutas.NUEVA_META)
                },
                onAbonar = { meta ->
                    viewModel.reiniciarAbono()
                    navController.navigate(Rutas.abono(meta.id))
                }
            )
        }

        composable(Rutas.NUEVA_META) {
            NuevaMetaScreen(
                uiState = uiState,
                onNombreChange = viewModel::actualizarNombre,
                onMontoObjetivoChange = viewModel::actualizarMontoObjetivo,
                onMontoInicialChange = viewModel::actualizarMontoInicial,
                onCategoriaChange = viewModel::actualizarCategoria,
                onModoChange = viewModel::cambiarModoSimulador,
                onMesesDeseadosChange = viewModel::actualizarMesesDeseados,
                onAhorroMensualChange = viewModel::actualizarAhorroMensual,
                onGuardarMeta = { nombre, objetivo, inicial, categoria ->
                    viewModel.agregarNuevaMeta(nombre, objetivo, inicial, categoria) {
                        navController.popBackStack()
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        dialog(
            route = Rutas.ABONO,
            arguments = listOf(navArgument(Rutas.ARG_ID_META) { type = NavType.StringType })
        ) { entrada ->
            val idMeta = entrada.arguments?.getString(Rutas.ARG_ID_META)
            val meta = uiState.metas.firstOrNull { it.id == idMeta }

            if (meta != null) {
                DepositDialog(
                    meta = meta,
                    uiState = uiState,
                    onMontoChange = viewModel::actualizarMontoAbono,
                    onConfirmar = { id, monto ->
                        viewModel.registrarAbono(id, monto) {
                            navController.popBackStack()
                        }
                    },
                    onCancelar = {
                        viewModel.limpiarMensajeError()
                        navController.popBackStack()
                    }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}