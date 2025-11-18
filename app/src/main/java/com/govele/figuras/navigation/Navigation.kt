package com.govele.figuras.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.govele.figuras.domain.model.Figura
import com.govele.figuras.views.diseno.DisenoScreen
import com.govele.figuras.views.seleccion.SeleccionScreen

sealed class Destination(val route: String) {
    object Seleccion : Destination("seleccion")
    object Diseno : Destination("diseno") {
        const val ARG_FIGURA = "figura"
        fun createRoute(figura: Figura? = null) =
            if (figura != null) "diseno?figura=${figura}" else "diseno"
    }
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destination.Seleccion.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.Seleccion.route) {
            SeleccionScreen(
                onNavigateToDiseno = { figura ->
                    navController.navigate(Destination.Diseno.createRoute(figura))
                }
            )
        }

        composable(Destination.Diseno.route) {
            DisenoScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}