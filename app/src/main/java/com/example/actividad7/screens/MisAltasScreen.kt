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
fun MisAltasScreen(usuarioId: Int) {
    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }
    val registros = remember { mutableStateListOf<Map<String, Any?>>() }

    LaunchedEffect(Unit) {
        val datos = dbManager.obtenerRefaccionesPorUsuario(usuarioId)
        registros.clear()
        registros.addAll(datos)
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Mis Altas (Tabla Completa)", color = Color.White, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))

        if (registros.isEmpty()) {
            Text("No hay registros.", color = Color.White)
        } else {
            Row(Modifier.horizontalScroll(rememberScrollState()).fillMaxWidth()) {
                Column {
                    Row(Modifier.background(Color(0xFF003366))) {
                        TablaHeader("ID", 80)
                        TablaHeader("Aprob. MTTO", 120)
                        TablaHeader("Aprob. PLANTA", 120)
                        TablaHeader("No. MFG", 120) // 🔥 NUEVA COLUMNA
                        TablaHeader("Descripción", 200)
                        TablaHeader("Costo", 120)
                        TablaHeader("Área", 140)
                        TablaHeader("No. Parte", 150)
                    }
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        registros.forEach { ref ->
                            Row(Modifier.background(Color(0x22003366))) {
                                TablaCell(ref["id"]?.toString(), 80)
                                TablaCell(ref["aprobacion_mtto"]?.toString(), 120)
                                TablaCell(ref["aprobacion_planta"]?.toString(), 120)
                                TablaCell(ref["numero_mfg"]?.toString(), 120) // 🔥 NUEVA CELDA
                                TablaCell(ref["descripcion"]?.toString(), 200)
                                TablaCell(ref["costo"]?.toString(), 120)
                                TablaCell(ref["area"]?.toString(), 140)
                                TablaCell(ref["numero_parte_proveedor"]?.toString(), 150)
                            }
                            Divider(color = Color.White.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }
    }
}