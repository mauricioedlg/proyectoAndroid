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

        // 🔥 USUARIOS INICIALES Y SUS CONTRASEÑAS ACTUALIZADAS
        // Insertamos todos los usuarios para que el login funcione
        db.execSQL("""
            INSERT INTO usuarios (UsuarioID, Username, Correo, Contrasena, Nombre, Apellidos, Rol, NumeroDeSitio)
            VALUES 
            (3, 'mauricio.estrada', 'mauricio.estrada@clarios.com', 'Pinos364123', 'Mauricio', 'Estrada De la Garza', 1, 13),
            (6, 'biancanava', 'bianca.nava@clarios.com', '987654321', 'Bianca', 'Nava', 4, 13),
            (7, 'benjamin.garcia', 'benjamin.garcia@clarios.com', 'ggez2003', 'Benjamin', 'Garcia', 3, 13),
            (8, 'julian.basurto', 'julian.basurto@clarios.com', 'clarios.com!', 'Julian', 'Basurto', 6, 13),
            (9, 'ricardo.medrano', 'ricardo.medrano@clarios.com', 'celayaPlanta', 'Ricardo', 'Medrano', 6, 13),
            (10, 'jesus.torres', 'jesus.torres@clarios.com', 'torreonPlanta', 'Jesus', 'Torres', 6, 13);
        """.trimIndent())

        // Tabla Refacciones
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
        // Borra las tablas existentes y llama a onCreate para recrearlas con los datos actualizados.
        db.execSQL("DROP TABLE IF EXISTS refacciones")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }
}