package com.example.gestortareas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestortareas.model.Prioridad
import com.example.gestortareas.model.Tarea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioTareaScreen(
    tareaExistente: Tarea?,
    onGuardar: (titulo: String, descripcion: String, prioridad: String) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf(tareaExistente?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(tareaExistente?.descripcion ?: "") }
    var prioridadSeleccionada by rememberSaveable {
        mutableStateOf(tareaExistente?.prioridad ?: Prioridad.MEDIA.texto)
    }
    var menuDesplegado by remember { mutableStateOf(false) }
    var errorTitulo by rememberSaveable { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    val opcionesPrioridad = Prioridad.entries.map { it.texto }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (tareaExistente == null) "Nueva tarea" else "Editar tarea")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = {
                    titulo = it
                    errorTitulo = null
                },
                label = { Text("Título *") },
                isError = errorTitulo != null,
                supportingText = {
                    if (errorTitulo != null) {
                        Text(text = errorTitulo!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = menuDesplegado,
                onExpandedChange = { menuDesplegado = !menuDesplegado }
            ) {
                OutlinedTextField(
                    value = prioridadSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Prioridad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuDesplegado) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuDesplegado,
                    onDismissRequest = { menuDesplegado = false }
                ) {
                    opcionesPrioridad.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                prioridadSeleccionada = opcion
                                menuDesplegado = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    enabled = !guardando,
                    onClick = {
                        val tituloLimpio = titulo.trim()
                        when {
                            tituloLimpio.isEmpty() -> {
                                errorTitulo = "El título no puede estar vacío."
                            }
                            tituloLimpio.length < 3 -> {
                                errorTitulo = "El título debe tener al menos 3 caracteres."
                            }
                            prioridadSeleccionada !in opcionesPrioridad -> {
                                errorTitulo = "Selecciona una prioridad válida."
                            }
                            else -> {
                                guardando = true
                                onGuardar(tituloLimpio, descripcion.trim(), prioridadSeleccionada)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
