package com.example.gestortareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.example.gestortareas.database.AppDatabase
import com.example.gestortareas.model.Tarea
import com.example.gestortareas.preferences.PreferenciasDataStore
import com.example.gestortareas.ui.FormularioTareaScreen
import com.example.gestortareas.ui.ListaTareasScreen
import com.example.gestortareas.ui.TareasEjemploScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    GestorTareasApp()
                }
            }
        }
    }
}

@Composable
fun GestorTareasApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = remember { AppDatabase.obtenerBaseDatos(context) }
    val tareaDao = remember { db.tareaDao() }
    val preferencias = remember { PreferenciasDataStore(context) }

    val listaTareas by tareaDao.obtenerTodas().collectAsState(initial = emptyList())
    val soloPendientes by preferencias.soloPendientesFlow.collectAsState(initial = false)

    var pantallaActual by rememberSaveable { mutableStateOf("lista") }
    var tareaEnEdicionId by rememberSaveable { mutableStateOf<Int?>(null) }

    when (pantallaActual) {
        "lista" -> {
            ListaTareasScreen(
                tareas = listaTareas,
                soloPendientes = soloPendientes,
                onCambiarFiltro = { nuevoValor ->
                    scope.launch { preferencias.guardarSoloPendientes(nuevoValor) }
                },
                onNuevaTarea = {
                    tareaEnEdicionId = null
                    pantallaActual = "formulario"
                },
                onEditarTarea = { tarea ->
                    tareaEnEdicionId = tarea.id
                    pantallaActual = "formulario"
                },
                onCambiarCompletada = { tarea ->
                    scope.launch {
                        tareaDao.actualizar(tarea.copy(completada = !tarea.completada))
                    }
                },
                onEliminarTarea = { tarea ->
                    scope.launch { tareaDao.eliminar(tarea) }
                },
                onIrATareasEjemplo = {
                    pantallaActual = "api"
                }
            )
        }

        "formulario" -> {
            val tareaAEditar = listaTareas.find { it.id == tareaEnEdicionId }
            FormularioTareaScreen(
                tareaExistente = tareaAEditar,
                onGuardar = { titulo, descripcion, prioridad ->
                    scope.launch {
                        if (tareaAEditar == null) {
                            tareaDao.insertar(
                                Tarea(
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    prioridad = prioridad
                                )
                            )
                        } else {
                            tareaDao.actualizar(
                                tareaAEditar.copy(
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    prioridad = prioridad
                                )
                            )
                        }
                        pantallaActual = "lista"
                    }
                },
                onCancelar = {
                    pantallaActual = "lista"
                }
            )
        }

        "api" -> {
            TareasEjemploScreen(
                onVolver = { pantallaActual = "lista" }
            )
        }
    }
}
