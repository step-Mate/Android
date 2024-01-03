package jinproject.stepwalk.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.calendar.CalendarScreen
import jinproject.stepwalk.home.screen.HomeScreen
import jinproject.stepwalk.home.screen.state.SnackBarMessage
import jinproject.stepwalk.home.screen.state.ZonedTime
import jinproject.stepwalk.home.screen.state.ZonedTimeRange
import jinproject.stepwalk.home.utils.onKorea
import java.time.Instant

const val homeRoute = "home"
private const val calendarRoute = "calendar"
private const val calendarLink = "$calendarRoute/{start}"

fun NavGraphBuilder.homeNavGraph(
    navigateToCalendar: (Long) -> Unit,
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    composable(route = homeRoute) {
        HomeScreen(
            navigateToCalendar = navigateToCalendar,
            showSnackBar = showSnackBar
        )
    }
    composable(
        route = calendarLink,
        arguments = listOf(navArgument("start") { type = NavType.LongType })
    ) { navBackStackEntry ->
        val today = ZonedTime(Instant.now().onKorea())
        val start = ZonedTime(
            Instant.ofEpochSecond(
                navBackStackEntry.arguments?.getLong("start") ?: today.time.toEpochSecond()
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

fun NavController.navigateToHome(navOptions: NavOptions?) {
    this.navigate(homeRoute, navOptions = navOptions)
}

fun NavController.navigateToCalendar(time: Long) {
    this.navigate("$calendarRoute/$time")
}