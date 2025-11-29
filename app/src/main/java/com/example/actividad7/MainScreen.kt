
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

@Composable
fun MainScreen() {

    val context = LocalContext.current
    val dbManager = remember { DatabaseManager(context) }

    var isAuthenticated by remember { mutableStateOf(false) }

    // Usuario por defecto (obligatorio username)
    val defaultUser = Usuario(
        id = 0,
        nombre = "Invitado",
        username = "invitado"
    )

    var currentUser by remember { mutableStateOf(defaultUser) }

    // Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        "Inicio",
        "Formulario",
        "Mis Altas",
        "Altas Globales",
        "Corregir refacciones",
        "Cerrar Sesión"
    )

    var currentScreen by remember { mutableStateOf("Inicio") }

    if (!isAuthenticated) {

        LoginScreen(
            onLoginSuccess = { usuario ->
                currentUser = usuario
                isAuthenticated = true
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
                    Divider()

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
                        title = {
                            Text(
                                text = currentScreen,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } }
                            ) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Abrir menú",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->

                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                        .clickable(enabled = drawerState.isOpen) {
                            scope.launch { drawerState.close() }
                        },
                    contentAlignment = Alignment.TopCenter
                ) {

                    when (currentScreen) {
                        "Inicio" -> HomeScreen(nombreUsuario = currentUser.nombre)
                        "Formulario" -> FormularioScreen(usuarioId = currentUser.id)
                        "Mis Altas" -> MisAltasScreen(usuarioId = currentUser.id)
                        "Altas Globales" -> AltasGlobalesScreen()
                        "Corregir refacciones" -> CorregirRefaccionesScreen(usuarioId = currentUser.id)
                        else ->
                            Text("Pantalla no encontrada: $currentScreen", color = Color.Red)
                    }
                }
            }
        }
    }
}

// HomeScreen
@Composable
fun HomeScreen(nombreUsuario: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Bienvenido, $nombreUsuario",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Usa el menú para navegar.", color = MaterialTheme.colorScheme.onBackground)
    }
}
