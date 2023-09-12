package jinproject.stepwalk.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.HomeScreen

const val homeRoute = "home"

fun NavGraphBuilder.homeScreen(healthConnector: HealthConnector) {
    composable(route = homeRoute) {
        HomeScreen(
            healthConnector = healthConnector
        )
    }
}

fun NavController.navigateToHome(navOptions: NavOptions?) {
    this.navigate(homeRoute, navOptions = navOptions)
}