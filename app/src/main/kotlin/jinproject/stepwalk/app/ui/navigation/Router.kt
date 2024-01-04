package jinproject.stepwalk.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import jinproject.stepwalk.home.navigation.homeRoute
import jinproject.stepwalk.home.navigation.navigateToCalendar
import jinproject.stepwalk.home.navigation.navigateToHome

/**
 * Navigation을 담당하는 클래스
 * @param navController navigation을 수행하는 주체
 */
@Stable
internal class Router(val navController: NavHostController) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    internal fun navigate(destination: BottomNavigationDestination) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (destination) {
            BottomNavigationDestination.HOME -> navController.navigateToHome(navOptions)
            BottomNavigationDestination.SETTING -> navController.navigateToSetting(navOptions)
        }
    }

}

fun NavDestination?.showBottomBarOrHide(): Boolean =
    (this?.route ?: false) in BottomNavigationDestination.values
        .map { it.route }

fun NavDestination?.isDestinationInHierarchy(destination: BottomNavigationDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false

fun NavController.navigateToSetting(navOptions: NavOptions?) {
    this.navigate(BottomNavigationDestination.SETTING.route, navOptions = navOptions)
}