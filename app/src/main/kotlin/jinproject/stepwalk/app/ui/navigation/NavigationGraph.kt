package jinproject.stepwalk.app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.home.navigation.backStackToHome
import jinproject.stepwalk.home.navigation.homeGraph
import jinproject.stepwalk.home.navigation.homeNavGraph
import jinproject.stepwalk.home.navigation.navigateToCalendar
import jinproject.stepwalk.login.navigation.authNavGraph
import jinproject.stepwalk.login.navigation.navigateToFindId
import jinproject.stepwalk.login.navigation.navigateToFindPassword
import jinproject.stepwalk.login.navigation.navigateToLogin
import jinproject.stepwalk.login.navigation.navigateToSignUp
import jinproject.stepwalk.login.navigation.navigateToSignUpDetail
import jinproject.stepwalk.ranking.navigation.navigateToRankingUserDetail
import jinproject.stepwalk.ranking.navigation.rankingNavGraph

@Composable
internal fun NavigationGraph(
    router: Router,
    modifier: Modifier = Modifier,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val navController = router.navController

    NavHost(
        navController = navController,
        startDestination = homeGraph,
        modifier = modifier
    ) {
        homeNavGraph(
            navigateToCalendar = navController::navigateToCalendar,
            popBackStack = navController::popBackStackIfCan,
            showSnackBar = showSnackBar
        )

        authNavGraph(
            navigateToSignUp = navController::navigateToSignUp,
            navigateToSignUpDetail = navController::navigateToSignUpDetail,
            navigateToFindId = navController::navigateToFindId,
            navigateToFindPassword = navController::navigateToFindPassword,
            popBackStack = navController::popBackStackIfCan,
            backStackToHome = navController::backStackToHome,
            showSnackBar = showSnackBar
        )

        rankingNavGraph(
            popBackStack = navController::popBackStackIfCan,
            showSnackBar = showSnackBar,
            navigateToRankingUserDetail = navController::navigateToRankingUserDetail,
            navigateToLogin = navController::navigateToLogin,
        )

        composable(route = BottomNavigationDestination.Profile.route) {
            Column(modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()) {
                Image(
                    painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_setting),
                    contentDescription = "settingIcon"
                )
            }
        }

        composable(route = BottomNavigationDestination.Mission.route) {
            Column(modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()) {
                Image(
                    painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_bookmark),
                    contentDescription = "settingIcon"
                )
            }
        }
    }
}


@Composable
internal fun BottomNavigationGraph(
    router: Router,
    modifier: Modifier = Modifier,
) {
    when {
        router.currentDestination.showBottomBarOrHide() -> {
            NavigationBar(
                modifier = modifier,
                contentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
            ) {
                BottomNavigationDestination.entries.forEach { destination ->
                    val selected = router.currentDestination.isDestinationInHierarchy(destination)

                    NavigationBarItem(
                        selected = selected,
                        onClick = { router.navigateOnBottomNavigationBar(destination) },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = destination.icon),
                                contentDescription = "clickIcon",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        iconClicked = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = destination.iconClicked),
                                contentDescription = "clickedIcon",
                                tint = MaterialTheme.colorScheme.onSurface
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
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.background
}