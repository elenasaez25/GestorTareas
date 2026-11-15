package com.example.gestortareas.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.gestortareas.R
import com.example.gestortareas.model.Tarea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTareasScreen(
    tareas: List<Tarea>,
    soloPendientes: Boolean,
    onCambiarFiltro: (Boolean) -> Unit,
    onNuevaTarea: () -> Unit,
    onEditarTarea: (Tarea) -> Unit,
    onCambiarCompletada: (Tarea) -> Unit,
    onEliminarTarea: (Tarea) -> Unit,
    onIrATareasEjemplo: () -> Unit
) {
    val context = LocalContext.current
    var tareaAEliminar by remember { mutableStateOf<Tarea?>(null) }

    val tareasFiltradas = if (soloPendientes) {
        tareas.filter { !it.completada }
    } else {
        tareas
    }

    if (tareaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { tareaAEliminar = null },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Seguro que deseas eliminar «${tareaAEliminar?.titulo}»?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        tareaAEliminar?.let { onEliminarTarea(it) }
                        tareaAEliminar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { tareaAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tarea),
                        contentDescription = "Icono de la aplicación",
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = { Text("Mis Tareas") },
                actions = {
                    TextButton(onClick = onIrATareasEjemplo) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Tareas de ejemplo")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaTarea) {
                Icon(Icons.Default.Add, contentDescription = "Añadir tarea")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mostrar solo pendientes", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = soloPendientes,
                    onCheckedChange = onCambiarFiltro
                )
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            if (tareasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (soloPendientes && tareas.isNotEmpty()) {
                            "¡Genial! No tienes tareas pendientes."
                        } else {
                            "No hay tareas registradas. Pulsa + para crear una."
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(tareasFiltradas, key = { it.id }) { tarea ->
                        TarjetaTarea(
                            tarea = tarea,
                            onToggleCompletada = { onCambiarCompletada(tarea) },
                            onEditar = { onEditarTarea(tarea) },
                            onEliminar = { tareaAEliminar = tarea },
                            onCompartir = { compartirTarea(context, tarea) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaTarea(
    tarea: Tarea,
    onToggleCompletada: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onCompartir: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = { onToggleCompletada() }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                )
                if (tarea.descripcion.isNotBlank()) {
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prioridad: ${tarea.prioridad} • ${if (tarea.completada) "Completada" else "Pendiente"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onCompartir) {
                Icon(Icons.Default.Share, contentDescription = "Compartir")
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun compartirTarea(context: Context, tarea: Tarea) {
    val textoCompartir = buildString {
        appendLine("Tarea: ${tarea.titulo}")
        if (tarea.descripcion.isNotBlank()) {
            appendLine("Descripción: ${tarea.descripcion}")
        }
        append("Prioridad: ${tarea.prioridad}")
    }

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Tarea: ${tarea.titulo}")
        putExtra(Intent.EXTRA_TEXT, textoCompartir)
    }

    val chooser = Intent.createChooser(sendIntent, "Compartir tarea mediante")
    try {
        context.startActivity(chooser)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "No hay aplicaciones instaladas para compartir texto.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
