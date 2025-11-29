package com.example.actividad7.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actividad7.DatabaseManager

@Composable
fun CotizacionesPendientesScreen() {
    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }

    val pendientes = remember { mutableStateListOf<Map<String, Any?>>() }
    var seleccionado by remember { mutableStateOf<Map<String, Any?>?>(null) }

    fun cargarDatos() {
        pendientes.clear()
        pendientes.addAll(dbManager.obtenerPendientesCotizacion())
    }

    LaunchedEffect(Unit) { cargarDatos() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        if (seleccionado == null) {
            // === LISTA ===
            Text("Cotizaciones Pendientes", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(Modifier.height(16.dp))

            if (pendientes.isEmpty()) {
                Text("No hay refacciones pendientes de cotizar.", color = Color.White)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(pendientes) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { seleccionado = item },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("No. Parte: ${item["numero_parte_proveedor"]}", fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Descripción: ${item["descripcion"]}", color = Color.White)
                                Text("MFG: ${item["numero_mfg"]}", fontSize = MaterialTheme.typography.bodySmall.fontSize, color = Color.Green)
                            }
                        }
                    }
                }
            }
        } else {
            // === DETALLE ===
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Text("Confirmar Cotización", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(Modifier.height(16.dp))

                DetalleTexto("Descripción:", seleccionado!!["descripcion"].toString())
                DetalleTexto("No. Parte:", seleccionado!!["numero_parte_proveedor"].toString())
                DetalleTexto("MFG Asignado:", seleccionado!!["numero_mfg"].toString())

                Spacer(Modifier.height(24.dp))

                Text("¿Cotizar refacción?", style = MaterialTheme.typography.headlineSmall, color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))

                Spacer(Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    // BOTÓN NO (RECHAZAR)
                    Button(
                        onClick = {
                            val id = seleccionado!!["id"].toString().toInt()
                            if (dbManager.actualizarCotizacion(id, "NO")) { // "NO" = Rechazado en cotización
                                Toast.makeText(context, "Solicitud Rechazada", Toast.LENGTH_SHORT).show()
                                cargarDatos()
                                seleccionado = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) { Text("NO") }

                    // BOTÓN SI (CONCLUIR)
                    Button(
                        onClick = {
                            val id = seleccionado!!["id"].toString().toInt()
                            if (dbManager.actualizarCotizacion(id, "SI")) { // "SI" = Cotizado/Concluido
                                Toast.makeText(context, "Solicitud Concluida Exitosamente", Toast.LENGTH_SHORT).show()
                                cargarDatos()
                                seleccionado = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) { Text("SI") }
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = { seleccionado = null }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Volver", color = Color.White)
                }
            }
        }
    }
}