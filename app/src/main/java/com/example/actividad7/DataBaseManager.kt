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
    SQLiteOpenHelper(context, "proyectoAndroid.db", null, 13) {

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
    // NOTIFICACIONES
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

    fun eliminarNotificacion(notificacionId: Int) {
        val db = writableDatabase
        db.delete("notificaciones", "id = ?", arrayOf(notificacionId.toString()))
    }

    // ==========================================
    // REFACCIONES + FLUJO
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
                // Notificar SIEMPRE al Gerente Mantenimiento (Rol 1)
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

    // 🔥 ACTUALIZAR ESTADO (FLUJO CORREGIDO PARA HISTORIAL)
    fun actualizarEstadoAprobacion(idRefaccion: Int, nuevoEstado: String, rolUsuario: Int): Boolean {
        val db = writableDatabase

        var usuarioDueñoId = -1
        var descripcionRefaccion = ""
        val cursor = db.rawQuery("SELECT usuario_id, descripcion FROM refacciones WHERE id = ?", arrayOf(idRefaccion.toString()))
        if (cursor.moveToFirst()) {
            usuarioDueñoId = cursor.getInt(0)
            descripcionRefaccion = cursor.getString(1)
        }
        cursor.close()

        val values = ContentValues()

        if (rolUsuario == 1 || rolUsuario == 0) { // Mantenimiento
            values.put("aprobacion_mtto", nuevoEstado)
            if (nuevoEstado == "NO") values.put("aprobacion_planta", "NO")
        } else if (rolUsuario == 2) { // Planta
            values.put("aprobacion_planta", nuevoEstado)
        }

        val filas = db.update("refacciones", values, "id = ?", arrayOf(idRefaccion.toString()))

        if (filas > 0) {
            // Borrar notificación del gerente actual (porque ya la atendió)
            db.delete("notificaciones", "refaccion_id = ? AND usuario_destinoa_id != ?",
                arrayOf(idRefaccion.toString(), usuarioDueñoId.toString()))
            // ^ Borramos la del gerente, pero NO la del dueño si existiera alguna

            // LÓGICA DE NOTIFICACIÓN
            if (rolUsuario == 1 || rolUsuario == 0) {
                // --- ACCIÓN DE MANTENIMIENTO ---
                if (nuevoEstado == "SI") {
                    // 1. Notificar a PLANTA (Rol 2)
                    val cursorPlanta = db.rawQuery("SELECT UsuarioID FROM usuarios WHERE Rol = 2 LIMIT 1", null)
                    if (cursorPlanta.moveToFirst()) {
                        val plantaId = cursorPlanta.getInt(0)
                        crearNotificacion(db, plantaId, "Solicitud aprobada por Mtto. Pendiente tu aprobación: $descripcionRefaccion", idRefaccion.toLong())
                    }
                    cursorPlanta.close()

                    // 2. 🔥 HISTORIAL: Notificar al USUARIO también
                    if (usuarioDueñoId != -1) {
                        crearNotificacion(db, usuarioDueñoId, "Tu alta '$descripcionRefaccion' fue aprobada por Mtto. Pasó a revisión de Planta.", idRefaccion.toLong())
                    }

                } else {
                    // Rechazó Mtto
                    crearNotificacion(db, usuarioDueñoId, "Tu alta '$descripcionRefaccion' fue RECHAZADA por Mantenimiento.", idRefaccion.toLong())
                }
            } else if (rolUsuario == 2) {
                // --- ACCIÓN DE PLANTA ---
                if (nuevoEstado == "SI") {
                    crearNotificacion(db, usuarioDueñoId, "¡Felicidades! Tu alta '$descripcionRefaccion' fue APROBADA por Planta (Proceso Completo).", idRefaccion.toLong())
                } else {
                    crearNotificacion(db, usuarioDueñoId, "Tu alta '$descripcionRefaccion' fue RECHAZADA por Planta.", idRefaccion.toLong())
                }
            }
        }

        return filas > 0
    }

    // Métodos estándar
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

    fun obtenerPendientesAprobacion(rolUsuario: Int): List<Map<String, Any?>> {
        val db = readableDatabase
        val lista = mutableListOf<Map<String, Any?>>()
        val query = when(rolUsuario) {
            1 -> "SELECT * FROM refacciones WHERE aprobacion_mtto = 'PENDIENTE'"
            2 -> "SELECT * FROM refacciones WHERE aprobacion_mtto = 'SI' AND aprobacion_planta = 'PENDIENTE'"
            0 -> "SELECT * FROM refacciones WHERE aprobacion_mtto = 'PENDIENTE' OR (aprobacion_mtto = 'SI' AND aprobacion_planta = 'PENDIENTE')"
            else -> "SELECT * FROM refacciones WHERE 1=0"
        }
        val cursor = db.rawQuery(query, null)
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
        if (mapa["aprobacion_planta"] == null) mapa["aprobacion_planta"] = "PENDIENTE"
        return mapa
    }
}