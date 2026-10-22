package com.example.saveapp.screen


import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.saveapp.R
import com.example.saveapp.data.model.CategoriaMeta
import com.example.saveapp.data.model.EstadoMeta

@Composable
fun ChipSeleccionable(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(texto) },
        modifier = modifier,
        leadingIcon = if (seleccionado) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}

@Composable
fun CategoriaMeta.etiqueta(): String = stringResource(
    when (this) {
        CategoriaMeta.CORTO_PLAZO -> R.string.filter_short
        CategoriaMeta.MEDIANO_PLAZO -> R.string.filter_medium
        CategoriaMeta.LARGO_PLAZO -> R.string.filter_long
    }
)

@Composable
fun EstadoMeta.etiqueta(): String = stringResource(
    when (this) {
        EstadoMeta.EN_PROGRESO -> R.string.goal_status_progress
        EstadoMeta.CASI_COMPLETADA -> R.string.goal_status_almost
        EstadoMeta.COMPLETADA -> R.string.goal_status_completed
    }
)