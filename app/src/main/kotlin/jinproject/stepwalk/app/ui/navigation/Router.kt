package jinproject.stepwalk.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import jinproject.stepwalk.home.navigation.navigateToHome
import jinproject.stepwalk.ranking.navigation.navigateToRanking

/**
 * Navigation을 담당하는 클래스
 * @param navController navigation을 수행하는 주체
 */
@Stable
internal class Router(val navController: NavHostController) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    internal fun navigateTopLevelDestination(destination: NavigationDestination) {
        val navOptions = navOptions {
            navController.currentBackStackEntry?.destination?.route?.let {
                popUpTo(it) {
                    inclusive = true
                }
            }
            launchSingleTop = true
        }

        when (destination) {
            NavigationDestination.Home -> navController.navigateToHome(navOptions)
            NavigationDestination.Profile -> navController.navigateToProfile(navOptions)
            NavigationDestination.Ranking -> navController.navigateToRanking(navOptions = navOptions)
            NavigationDestination.Mission -> navController.navigateToMission(navOptions)
        }
    }

}

fun NavDestination?.isShownBar(): Boolean =
    (this?.route ?: false) in NavigationDestination.entries
        .map { it.route }

fun NavDestination?.isDestinationInHierarchy(destination: NavigationDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false

fun NavController.popBackStackIfCan() {
    this.previousBackStackEntry?.let {
        this.popBackStack()
    }
}

fun NavController.navigateToProfile(navOptions: NavOptions?) {
    this.navigate(NavigationDestination.Profile.route, navOptions = navOptions)
}

fun NavController.navigateToMission(navOptions: NavOptions?) {
    this.navigate(NavigationDestination.Mission.route, navOptions = navOptions)
}