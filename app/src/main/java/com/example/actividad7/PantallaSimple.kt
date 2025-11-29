package com.example.actividad7.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaSimple(
    nombreUsuario: String,
    cantidadNotificaciones: Int, // 🔥 ESTE PARÁMETRO FALTABA
    onNotificationClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally
    ) {
        // Fila para el título "Hola" y el icono con badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "¡Hola $nombreUsuario!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 🔥 CONTENEDOR DEL ICONO + BADGE ROJO
            Box(
                modifier = Modifier
                    .size(40.dp) // Tamaño del área clicable
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                // 1. El icono de la campana
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                // 2. El globito rojo (Badge)
                // Solo se muestra si hay notificaciones (> 0)
                if (cantidadNotificaciones > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd) // Esquina superior derecha
                            .size(18.dp) // Tamaño del círculo rojo
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cantidadNotificaciones.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bienvenido al app de refacciones",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}
