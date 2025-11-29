package com.example.actividad7.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    // ----- VARIABLES PARA TODOS LOS CAMPOS -----
    var descripcion by remember { mutableStateOf(ref["descripcion"]?.toString() ?: "") }
    var costo by remember { mutableStateOf(ref["costo"]?.toString() ?: "") }
    var area by remember { mutableStateOf(ref["area"]?.toString() ?: "") }
    var consumo by remember { mutableStateOf(ref["consumo"]?.toString() ?: "") }
    var frecuencia by remember { mutableStateOf(ref["frecuencia"]?.toString() ?: "") }
    var unidad by remember { mutableStateOf(ref["unidad"]?.toString() ?: "") }
    var cantidad by remember { mutableStateOf(ref["cantidad"]?.toString() ?: "") }
    var observacion by remember { mutableStateOf(ref["observacion"]?.toString() ?: "") }
    var numeroParte by remember { mutableStateOf(ref["numero_parte_proveedor"]?.toString() ?: "") }
    var marcaProveedor by remember { mutableStateOf(ref["marca_proveedor"]?.toString() ?: "") }
    var equiposAUsar by remember { mutableStateOf(ref["equipos_a_usar"]?.toString() ?: "") }
    var familia by remember { mutableStateOf(ref["familia"]?.toString() ?: "") }
    var reemplazable by remember { mutableStateOf(ref["reemplazable"]?.toString() ?: "") }
    var reduceVelocidad by remember { mutableStateOf(ref["reduce_velocidad"]?.toString() ?: "") }
    var existeRiesgo by remember { mutableStateOf(ref["existe_riesgo"]?.toString() ?: "") }
    var nacionalidad by remember { mutableStateOf(ref["nacionalidad"]?.toString() ?: "") }
    var paginaWeb by remember { mutableStateOf(ref["pagina_web"]?.toString() ?: "") }

    // ----- COLORES DE INPUT -----
    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
        cursorColor = Color.White
    )

    // ----- CONTENEDOR SCROLLEABLE -----
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Editar Refacción", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(Modifier.height(12.dp))

        // ---- FUNCIÓN PARA DIBUJAR CAMPOS ----
        @Composable
        fun Campo(label: String, valor: String, onChange: (String) -> Unit) {
            OutlinedTextField(
                value = valor,
                onValueChange = onChange,
                label = { Text(label) },
                colors = colors,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }

        // ---- LISTA COMPLETA DE CAMPOS ----
        Campo("Descripción", descripcion) { descripcion = it }
        Campo("Costo", costo) { costo = it }
        Campo("Área", area) { area = it }
        Campo("Consumo", consumo) { consumo = it }
        Campo("Frecuencia", frecuencia) { frecuencia = it }
        Campo("Unidad", unidad) { unidad = it }
        Campo("Cantidad", cantidad) { cantidad = it }
        Campo("Observación", observacion) { observacion = it }
        Campo("Número de parte", numeroParte) { numeroParte = it }
        Campo("Marca proveedor", marcaProveedor) { marcaProveedor = it }
        Campo("Equipos a usar", equiposAUsar) { equiposAUsar = it }
        Campo("Familia", familia) { familia = it }
        Campo("Reemplazable", reemplazable) { reemplazable = it }
        Campo("Reduce velocidad", reduceVelocidad) { reduceVelocidad = it }
        Campo("Existe riesgo", existeRiesgo) { existeRiesgo = it }
        Campo("Nacionalidad", nacionalidad) { nacionalidad = it }
        Campo("Página Web", paginaWeb) { paginaWeb = it }

        Spacer(Modifier.height(16.dp))

        // ----- BOTÓN GUARDAR -----
        Button(
            onClick = {
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
                    "pagina_web" to paginaWeb
                )

                db.actualizarRefaccion(ref["id"].toString(), datos)
                onVolver()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366), contentColor = Color.White)
        ) {
            Text("Guardar Cambios")
        }

        Spacer(Modifier.height(8.dp))

        // ----- BOTÓN CANCELAR -----
        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text("Cancelar")
        }
    }
}
