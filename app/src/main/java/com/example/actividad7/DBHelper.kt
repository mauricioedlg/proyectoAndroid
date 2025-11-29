package com.example.actividad7

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(
    context,
    "proyectoAndroid.db",
    null,
    8 // 🔥 Versión 8: Forzará la actualización de la tabla refacciones
) {
    override fun onCreate(db: SQLiteDatabase) {
        crearTablaUsuarios(db)
        crearTablaRefacciones(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 🔥 ACTUALIZACIÓN:
        // Solo borramos y recreamos 'refacciones' para quitar la columna 'foto'.
        // NO tocamos la tabla 'usuarios' para respetar tus datos actuales.

        db.execSQL("DROP TABLE IF EXISTS refacciones")
        crearTablaRefacciones(db)
    }

    private fun crearTablaUsuarios(db: SQLiteDatabase) {
        // Solo crea la estructura si no existe. NO inserta datos.
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
        // Tabla SIN la columna 'foto'
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
                FOREIGN KEY(usuario_id) REFERENCES usuarios(UsuarioID)
            );
        """.trimIndent())
    }
}