package com.example.actividad7.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation // 🔥 IMPORTACIÓN NECESARIA
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.example.actividad7.DatabaseManager
import com.example.actividad7.Usuario

@Composable
fun LoginScreen(onLoginSuccess: (Usuario) -> Unit) {
    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = Color.White
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; isError = false },
                    label = { Text("Usuario") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.7f)) },
                    isError = isError,
                    colors = colors,
                    modifier = Modifier.fillMaxWidth()
                )

                // CAMPO DE CONTRASEÑA
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; isError = false },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White.copy(alpha = 0.7f)) },
                    // 🔥 ESTA LÍNEA OCULTA LA CONTRASEÑA
                    visualTransformation = PasswordVisualTransformation(),
                    isError = isError,
                    colors = colors,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError) {
                    Text(
                        text = "Credenciales incorrectas",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = {
                        val usuario = dbManager.validarUsuarioPorUsername(username, password)

                        if (usuario != null) {
                            onLoginSuccess(usuario)
                        } else {
                            isError = true
                            Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text("Ingresar")
                }
            }
        }
    }
}