package com.example.actividad7.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.actividad7.DatabaseManager

@Composable
fun EditarRefaccionForm(ref: Map<String, Any?>, onVolver: () -> Unit) {
    val context = LocalContext.current
    val db = remember { DatabaseManager(context) }

    var descripcion by remember { mutableStateOf(ref["descripcion"]?.toString() ?: "") }
    var area by remember { mutableStateOf(ref["area"]?.toString() ?: "") }
    var observacion by remember { mutableStateOf(ref["observacion"]?.toString() ?: "") }

    // Definir colores para TextField que sean visibles sobre fondo morado
    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White,
        focusedLabelColor = Color.White.copy(alpha = 0.7f),
        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
        cursorColor = Color.White
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Editar Refacción", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(Modifier.height(8.dp))

        // Usar OutlinedTextField con los colores definidos
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            colors = colors,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = area,
            onValueChange = { area = it },
            label = { Text("Área") },
            colors = colors,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = observacion,
            onValueChange = { observacion = it },
            label = { Text("Observación") },
            colors = colors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            val datos = mapOf(
                "descripcion" to descripcion,
                "area" to area,
                "observacion" to observacion
            )
            // Asumiendo que DatabaseManager.kt tiene un método actualizarRefaccion
            db.actualizarRefaccion(ref["id"].toString(), datos)
            onVolver()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar Cambios")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar")
        }
    }
}