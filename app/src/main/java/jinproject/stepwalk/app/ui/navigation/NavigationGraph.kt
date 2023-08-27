package jinproject.stepwalk.app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import jinproject.stepwalk.app.ui.core.SnackBarMessage
import jinproject.stepwalk.home.navigation.homeScreen


@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationDestination.HOME.route,
        modifier = modifier
    ) {
        homeScreen()

        composable(route = BottomNavigationDestination.SETTING.route) {
            Column(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_setting),
                    contentDescription = "settingIcon"
                )
            }
        }
    }
}


@Composable
fun BottomNavigationGraph(
    router: Router,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        BottomNavigationDestination.values.forEach { destination ->
            val selected = router.currentDestination.isTopLevelDestinationInHierarchy(destination)

            NavigationBarItem(
                selected = selected,
                onClick = { router.navigate(destination) },
                icon = {
                    Icon(painter = painterResource(id = destination.icon), contentDescription = "clickedIcon")
                },
                iconClicked = {
                    Icon(painter = painterResource(id = destination.iconClicked), contentDescription = "clickedIcon")
                }
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: BottomNavigationDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false

@Composable
private fun RowScope.NavigationBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = false,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    iconClicked: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) iconClicked else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = NavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = NavigationDefaults.navigationContentColor(),
        ),
    )
}

@Stable
private object NavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}