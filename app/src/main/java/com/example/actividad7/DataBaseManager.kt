package com.example.actividad7

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

data class Usuario(
    val id: Int,
    val nombre: String,
    val username: String,
    val rol: Int // Importante para el menú
)

class DatabaseManager(context: Context) :
    SQLiteOpenHelper(context, "proyectoAndroid.db", null, 5) {

    override fun onCreate(db: SQLiteDatabase) { /* Se maneja en DBHelper */ }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { /* Se maneja en DBHelper */ }

    // LOGIN: Lee el Rol del usuario
    fun validarUsuarioPorUsername(username: String, password: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT UsuarioID, Nombre, Username, Rol FROM usuarios WHERE Username = ? AND Contrasena = ?",
            arrayOf(username, password)
        )

        return if (cursor.moveToFirst()) {
            val user = Usuario(
                id = cursor.getInt(0),
                nombre = cursor.getString(1),
                username = cursor.getString(2),
                rol = cursor.getInt(3)
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    // Insertar
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
            }
        }
        return try {
            db.insertOrThrow("refacciones", null, values)
        } catch (e: Exception) {
            -1L
        }
    }

    // Actualizar datos generales
    fun actualizarRefaccion(id: String, datos: Map<String, Any?>): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        datos.forEach { (key, value) ->
            when (value) {
                is String -> values.put(key, value)
                is Int -> values.put(key, value)
                is Double -> values.put(key, value)
                null -> values.putNull(key)
            }
        }
        val filas = db.update("refacciones", values, "id = ?", arrayOf(id))
        return filas > 0
    }

    // 🔥 OBTENER SOLO PENDIENTES (Para pantalla de aprobaciones)
    fun obtenerPendientesAprobacion(): List<Map<String, Any?>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any?>>()
        val cursor = db.rawQuery("SELECT * FROM refacciones WHERE aprobacion_mtto = 'PENDIENTE'", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToMap(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    // 🔥 APROBAR O RECHAZAR
    fun actualizarEstadoAprobacion(id: Int, nuevoEstado: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("aprobacion_mtto", nuevoEstado) // 'SI' o 'NO'

        val filas = db.update("refacciones", values, "id = ?", arrayOf(id.toString()))
        return filas > 0
    }

    // Obtener todas (Altas Globales)
    fun obtenerRefacciones(): List<Map<String, Any?>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any?>>()
        val cursor = db.rawQuery("SELECT * FROM refacciones", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToMap(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    // Obtener por usuario (Mis Altas)
    fun obtenerRefaccionesPorUsuario(usuarioId: Int): List<Map<String, Any?>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any?>>()
        val cursor = db.rawQuery("SELECT * FROM refacciones WHERE usuario_id = ?", arrayOf(usuarioId.toString()))

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToMap(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    private fun cursorToMap(cursor: android.database.Cursor): Map<String, Any?> {
        val columnas = cursor.columnNames
        val mapa = mutableMapOf<String, Any?>()
        columnas.forEach { col ->
            val index = cursor.getColumnIndex(col)
            mapa[col] = when (cursor.getType(index)) {
                android.database.Cursor.FIELD_TYPE_STRING -> cursor.getString(index)
                android.database.Cursor.FIELD_TYPE_INTEGER -> cursor.getInt(index)
                android.database.Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(index)
                android.database.Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(index)
                else -> null
            }
        }
        if (mapa["aprobacion_mtto"] == null) {
            mapa["aprobacion_mtto"] = "PENDIENTE"
        }
        return mapa
    }
}