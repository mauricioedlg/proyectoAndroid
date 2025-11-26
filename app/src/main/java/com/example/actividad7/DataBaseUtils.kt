package com.example.actividad7

import android.content.Context
import java.io.FileOutputStream

object DataBaseUtils {
    fun copiarBaseDatosSiNoExiste(context: Context) {
        val dbFile = context.getDatabasePath("proyectoAndroid.db")

        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()
            try {
                // Asegúrate de que "proyectoAndroid.db" esté en la carpeta src/main/assets/
                context.assets.open("proyectoAndroid.db").use { input ->
                    FileOutputStream(dbFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}