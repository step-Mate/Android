package jinproject.stepwalk.app.ui.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import jinproject.stepwalk.app.ui.navigation.BottomNavigationDestination

fun NavController.navigateToHome(navOptions: NavOptions?) {
    this.navigate(BottomNavigationDestination.HOME.route, navOptions = navOptions)
}