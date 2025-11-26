package com.example.actividad7

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

data class Usuario(
    val id: Int,
    val nombre: String,
    val username: String
)

class DatabaseManager(context: Context) :
// NOTA: Usamos 'proyectoAndroid.db' para el archivo de la base de datos real
// La versión la dejamos en 3 para que coincida con DBHelper si lo usas.
    SQLiteOpenHelper(context, "proyectoAndroid.db", null, 3) {

    // NOTA: Estas funciones on* ya no son críticas si usas DBHelper y DataBaseUtils para copiar el archivo .db
    // Pero se mantienen para la estructura de SQLiteOpenHelper.

    // 💡 IMPORTANTE: Si estás usando DataBaseUtils para copiar un archivo .db,
    // estas funciones onCreate y onUpgrade deben ser las de DBHelper.kt.
    // En tu proyecto, estás usando un DatabaseManager para la lógica y un DBHelper para la creación/copia.
    // Vamos a mover la lógica de acceso a datos aquí y quitar las funciones de creación de DB para evitar conflictos
    // con el archivo .db que se copia (proyectoAndroid.db) por DataBaseUtils.

    // Dejamos las on* mínimas, la lógica de creación real debe estar en DBHelper.kt
    override fun onCreate(db: SQLiteDatabase) {
        // No hacer nada, se asume que la base de datos se copia de assets/proyectoAndroid.db
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // No hacer nada, la lógica de upgrade la maneja el DBHelper si se necesita
    }

    // VALIDAR LOGIN POR USERNAME (Ya estaba correcta, pero ajustamos a la tabla 'usuarios' real)
    fun validarUsuarioPorUsername(username: String, password: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT UsuarioID, Nombre, Username FROM usuarios WHERE Username = ? AND Contrasena = ?",
            arrayOf(username, password)
        )

        return if (cursor.moveToFirst()) {
            val user = Usuario(
                id = cursor.getInt(0),
                nombre = cursor.getString(1),
                username = cursor.getString(2)
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    // ============================================================
    //  NUEVAS FUNCIONES PARA REFACCIONES
    // ============================================================

    // 1. INSERTAR REFACCIÓN (Para FormularioScreen.kt)
    fun insertarRefaccion(datos: Map<String, Any?>): Long {
        val db = writableDatabase
        val values = ContentValues()

        // Mapear los datos de Map<String, Any?> a ContentValues
        datos.forEach { (key, value) ->
            when (value) {
                is String -> values.put(key, value)
                is Int -> values.put(key, value)
                is Double -> values.put(key, value)
                is ByteArray -> values.put(key, value)
                null -> values.putNull(key)
                else -> Log.e("DBManager", "Tipo de dato no soportado para key: $key")
            }
        }

        return try {
            db.insertOrThrow("refacciones", null, values)
        } catch (e: Exception) {
            Log.e("DBManager", "Error al insertar refacción: ${e.message}")
            -1L
        }
    }

    // 2. ACTUALIZAR REFACCIÓN (Para EditarRefaccionForm.kt)
    fun actualizarRefaccion(id: String, datos: Map<String, Any?>): Boolean {
        val db = writableDatabase
        val values = ContentValues()

        datos.forEach { (key, value) ->
            when (value) {
                is String -> values.put(key, value)
                is Int -> values.put(key, value)
                is Double -> values.put(key, value)
                is ByteArray -> values.put(key, value)
                null -> values.putNull(key)
                else -> Log.e("DBManager", "Tipo de dato no soportado para key: $key")
            }
        }

        val filasActualizadas = db.update("refacciones", values, "id = ?", arrayOf(id))
        return filasActualizadas > 0
    }

    // ============================================================
    //  OBTENER REFACCIONES (Para AltasGlobalesScreen y MisAltasScreen)
    // ============================================================
    fun obtenerRefacciones(): List<Map<String, Any?>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any?>>()

        // Seleccionar todos los campos necesarios para las pantallas de Altas
        val cursor = db.rawQuery(
            """
            SELECT id, descripcion, costo, area, consumo, numero_parte_proveedor, familia
            FROM refacciones
            """.trimIndent(), null
        )

        if (cursor.moveToFirst()) {
            do {
                val registro = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    "descripcion" to cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    "costo" to cursor.getDouble(cursor.getColumnIndexOrThrow("costo")),
                    "area" to cursor.getString(cursor.getColumnIndexOrThrow("area")),
                    "consumo" to cursor.getString(cursor.getColumnIndexOrThrow("consumo")),
                    "numero_parte_proveedor" to cursor.getString(cursor.getColumnIndexOrThrow("numero_parte_proveedor")),
                    "familia" to cursor.getString(cursor.getColumnIndexOrThrow("familia"))
                )
                lista.add(registro)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }

}