package com.example.actividad7

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(
    context,
    "proyectoAndroid.db",
    null,
    3 // Subimos versión para forzar la actualización
) {
    override fun onCreate(db: SQLiteDatabase) {
        // Tabla Usuarios
        db.execSQL("""
            CREATE TABLE usuarios (
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

        // Usuario por defecto
        db.execSQL("""
            INSERT INTO usuarios (Username, Correo, Contrasena, Nombre, Apellidos, Rol, NumeroDeSitio)
            VALUES 
            ('mauricio.estrada', 'mauricio.estrada@clarios.com', '1234567890', 'Mauricio', 'Estrada', 1, 13);
        """.trimIndent())

        // Tabla Refacciones CORREGIDA (Sintaxis válida para SQLite)
        db.execSQL("""
            CREATE TABLE refacciones (
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
                foto BLOB,
                usuario_id INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(UsuarioID)
            );
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS refacciones")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }
}