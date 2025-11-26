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
fun AltasGlobalesScreen() {
    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }
    val listaGlobal = remember { mutableStateListOf<Map<String, Any?>>() }

    LaunchedEffect(Unit) {
        val datos = dbManager.obtenerRefacciones()
        listaGlobal.clear()
        listaGlobal.addAll(datos)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Altas Globales", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(Modifier.height(8.dp))

        if (listaGlobal.isEmpty()) {
            Text("No existen registros en la base de datos.", color = Color.White)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listaGlobal) { ref ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Familia: ${ref["familia"] ?: "N/A"}", color = Color.White)
                            Text("Área: ${ref["area"] ?: "N/A"}", color = Color.White)
                            Text("Consumo: ${ref["consumo"] ?: "N/A"}", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}