package jinproject.stepwalk.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.HomeScreen
import jinproject.stepwalk.home.calendar.CalendarScreen
import jinproject.stepwalk.home.state.ZonedTime
import jinproject.stepwalk.home.state.ZonedTimeRange
import jinproject.stepwalk.home.utils.onKorea
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

const val homeRoute = "home"
const val homeGraph = "home_graph"
const val calendarRoute = "calendar/{start}"

fun NavGraphBuilder.homeNavGraph(
    healthConnector: HealthConnector,
    navigateToCalendar: (Long) -> Unit,
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
        composable(
            route = calendarRoute,
            arguments = listOf(navArgument("start") { type = NavType.LongType })
        ) { navBackStackEntry ->
            val today = ZonedTime(Instant.now().onKorea())
            val start = ZonedTime(
                Instant.ofEpochSecond(
                    navBackStackEntry.arguments?.getString("start")?.toLong() ?: today.time.toEpochSecond()
                ).onKorea()
            )

            CalendarScreen(
                timeRange = ZonedTimeRange(
                    start = start,
                    endInclusive = today
                ),
                popBackStack = popBackStack
            )
        }
    }
}

fun NavController.navigateToHome(navOptions: NavOptions?) {
    this.navigate(homeGraph, navOptions = navOptions)
}

fun NavController.navigateToCalendar(time: Long) {
    this.navigate("calendar/$time")
}