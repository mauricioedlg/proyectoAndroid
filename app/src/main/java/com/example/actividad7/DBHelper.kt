package com.example.actividad7

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(
    context,
    "proyectoAndroid.db",
    null,
    13 // 🔥 Versión 13: Persistencia de datos arreglada
) {
    override fun onCreate(db: SQLiteDatabase) {
        crearTablaUsuarios(db)
        crearTablaRefacciones(db)
        crearTablaNotificaciones(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 🔥 CORRECCIÓN IMPORTANTE:
        // Ya NO borramos las tablas (DROP). Así tus datos se guardan aunque actualices la app.
        // Solo llamamos a crear por si falta alguna tabla nueva.

        crearTablaUsuarios(db)
        crearTablaRefacciones(db)
        crearTablaNotificaciones(db)
    }

    private fun crearTablaUsuarios(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usuarios (
                UsuarioID INTEGER PRIMARY KEY AUTOINCREMENT,
                Username TEXT NOT NULL UNIQUE,
                Correo TEXT NOT NULL UNIQUE,
                Contrasena TEXT NOT NULL,
                Nombre TEXT NOT NULL,
                Apellidos TEXT NOT NULL,
                Rol INTEGER NOT NULL,
                DescripcionRol TEXT,
                NumeroDeSitio INTEGER NOT NULL
            );
        """.trimIndent())
    }

    private fun crearTablaRefacciones(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS refacciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                descripcion TEXT,
                costo REAL,
                area TEXT,
                consumo TEXT,
                frecuencia TEXT,
                unidad TEXT,
                cantidad INTEGER,
                observacion TEXT,
                numero_parte_proveedor TEXT NOT NULL UNIQUE,
                marca_proveedor TEXT,
                equipos_a_usar TEXT,
                familia TEXT,
                reemplazable TEXT,
                reduce_velocidad TEXT,
                existe_riesgo TEXT,
                nacionalidad TEXT,
                pagina_web TEXT,
                usuario_id INTEGER NOT NULL DEFAULT 1,
                aprobacion_mtto TEXT DEFAULT 'PENDIENTE',
                aprobacion_planta TEXT DEFAULT 'PENDIENTE',
                FOREIGN KEY(usuario_id) REFERENCES usuarios(UsuarioID)
            );
        """.trimIndent())
    }

    private fun crearTablaNotificaciones(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS notificaciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_destinoa_id INTEGER NOT NULL,
                mensaje TEXT NOT NULL,
                refaccion_id INTEGER, 
                leido INTEGER DEFAULT 0,
                FOREIGN KEY(usuario_destinoa_id) REFERENCES usuarios(UsuarioID)
            );
        """.trimIndent())
    }
}