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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import jinproject.stepwalk.home.screen.home.state.SnackBarMessage
import jinproject.stepwalk.home.navigation.homeNavGraph
import jinproject.stepwalk.home.navigation.navigateToCalendar


@Composable
internal fun NavigationGraph(
    router: Router,
    modifier: Modifier = Modifier,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val navController = router.navController

    NavHost(
        navController = navController,
        startDestination = BottomNavigationDestination.HOME.route,
        modifier = modifier
    ) {
        homeNavGraph(
            navigateToCalendar = navController::navigateToCalendar,
            popBackStack = navController::popBackStack,
            showSnackBar = showSnackBar
        )

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
internal fun BottomNavigationGraph(
    router: Router,
    modifier: Modifier = Modifier
) {
    when {
        router.currentDestination.showBottomBarOrHide() -> {
            NavigationBar(
                modifier = modifier,
                containerColor = Color.Transparent,
            ) {
                BottomNavigationDestination.values.forEach { destination ->
                    val selected = router.currentDestination.isDestinationInHierarchy(destination)

                    NavigationBarItem(
                        selected = selected,
                        onClick = { router.navigate(destination) },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = destination.icon),
                                contentDescription = "clickIcon",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        iconClicked = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = destination.iconClicked),
                                contentDescription = "clickedIcon",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    )
                }
            }
        }
    }
}

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
            indicatorColor = NavigationDefaults.navigationIndicatorColor()
        )
    )
}

@Stable
private object NavigationDefaults {
    @Composable
    fun navigationIndicatorColor() =  MaterialTheme.colorScheme.background
}