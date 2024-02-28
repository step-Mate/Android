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
import com.beank.profile.navigation.navigateToProfile
import jinproject.stepwalk.home.navigation.navigateToHome
import jinproject.stepwalk.mission.navigation.navigateToMission

/**
 * Navigation을 담당하는 클래스
 * @param navController navigation을 수행하는 주체
 */
@Stable
internal class Router(val navController: NavHostController) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    internal fun navigateOnBottomNavigationBar(destination: BottomNavigationDestination) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (destination) {
            BottomNavigationDestination.Home -> navController.navigateToHome(navOptions)
            BottomNavigationDestination.Profile -> navController.navigateToProfile(navOptions)
            BottomNavigationDestination.Ranking -> navController.navigateToRanking(navOptions)
            BottomNavigationDestination.Mission -> navController.navigateToMission(navOptions)
        }
    }

}

fun NavDestination?.showBottomBarOrHide(): Boolean =
    (this?.route ?: false) in BottomNavigationDestination.entries
        .map { it.route }

fun NavDestination?.isDestinationInHierarchy(destination: BottomNavigationDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false

fun NavController.popBackStackIfCan() {
    this.previousBackStackEntry?.let {
        this.popBackStack()
    }
}

fun NavController.navigateToRanking(navOptions: NavOptions?) {
    this.navigate(BottomNavigationDestination.Ranking.route, navOptions = navOptions)
}
