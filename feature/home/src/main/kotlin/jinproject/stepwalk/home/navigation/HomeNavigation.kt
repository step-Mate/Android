package jinproject.stepwalk.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.home.screen.calendar.CalendarScreen
import jinproject.stepwalk.home.screen.home.HomeScreen
import jinproject.stepwalk.home.screen.homeSetting.HomeSettingScreen

const val homeGraph = "homeGraph"
const val homeRoute = "home"
private const val calendarRoute = "calendar"
private const val calendarLink = "$calendarRoute/{start}"
private const val homeSettingRoute = "homeSetting"

fun NavGraphBuilder.homeNavGraph(
    navigateToCalendar: (Long) -> Unit,
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    navigateToHomeSetting: () -> Unit,
) {
    navigation(
        route = homeGraph,
        startDestination = homeRoute
    ) {
        composable(route = homeRoute) {
            HomeScreen(
                navigateToCalendar = navigateToCalendar,
                showSnackBar = showSnackBar,
                navigateToHomeSetting = navigateToHomeSetting,
            )
        }
        composable(
            route = calendarLink,
            arguments = listOf(navArgument("start") { type = NavType.LongType })
        ) {
            CalendarScreen(
                popBackStack = popBackStack,
                showSnackBar = showSnackBar,
            )
        }
        composable(
            route = homeSettingRoute,
        ) {
            HomeSettingScreen(
                popBackStack = popBackStack,
                showSnackBar = showSnackBar,
            )
        }
    }
}

fun NavController.navigateToHome(navOptions: NavOptions?) {
    this.navigate(homeRoute, navOptions = navOptions)
}

fun NavController.navigateToCalendar(time: Long) {
    this.navigate("$calendarRoute/$time")
}

fun NavController.backStackToHome() {
    this.popBackStack(homeRoute, inclusive = false)
}

fun NavController.navigateToHomeSetting() {
    this.navigate(homeSettingRoute)
}