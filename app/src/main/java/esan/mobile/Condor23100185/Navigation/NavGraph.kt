package esan.mobile.Condor23100185.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import esan.mobile.Condor23100185.ViewModels.AuthViewModel
import esan.mobile.Condor23100185.ViewModels.MonedaViewModel
import esan.mobile.Condor23100185.screens.auth.LoginScreen
import esan.mobile.Condor23100185.screens.home.ConversionScreen

@Composable
fun NavigationMenu() {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel() }
    val monedaViewModel = remember { MonedaViewModel() }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(viewModel = authViewModel) {
                navController.navigate("conversion") {
                    popUpTo("login") { inclusive = true } // evita volver al login con "Back"
                }
            }
        }

        composable("conversion") {
            ConversionScreen(viewModel = monedaViewModel)
        }

    }
}

