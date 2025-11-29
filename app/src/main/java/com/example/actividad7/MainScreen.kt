@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.actividad7

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import com.example.actividad7.screens.AltasGlobalesScreen
import com.example.actividad7.screens.CorregirRefaccionesScreen
import com.example.actividad7.screens.FormularioScreen
import com.example.actividad7.screens.MisAltasScreen
import com.example.actividad7.screens.LoginScreen
import com.example.actividad7.screens.AprobacionesPendientesScreen
import com.example.actividad7.screens.PantallaSimple

@Composable
fun MainScreen() {

    // Usuario invitado con rol -1 para evitar conflictos
    val defaultUser = Usuario(
        id = 0,
        nombre = "Invitado",
        username = "invitado",
        rol = -1
    )

    var currentUser by remember { mutableStateOf(defaultUser) }
    var isAuthenticated by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Menú dinámico
    val menuItems = remember(currentUser) {
        val items = mutableListOf(
            "Inicio",
            "Formulario",
            "Mis Altas",
            "Altas Globales",
            "Corregir refacciones"
        )

        // 🔥 CORRECCIÓN: Ahora permitimos Rol 1 (Gerente Mantenimiento) Y Rol 0 (Admin)
        if (currentUser.rol == 1 || currentUser.rol == 0) {
            items.add("Aprobaciones pendientes")
        }

        items.add("Cerrar Sesión")
        items
    }

    var currentScreen by remember { mutableStateOf("Inicio") }

    if (!isAuthenticated) {
        LoginScreen(
            onLoginSuccess = { usuario ->
                currentUser = usuario
                isAuthenticated = true
                currentScreen = "Inicio"
            }
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        "Menú Principal",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )

                    val nombreRol = when(currentUser.rol) {
                        0 -> "Admin"
                        1 -> "Gte. Mantenimiento"
                        2 -> "Gte. Planta"
                        3 -> "Almacenista"
                        4 -> "Comprador"
                        else -> "Usuario"
                    }

                    Text(
                        text = "Usuario: ${currentUser.username}\nRol: $nombreRol",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    menuItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item) },
                            selected = item == currentScreen,
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (item == "Cerrar Sesión") {
                                    isAuthenticated = false
                                    currentUser = defaultUser
                                    currentScreen = "Inicio"
                                } else {
                                    currentScreen = item
                                }
                            }
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(currentScreen, color = MaterialTheme.colorScheme.onBackground) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú", tint = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (currentScreen) {
                        "Inicio" -> PantallaSimple(nombreUsuario = currentUser.nombre)
                        "Formulario" -> FormularioScreen(usuarioId = currentUser.id)
                        "Mis Altas" -> MisAltasScreen(usuarioId = currentUser.id)
                        "Altas Globales" -> AltasGlobalesScreen()
                        "Corregir refacciones" -> CorregirRefaccionesScreen(usuarioId = currentUser.id)
                        "Aprobaciones pendientes" -> AprobacionesPendientesScreen()
                        else -> Text("Pantalla no encontrada", color = Color.Red)
                    }
                }
            }
        }
    }
}