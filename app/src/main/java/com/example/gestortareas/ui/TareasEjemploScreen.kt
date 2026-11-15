package com.example.gestortareas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestortareas.model.TareaApi
import com.example.gestortareas.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasEjemploScreen(onVolver: () -> Unit) {
    val scope = rememberCoroutineScope()
    var tareasApi by remember { mutableStateOf<List<TareaApi>>(emptyList()) }
    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    fun cargarDesdeServidor() {
        scope.launch {
            cargando = true
            mensajeError = null
            try {
                tareasApi = RetrofitClient.api.obtenerTareasEjemplo(userId = 1)
            } catch (e: Exception) {
                mensajeError = "No se pudo conectar con el servidor. Revisa tu conexión a Internet."
            } finally {
                cargando = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = { Text("Tareas de ejemplo (API)") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Datos externos obtenidos desde JSONPlaceholder (no se guardan en tus tareas personales).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = { cargarDesdeServidor() },
                enabled = !cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cargar tareas de ejemplo")
            }

            when {
                cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                mensajeError != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = mensajeError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { cargarDesdeServidor() }) {
                            Text("Reintentar")
                        }
                    }
                }
                tareasApi.isNotEmpty() -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tareasApi, key = { it.id }) { item ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "#${item.id} - ${item.title}",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Estado: ${if (item.completed) "Completada" else "Pendiente"}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
