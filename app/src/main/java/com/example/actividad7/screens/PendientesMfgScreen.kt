package com.example.actividad7.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.actividad7.DatabaseManager

@Composable
fun PendientesMfgScreen() {
    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }

    val pendientes = remember { mutableStateListOf<Map<String, Any?>>() }
    var seleccionado by remember { mutableStateOf<Map<String, Any?>?>(null) }

    var nuevoMfg by remember { mutableStateOf("") }

    fun cargarDatos() {
        pendientes.clear()
        pendientes.addAll(dbManager.obtenerPendientesMfg())
    }

    LaunchedEffect(Unit) { cargarDatos() }

    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = Color.White
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        if (seleccionado == null) {
            // === VISTA DE LISTA ===
            Text("Asignar Número MFG", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Text("Registros aprobados pendientes de MFG", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            if (pendientes.isEmpty()) {
                Text("No hay registros pendientes de asignación.", color = Color.White)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(pendientes) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    seleccionado = item
                                    nuevoMfg = ""
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("No. Parte: ${item["numero_parte_proveedor"]}", fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Descripción: ${item["descripcion"]}", color = Color.White)
                                Text("ID: ${item["id"]}", fontSize = MaterialTheme.typography.bodySmall.fontSize, color = Color.White.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        } else {
            // === VISTA DE FORMULARIO ===
            Column(modifier = Modifier.fillMaxSize()) {
                Text("Asignar MFG", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(Modifier.height(16.dp))

                DetalleTexto("Descripción:", seleccionado!!["descripcion"].toString())
                DetalleTexto("No. Parte Proveedor:", seleccionado!!["numero_parte_proveedor"].toString())

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = nuevoMfg,
                    onValueChange = { nuevoMfg = it },
                    label = { Text("Número MFG (Nuevo)") },
                    colors = colors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // 🔥 BOTÓN BLANCO
                Button(
                    onClick = {
                        if (nuevoMfg.isBlank()) {
                            Toast.makeText(context, "Debes ingresar el número MFG", Toast.LENGTH_SHORT).show()
                        } else {
                            val id = seleccionado!!["id"].toString().toInt()
                            if (dbManager.asignarNumeroMfg(id, nuevoMfg)) {
                                Toast.makeText(context, "MFG Asignado y Usuario Notificado", Toast.LENGTH_LONG).show()
                                cargarDatos()
                                seleccionado = null
                            } else {
                                Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    // 🔥 Color Blanco de fondo, texto azul oscuro (primary del tema o 0xFF003366)
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF003366)
                    )
                ) {
                    Text("GUARDAR Y FINALIZAR")
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = { seleccionado = null }, modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)) {
                    Text("Cancelar / Volver", color = Color.White)
                }
            }
        }
    }
}