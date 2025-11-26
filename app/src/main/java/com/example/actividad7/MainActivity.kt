package com.example.actividad7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.actividad7.ui.theme.Actividad7Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Corrección: Aseguramos que el nombre coincida con tu archivo (DataBaseUtils o DatabaseUtils)
        // Si tu archivo se llama DataBaseUtils.kt, usa DataBaseUtils.
        // Si no tienes este archivo, avísame y te lo genero.
        try {
            DataBaseUtils.copiarBaseDatosSiNoExiste(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            Actividad7Theme {
                MainScreen()
            }
        }
    }
}