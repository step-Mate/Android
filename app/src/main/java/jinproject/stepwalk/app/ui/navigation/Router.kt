package jinproject.stepwalk.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import jinproject.stepwalk.home.navigation.navigateToHome

@Stable
class Router(private val navController: NavController) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    fun navigate(destination: BottomNavigationDestination) {
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

fun NavController.navigateToSetting(navOptions: NavOptions?) {
    this.navigate(BottomNavigationDestination.SETTING.route, navOptions = navOptions)
}