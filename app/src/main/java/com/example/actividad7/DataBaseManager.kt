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
    SQLiteOpenHelper(context, "proyectoAndroid.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) { /* NO SE CREA, VIENE DE ASSETS */ }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { /* NO USAR */ }

    // ============================================================
    // VALIDAR LOGIN
    // ============================================================
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
    // INSERTAR REFACCIÓN
    // ============================================================
    fun insertarRefaccion(datos: Map<String, Any?>): Long {
        val db = writableDatabase
        val values = ContentValues()

        datos.forEach { (key, value) ->
            when (value) {
                is String -> values.put(key, value)
                is Int -> values.put(key, value)
                is Double -> values.put(key, value)
                is ByteArray -> values.put(key, value)
                null -> values.putNull(key)
                else -> Log.e("DBManager", "Tipo de dato no soportado: $key")
            }
        }

        return try {
            db.insertOrThrow("refacciones", null, values)
        } catch (e: Exception) {
            Log.e("DBManager", "Error insertando refacción: ${e.message}")
            -1L
        }
    }

    // ============================================================
    // ACTUALIZAR REFACCIÓN
    // ============================================================
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
                else -> Log.e("DBManager", "Tipo no soportado: $key")
            }
        }

        val filas = db.update("refacciones", values, "id = ?", arrayOf(id))
        return filas > 0
    }

    // ============================================================
    // OBTENER TODAS LAS REFACCIONES (ALTAS GLOBALES)
    // ============================================================
    fun obtenerRefacciones(): List<Map<String, Any?>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any?>>()

        val cursor = db.rawQuery(
            """
            SELECT id, descripcion, costo, area, consumo, numero_parte_proveedor, familia
            FROM refacciones
            """.trimIndent(),
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val reg = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    "descripcion" to cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    "costo" to cursor.getDouble(cursor.getColumnIndexOrThrow("costo")),
                    "area" to cursor.getString(cursor.getColumnIndexOrThrow("area")),
                    "consumo" to cursor.getString(cursor.getColumnIndexOrThrow("consumo")),
                    "numero_parte_proveedor" to cursor.getString(cursor.getColumnIndexOrThrow("numero_parte_proveedor")),
                    "familia" to cursor.getString(cursor.getColumnIndexOrThrow("familia"))
                )
                lista.add(reg)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }

    // ============================================================
    // OBTENER REFACCIONES DE UN USUARIO
    // ============================================================
    fun obtenerRefaccionesPorUsuario(usuarioId: Int): List<Map<String, Any?>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any?>>()

        val cursor = db.rawQuery(
            """
            SELECT id, descripcion, costo, area, consumo, numero_parte_proveedor, familia
            FROM refacciones
            WHERE usuario_id = ?
            """.trimIndent(),
            arrayOf(usuarioId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val reg = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    "descripcion" to cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    "costo" to cursor.getDouble(cursor.getColumnIndexOrThrow("costo")),
                    "area" to cursor.getString(cursor.getColumnIndexOrThrow("area")),
                    "consumo" to cursor.getString(cursor.getColumnIndexOrThrow("consumo")),
                    "numero_parte_proveedor" to cursor.getString(cursor.getColumnIndexOrThrow("numero_parte_proveedor")),
                    "familia" to cursor.getString(cursor.getColumnIndexOrThrow("familia"))
                )
                lista.add(reg)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }
}
