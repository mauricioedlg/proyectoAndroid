package com.example.actividad7.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    // Configuración de colores
    val textColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = Color.White
    )

    val AzulMarino = Color(0xFF003366)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Registrar Refacción", style = MaterialTheme.typography.titleLarge, color = Color.White) }

        // Campos
        item { OutlinedTextField(value = numeroParte, onValueChange = { numeroParte = it }, label = { Text("Número parte (Obligatorio)") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = costo, onValueChange = { costo = it }, label = { Text("Costo") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text("Cantidad") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("Área") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = familia, onValueChange = { familia = it }, label = { Text("Familia") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = consumo, onValueChange = { consumo = it }, label = { Text("Consumo") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = frecuencia, onValueChange = { frecuencia = it }, label = { Text("Frecuencia") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = unidad, onValueChange = { unidad = it }, label = { Text("Unidad") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = observacion, onValueChange = { observacion = it }, label = { Text("Observación") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = marcaProveedor, onValueChange = { marcaProveedor = it }, label = { Text("Marca Proveedor") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = equiposAUsar, onValueChange = { equiposAUsar = it }, label = { Text("Equipos a Usar") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = reemplazable, onValueChange = { reemplazable = it }, label = { Text("Reemplazable") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = reduceVelocidad, onValueChange = { reduceVelocidad = it }, label = { Text("Reduce Velocidad") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = existeRiesgo, onValueChange = { existeRiesgo = it }, label = { Text("Existe Riesgo") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = nacionalidad, onValueChange = { nacionalidad = it }, label = { Text("Nacionalidad") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = paginaWeb, onValueChange = { paginaWeb = it }, label = { Text("Página Web") }, colors = textColors, modifier = Modifier.fillMaxWidth()) }

        // Botón Enviar
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
                        // "foto" to imagenSeleccionada, // 🔥 ELIMINADO
                        "usuario_id" to usuarioId
                    )

                    val result = dbManager.insertarRefaccion(datos)

                    if (result != -1L) {
                        Toast.makeText(context, "Guardado con éxito", Toast.LENGTH_SHORT).show()
                        // Limpiar campos
                        descripcion = ""; numeroParte = ""; cantidad = "";
                        costo = ""; area = ""; consumo = ""; frecuencia = ""; unidad = ""; observacion = ""; marcaProveedor = ""; equiposAUsar = ""; familia = ""; reemplazable = ""; reduceVelocidad = ""; existeRiesgo = ""; nacionalidad = ""; paginaWeb = ""
                    } else {
                        Toast.makeText(context, "Error al guardar. Verifique si el No. Parte ya existe.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AzulMarino, contentColor = Color.White)
            ) {
                Text("ENVIAR")
            }
        }
    }
}