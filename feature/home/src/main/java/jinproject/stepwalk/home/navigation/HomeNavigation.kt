package jinproject.stepwalk.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.HomeScreen
import jinproject.stepwalk.home.calendar.CalendarScreen

const val homeRoute = "home"
const val homeGraph = "home_graph"
const val calendarRoute = "calendar"

fun NavGraphBuilder.homeNavGraph(
    healthConnector: HealthConnector,
    navigateToCalendar: () -> Unit,
    popBackStack: () -> Unit
) {
    navigation(
        route = homeGraph,
        startDestination = homeRoute
    ) {
        composable(route = homeRoute) {
            HomeScreen(
                healthConnector = healthConnector,
                navigateToCalendar = navigateToCalendar
            )
        }
        composable(route = calendarRoute) {
            CalendarScreen(
                popBackStack = popBackStack
            )
        }
    }
}

fun NavController.navigateToHome(navOptions: NavOptions?) {
    this.navigate(homeGraph, navOptions = navOptions)
}

fun NavController.navigateToCalendar() {
    this.navigate(calendarRoute)
}