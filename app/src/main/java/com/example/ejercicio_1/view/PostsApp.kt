package com.example.ejercicio_1.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ejercicio_1.data.PostApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun PostsApp() {
  val retrofit = Retrofit.Builder()
    .baseUrl("https://dummyjson.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

  val servicio = retrofit.create(PostApiService::class.java)
  val navController = rememberNavController()

  Scaffold(
    modifier = Modifier.padding(top = 40.dp),
    topBar = { BarraSuperior() },
    bottomBar = { BarraInferior(navController) },
    floatingActionButton = { BotonFAB(navController) },
    content = { paddingValues -> Contenido(paddingValues, navController, servicio) }
  )
}

@Composable
fun BotonFAB(navController: NavHostController) {
  val cbeState by navController.currentBackStackEntryAsState()
  val rutaActual = cbeState?.destination?.route
  if (rutaActual == "posts") {
    FloatingActionButton(
      containerColor = Color.Blue,
      contentColor = Color.White,
      onClick = { navController.navigate("postNuevo") }
    ) {
      Icon(
        imageVector = Icons.Filled.Add,
        contentDescription = "Add"
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text = "POSTS APP",
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.primary
    )
  )
}

@Composable
fun BarraInferior(navController: NavHostController) {
  NavigationBar(
    containerColor = Color.LightGray
  ) {
    NavigationBarItem(
      icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
      label = { Text("Inicio") },
      selected = navController.currentDestination?.route == "inicio",
      onClick = { navController.navigate("inicio") }
    )
    NavigationBarItem(
      icon = { Icon(Icons.Outlined.Favorite, contentDescription = "Posts") },
      label = { Text("Posts") },
      selected = navController.currentDestination?.route == "posts",
      onClick = { navController.navigate("posts") }
    )
  }
}

@Composable
fun Contenido(
  pv: PaddingValues,
  navController: NavHostController,
  servicio: PostApiService
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(pv)
  ) {
    NavHost(
      navController = navController,
      startDestination = "inicio"
    ) {
      composable("inicio") { ScreenInicio() }
      composable("posts") { ContenidoPostsListado(navController, servicio) }
      composable("postVer/{id}", arguments = listOf(
        navArgument("id") { type = NavType.IntType })
      ) { backStackEntry ->
        val id = backStackEntry.arguments?.getInt("id")
        if (id != null) {
          ContenidoPostEditar(navController, servicio, id)
        }
      }
      composable("postDel/{id}", arguments = listOf(
        navArgument("id") { type = NavType.IntType })
      ) { backStackEntry ->
        val id = backStackEntry.arguments?.getInt("id")
        if (id != null) {
          ContenidoPostEliminar(navController, servicio, id)
        }
      }
      composable("postNuevo") { ContenidoPostNuevo(navController, servicio) }
    }
  }
}

@Composable
fun ScreenInicio() {
  Text(
    text = "Bienvenido a la pantalla de inicio",
    fontSize = 20.sp,
    fontWeight = FontWeight.Bold,
    modifier = Modifier.padding(16.dp)
  )
}
