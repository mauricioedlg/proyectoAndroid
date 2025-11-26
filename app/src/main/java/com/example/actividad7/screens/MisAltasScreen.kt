package com.example.actividad7.screens

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
fun MisAltasScreen() {
    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }
    val refacciones = remember { mutableStateListOf<Map<String, Any?>>() }

    LaunchedEffect(Unit) {
        val datos = dbManager.obtenerRefacciones()
        refacciones.clear()
        refacciones.addAll(datos)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Mis Altas Registradas", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(Modifier.height(8.dp))

        if (refacciones.isEmpty()) {
            Text("No hay refacciones registradas aún.", color = Color.White)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(refacciones) { ref ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Descripción: ${ref["descripcion"] ?: "N/A"}", color = Color.White)
                            Text("Costo: ${ref["costo"] ?: "N/A"}", color = Color.White)
                            Text("No. Parte: ${ref["numero_parte_proveedor"] ?: "N/A"}", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
