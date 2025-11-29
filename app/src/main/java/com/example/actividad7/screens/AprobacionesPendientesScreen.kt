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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actividad7.DatabaseManager

@Composable
fun AprobacionesPendientesScreen() {
    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }

    val pendientes = remember { mutableStateListOf<Map<String, Any?>>() }
    var seleccionado by remember { mutableStateOf<Map<String, Any?>?>(null) }

    fun cargarDatos() {
        pendientes.clear()
        pendientes.addAll(dbManager.obtenerPendientesAprobacion())
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    val ColorAprobar = Color(0xFF4CAF50)
    val ColorRechazar = Color(0xFFF44336)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        if (seleccionado == null) {
            // LISTA
            Text("Aprobaciones Pendientes", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(Modifier.height(16.dp))

            if (pendientes.isEmpty()) {
                Text("No hay solicitudes pendientes.", color = Color.White)
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
                                Text("Solicitante ID: ${item["usuario_id"]}", fontSize = MaterialTheme.typography.bodySmall.fontSize, color = Color.White.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        } else {
            // DETALLE
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Text("Detalle de Aprobación", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(Modifier.height(16.dp))

                DetalleTexto("ID:", seleccionado!!["id"].toString())
                DetalleTexto("No. Parte:", seleccionado!!["numero_parte_proveedor"].toString())
                DetalleTexto("Descripción:", seleccionado!!["descripcion"].toString())
                DetalleTexto("Costo:", "$ ${seleccionado!!["costo"]}")
                DetalleTexto("Área:", seleccionado!!["area"].toString())
                DetalleTexto("Cantidad:", seleccionado!!["cantidad"].toString())
                DetalleTexto("Observación:", seleccionado!!["observacion"].toString())
                DetalleTexto("Familia:", seleccionado!!["familia"].toString())

                Spacer(Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

                    Button(
                        onClick = {
                            val id = seleccionado!!["id"].toString().toInt()
                            if (dbManager.actualizarEstadoAprobacion(id, "NO")) {
                                Toast.makeText(context, "Registro RECHAZADO", Toast.LENGTH_SHORT).show()
                                cargarDatos()
                                seleccionado = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorRechazar)
                    ) { Text("RECHAZAR") }

                    Button(
                        onClick = {
                            val id = seleccionado!!["id"].toString().toInt()
                            if (dbManager.actualizarEstadoAprobacion(id, "SI")) {
                                Toast.makeText(context, "Registro APROBADO", Toast.LENGTH_SHORT).show()
                                cargarDatos()
                                seleccionado = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorAprobar)
                    ) { Text("APROBAR") }
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = { seleccionado = null }, modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)) {
                    Text("Volver a la lista", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DetalleTexto(label: String, value: String) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
        Text(if (value == "null") "-" else value, color = Color.White, style = MaterialTheme.typography.bodyLarge)
        Divider(color = Color.White.copy(alpha = 0.2f))
    }
}