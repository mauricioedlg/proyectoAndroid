package com.example.actividad7.screens

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.actividad7.DatabaseManager

@Composable
fun FormularioScreen(usuarioId: Int) { // Recibe el ID del usuario logueado
    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }

    // Campos del formulario
    var descripcion by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var consumo by remember { mutableStateOf("") }
    var frecuencia by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }
    var numeroParte by remember { mutableStateOf("") }
    var marcaProveedor by remember { mutableStateOf("") }
    var equiposAUsar by remember { mutableStateOf("") }
    var familia by remember { mutableStateOf("") }
    var reemplazable by remember { mutableStateOf("") }
    var reduceVelocidad by remember { mutableStateOf("") }
    var existeRiesgo by remember { mutableStateOf("") }
    var nacionalidad by remember { mutableStateOf("") }
    var paginaWeb by remember { mutableStateOf("") }

    var imagenSeleccionada by remember { mutableStateOf<ByteArray?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            imagenSeleccionada = inputStream?.readBytes()
        }
    }

    // Configuración de colores para los campos de texto (Blanco sobre Morado)
    val textColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = Color.White
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Registrar Refacción", style = MaterialTheme.typography.titleLarge, color = Color.White) }

        item { OutlinedTextField(value = numeroParte, onValueChange = { numeroParte = it }, label = { Text("Número parte (Obligatorio)") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = costo, onValueChange = { costo = it }, label = { Text("Costo") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("Área") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text("Cantidad") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }

        // ... (Puedes agregar el resto de campos si son necesarios, simplificado para brevedad visual)

        item {
            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Seleccionar foto")
            }
        }

        item {
            imagenSeleccionada?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                Image(
                    bitmap.asImageBitmap(),
                    contentDescription = "Foto seleccionada",
                    modifier = Modifier.size(150.dp)
                )
            }
        }

        item {
            Button(
                onClick = {
                    if (numeroParte.isBlank()) {
                        Toast.makeText(context, "El número de parte es obligatorio", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val datos = mapOf(
                        "descripcion" to descripcion,
                        "costo" to costo.toDoubleOrNull(),
                        "area" to area,
                        "consumo" to consumo,
                        "frecuencia" to frecuencia,
                        "unidad" to unidad,
                        "cantidad" to cantidad.toIntOrNull(),
                        "observacion" to observacion,
                        "numero_parte_proveedor" to numeroParte,
                        "marca_proveedor" to marcaProveedor,
                        "equipos_a_usar" to equiposAUsar,
                        "familia" to familia,
                        "reemplazable" to reemplazable,
                        "reduce_velocidad" to reduceVelocidad,
                        "existe_riesgo" to existeRiesgo,
                        "nacionalidad" to nacionalidad,
                        "pagina_web" to paginaWeb,
                        "foto" to imagenSeleccionada,
                        "usuario_id" to usuarioId // ✅ Aquí vinculamos al usuario
                    )

                    val result = dbManager.insertarRefaccion(datos)

                    if (result != -1L) {
                        Toast.makeText(context, "Guardado con éxito", Toast.LENGTH_SHORT).show()
                        // Limpiar campos críticos
                        descripcion = ""; numeroParte = ""; cantidad = ""; imagenSeleccionada = null
                    } else {
                        Toast.makeText(context, "Error al guardar (revise log)", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("GUARDAR REFACCIÓN")
            }
        }
    }
}