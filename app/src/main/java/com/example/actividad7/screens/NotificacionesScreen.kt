package com.example.actividad7.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
fun NotificacionesScreen(
    usuarioId: Int,
    userRole: Int,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }

    val notificaciones = remember { mutableStateListOf<Map<String, Any>>() }

    // Función para cargar/recargar
    fun cargar() {
        notificaciones.clear()
        notificaciones.addAll(dbManager.obtenerNotificaciones(usuarioId))
        // Al entrar, marcamos como leídas visualmente (quita el globito del menú principal)
        dbManager.marcarNotificacionesComoLeidas(usuarioId)
    }

    LaunchedEffect(Unit) { cargar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Notificaciones", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (notificaciones.isEmpty()) {
            Text("No tienes notificaciones nuevas.", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(top = 16.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(notificaciones) { notif ->
                    val mensaje = notif["mensaje"] as String
                    val notifId = notif["id"] as Int
                    val esRechazo = mensaje.contains("RECHAZADA")

                    NotificationCard(
                        mensaje = mensaje,
                        esRechazo = esRechazo,
                        onClick = {
                            if (userRole == 1 || userRole == 0) {
                                // ES GERENTE/ADMIN:
                                // Navega a la pantalla de aprobación.
                                // La notificación NO se borra aquí, se borrará sola cuando el gerente apruebe/rechace.
                                onNavigate("Aprobaciones pendientes")
                            } else {
                                // ES USUARIO NORMAL:
                                // Borra la notificación al instante porque ya la vio.
                                dbManager.eliminarNotificacion(notifId)
                                // Navega a sus altas.
                                onNavigate("Mis Altas")
                                cargar() // Refrescar lista localmente
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(mensaje: String, esRechazo: Boolean, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (esRechazo) Color(0xFFB00020).copy(alpha = 0.8f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(10.dp),
                shape = MaterialTheme.shapes.small,
                color = if (esRechazo) Color.White else MaterialTheme.colorScheme.primary
            ) {}

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = if (esRechazo) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}