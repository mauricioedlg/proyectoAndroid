package com.example.actividad7.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

@Composable
fun TablaHeader(texto: String, ancho: Int) {
    Text(
        text = texto,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier
            .width(ancho.dp)
            .padding(6.dp)
    )
}

@Composable
fun TablaCell(texto: String?, ancho: Int) {
    Text(
        text = texto ?: "",
        color = Color.White,
        fontSize = 12.sp,
        modifier = Modifier
            .width(ancho.dp)
            .padding(6.dp)
    )
}
