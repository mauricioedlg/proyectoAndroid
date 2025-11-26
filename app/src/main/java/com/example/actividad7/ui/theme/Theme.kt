package com.example.actividad7.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Forzamos el esquema de colores para que SIEMPRE sea fondo morado y texto blanco
// independientemente del modo claro/oscuro del sistema.

private val AppColors = darkColorScheme(
    primary = BlancoTexto,
    onPrimary = MoradoFondo,
    primaryContainer = MoradoClaro,
    onPrimaryContainer = BlancoTexto,
    secondary = MoradoClaro,
    onSecondary = BlancoTexto,
    background = MoradoFondo,
    onBackground = BlancoTexto,
    surface = MoradoFondo, // Las tarjetas también serán moradas o ligeramente distintas si prefieres
    onSurface = BlancoTexto,
    error = ErrorRojo,
    onError = Color.Black
)

@Composable
fun Actividad7Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Usamos siempre AppColors para garantizar el diseño morado/blanco
    MaterialTheme(
        colorScheme = AppColors,
        typography = Typography, // Asegúrate de que tu Type.kt use colores por defecto o tintados
        content = content
    )
}