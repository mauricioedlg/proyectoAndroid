package com.example.actividad7.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.actividad7.DatabaseManager

@Composable
fun CorregirRefaccionesScreen(usuarioId: Int) {

    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }

    val lista = remember { mutableStateListOf<Map<String, Any?>>() }
    var seleccionada by remember { mutableStateOf<Map<String, Any?>?>(null) }

    fun recargarLista() {
        lista.clear()
        lista.addAll(dbManager.obtenerRefaccionesPorUsuario(usuarioId))
    }

    LaunchedEffect(Unit) {
        recargarLista()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        if (seleccionada == null) {

            Text("Selecciona para Corregir", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                items(lista) { ref ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { seleccionada = ref },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {

                        Column(Modifier.padding(16.dp)) {
                            Text("ID: ${ref["id"]}", color = Color.White)
                            Text("Descripción: ${ref["descripcion"]}", color = Color.White)
                        }
                    }
                }
            }

        } else {

            EditarRefaccionForm(
                ref = seleccionada!!,
                onVolver = {
                    seleccionada = null
                    recargarLista()
                }
            )
        }
    }
}
