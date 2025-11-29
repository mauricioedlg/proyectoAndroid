package com.example.actividad7

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Usuario(
    val id: Int,
    val nombre: String,
    val username: String,
    val rol: Int
)

class DatabaseManager(context: Context) :
    SQLiteOpenHelper(context, "proyectoAndroid.db", null, 11) {

    override fun onCreate(db: SQLiteDatabase) { /* Se maneja en DBHelper */ }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { /* Se maneja en DBHelper */ }

    // LOGIN
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

    // ==========================================
    // LÓGICA DE NOTIFICACIONES
    // ==========================================

    private fun crearNotificacion(db: SQLiteDatabase, usuarioDestinoId: Int, mensaje: String, refaccionId: Long) {
        val values = ContentValues().apply {
            put("usuario_destinoa_id", usuarioDestinoId)
            put("mensaje", mensaje)
            put("refaccion_id", refaccionId)
            put("leido", 0)
        }
        db.insert("notificaciones", null, values)
    }

    fun obtenerNotificaciones(usuarioId: Int): List<Map<String, Any>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any>>()
        val cursor = db.rawQuery(
            "SELECT * FROM notificaciones WHERE usuario_destinoa_id = ? ORDER BY id DESC",
            arrayOf(usuarioId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val mapa = mutableMapOf<String, Any>()
                mapa["id"] = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                mapa["mensaje"] = cursor.getString(cursor.getColumnIndexOrThrow("mensaje"))
                mapa["leido"] = cursor.getInt(cursor.getColumnIndexOrThrow("leido"))
                // Recuperamos el ID de refacción por si se necesita
                val refIdIndex = cursor.getColumnIndex("refaccion_id")
                if(refIdIndex != -1) mapa["refaccion_id"] = cursor.getInt(refIdIndex)

                lista.add(mapa)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun contarNotificacionesNoLeidas(usuarioId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM notificaciones WHERE usuario_destinoa_id = ? AND leido = 0",
            arrayOf(usuarioId.toString())
        )
        var count = 0
        if (cursor.moveToFirst()) count = cursor.getInt(0)
        cursor.close()
        return count
    }

    fun marcarNotificacionesComoLeidas(usuarioId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply { put("leido", 1) }
        db.update("notificaciones", values, "usuario_destinoa_id = ?", arrayOf(usuarioId.toString()))
    }

    // 🔥 ELIMINAR UNA NOTIFICACIÓN ESPECÍFICA (Para el Usuario al hacer click)
    fun eliminarNotificacion(notificacionId: Int) {
        val db = writableDatabase
        db.delete("notificaciones", "id = ?", arrayOf(notificacionId.toString()))
    }

    // ==========================================
    // REFACCIONES + AUTOMATIZACIÓN
    // ==========================================

    fun insertarRefaccion(datos: Map<String, Any?>): Long {
        val db = writableDatabase
        val values = ContentValues()
        datos.forEach { (key, value) ->
            if (value != null) {
                when (value) {
                    is String -> values.put(key, value)
                    is Int -> values.put(key, value)
                    is Double -> values.put(key, value)
                }
            } else values.putNull(key)
        }

        return try {
            val id = db.insertOrThrow("refacciones", null, values)
            if (id != -1L) {
                // 🔥 Busca al Gerente (Rol 1) para notificarle
                val cursorGerente = db.rawQuery("SELECT UsuarioID FROM usuarios WHERE Rol = 1 LIMIT 1", null)
                if (cursorGerente.moveToFirst()) {
                    val gerenteId = cursorGerente.getInt(0)
                    val descripcion = datos["descripcion"] ?: "Refacción"
                    crearNotificacion(db, gerenteId, "Nueva solicitud pendiente: $descripcion", id)
                }
                cursorGerente.close()
            }
            id
        } catch (e: Exception) {
            -1L
        }
    }

    // 🔥 ACTUALIZAR ESTADO (MAGIA DE BORRADO AUTOMÁTICO)
    fun actualizarEstadoAprobacion(idRefaccion: Int, nuevoEstado: String): Boolean {
        val db = writableDatabase

        // 1. Obtener datos antes de actualizar para saber a quién notificar
        var usuarioDueñoId = -1
        var descripcionRefaccion = ""
        val cursor = db.rawQuery("SELECT usuario_id, descripcion FROM refacciones WHERE id = ?", arrayOf(idRefaccion.toString()))
        if (cursor.moveToFirst()) {
            usuarioDueñoId = cursor.getInt(0)
            descripcionRefaccion = cursor.getString(1)
        }
        cursor.close()

        // 2. Actualizar estado
        val values = ContentValues().apply { put("aprobacion_mtto", nuevoEstado) }
        val filas = db.update("refacciones", values, "id = ?", arrayOf(idRefaccion.toString()))

        if (filas > 0) {
            // 🔥 MAGIA 1: BORRAR la notificación del Gerente asociada a esta refacción
            // Esto cumple el requisito: "se quite esa notificación cuando haya rechazado o aprobado"
            db.delete("notificaciones", "refaccion_id = ?", arrayOf(idRefaccion.toString()))

            // 🔥 MAGIA 2: Crear notificación para el dueño
            if (usuarioDueñoId != -1) {
                val mensaje = if (nuevoEstado == "SI") {
                    "Tu alta '$descripcionRefaccion' ha sido APROBADA por el gerente."
                } else {
                    "Tu alta '$descripcionRefaccion' ha sido RECHAZADA por el gerente."
                }
                // También le pasamos el refaccionId
                crearNotificacion(db, usuarioDueñoId, mensaje, idRefaccion.toLong())
            }
        }

        return filas > 0
    }

    // (Resto de métodos estándar)
    fun actualizarRefaccion(id: String, datos: Map<String, Any?>): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        datos.forEach { (key, value) ->
            if (value != null) {
                when (value) {
                    is String -> values.put(key, value)
                    is Int -> values.put(key, value)
                    is Double -> values.put(key, value)
                }
            } else values.putNull(key)
        }
        return db.update("refacciones", values, "id = ?", arrayOf(id)) > 0
    }

    fun obtenerPendientesAprobacion(): List<Map<String, Any?>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any?>>()
        val cursor = db.rawQuery("SELECT * FROM refacciones WHERE aprobacion_mtto = 'PENDIENTE'", null)
        if (cursor.moveToFirst()) {
            do { lista.add(cursorToMap(cursor)) } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun obtenerRefacciones(): List<Map<String, Any?>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any?>>()
        val cursor = db.rawQuery("SELECT * FROM refacciones", null)
        if (cursor.moveToFirst()) {
            do { lista.add(cursorToMap(cursor)) } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun obtenerRefaccionesPorUsuario(usuarioId: Int): List<Map<String, Any?>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any?>>()
        val cursor = db.rawQuery("SELECT * FROM refacciones WHERE usuario_id = ?", arrayOf(usuarioId.toString()))
        if (cursor.moveToFirst()) {
            do { lista.add(cursorToMap(cursor)) } while (cursor.moveToNext())
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
        if (mapa["aprobacion_mtto"] == null) mapa["aprobacion_mtto"] = "PENDIENTE"
        return mapa
    }
}