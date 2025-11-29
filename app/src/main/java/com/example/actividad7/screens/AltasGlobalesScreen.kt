package com.example.actividad7.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.actividad7.DatabaseManager
import com.example.actividad7.ui.TablaHeader
import com.example.actividad7.ui.TablaCell

@Composable
fun AltasGlobalesScreen() {

    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }

    val registros = remember { mutableStateListOf<Map<String, Any?>>() }

    LaunchedEffect(Unit) {
        val datos = dbManager.obtenerRefacciones()
        registros.clear()
        registros.addAll(datos)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        Text("Altas Globales (Tabla Completa)", color = Color.White, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))

        if (registros.isEmpty()) {

            Text("No hay registros.", color = Color.White)

        } else {

            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {

                Column {

                    // ------------------ ENCABEZADOS -------------------
                    Row(Modifier.background(Color(0xFF003366))) {
                        TablaHeader("ID", 80)
                        TablaHeader("Descripción", 200)
                        TablaHeader("Costo", 120)
                        TablaHeader("Área", 140)
                        TablaHeader("Consumo", 140)
                        TablaHeader("Frecuencia", 140)
                        TablaHeader("Unidad", 120)
                        TablaHeader("Cantidad", 120)
                        TablaHeader("Observación", 200)
                        TablaHeader("No. Parte", 150)
                        TablaHeader("Marca", 140)
                        TablaHeader("Equipos Usar", 180)
                        TablaHeader("Familia", 140)
                        TablaHeader("Reemplazable", 140)
                        TablaHeader("Reduce Velocidad", 180)
                        TablaHeader("Existe Riesgo", 150)
                        TablaHeader("Nacionalidad", 150)
                        TablaHeader("Página Web", 200)
                    }

                    // ------------------ FILAS -------------------
                    Column(Modifier.verticalScroll(rememberScrollState())) {

                        registros.forEach { ref ->

                            Row(Modifier.background(Color(0x22003366))) {

                                TablaCell(ref["id"]?.toString(), 80)
                                TablaCell(ref["descripcion"]?.toString(), 200)
                                TablaCell(ref["costo"]?.toString(), 120)
                                TablaCell(ref["area"]?.toString(), 140)
                                TablaCell(ref["consumo"]?.toString(), 140)
                                TablaCell(ref["frecuencia"]?.toString(), 140)
                                TablaCell(ref["unidad"]?.toString(), 120)
                                TablaCell(ref["cantidad"]?.toString(), 120)
                                TablaCell(ref["observacion"]?.toString(), 200)
                                TablaCell(ref["numero_parte_proveedor"]?.toString(), 150)
                                TablaCell(ref["marca_proveedor"]?.toString(), 140)
                                TablaCell(ref["equipos_a_usar"]?.toString(), 180)
                                TablaCell(ref["familia"]?.toString(), 140)
                                TablaCell(ref["reemplazable"]?.toString(), 140)
                                TablaCell(ref["reduce_velocidad"]?.toString(), 180)
                                TablaCell(ref["existe_riesgo"]?.toString(), 150)
                                TablaCell(ref["nacionalidad"]?.toString(), 150)
                                TablaCell(ref["pagina_web"]?.toString(), 200)
                            }

                            Divider(color = Color.White.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }
    }
}
