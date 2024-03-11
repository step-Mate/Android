package jinproject.stepwalk.app.ui.navigation

import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import jinproject.stepwalk.app.ui.navigation.permission.PermissionScreen
import jinproject.stepwalk.app.ui.navigation.permission.permissionRoute
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.home.navigation.homeNavGraph
import jinproject.stepwalk.home.navigation.navigateToCalendar
import jinproject.stepwalk.home.navigation.navigateToHome
import jinproject.stepwalk.home.navigation.navigateToHomeSetting
import jinproject.stepwalk.login.navigation.authNavGraph
import jinproject.stepwalk.login.navigation.navigateToFindId
import jinproject.stepwalk.login.navigation.navigateToFindPassword
import jinproject.stepwalk.login.navigation.navigateToLogin
import jinproject.stepwalk.login.navigation.navigateToSignUp
import jinproject.stepwalk.login.navigation.navigateToSignUpDetail
import jinproject.stepwalk.mission.navigation.missionNavGraph
import jinproject.stepwalk.mission.navigation.navigateToMissionDetail
import jinproject.stepwalk.profile.navigation.navigateToEditUser
import jinproject.stepwalk.profile.navigation.navigateToProfile
import jinproject.stepwalk.profile.navigation.navigateToTerms
import jinproject.stepwalk.profile.navigation.profileNavigation
import jinproject.stepwalk.profile.navigation.profileRoute
import jinproject.stepwalk.ranking.navigation.navigateToNotification
import jinproject.stepwalk.ranking.navigation.navigateToRanking
import jinproject.stepwalk.ranking.navigation.navigateToRankingUserDetail
import jinproject.stepwalk.ranking.navigation.rankingNavGraph
import jinproject.stepwalk.ranking.navigation.rankingRoute

@Composable
internal fun NavigationGraph(
    router: Router,
    modifier: Modifier = Modifier,
    startDestination: String,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val navController = router.navController

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = permissionRoute) {
            PermissionScreen(
                showSnackBar = showSnackBar,
                navigateToHome = navController::navigateToHome
            )
        }

        homeNavGraph(
            navigateToCalendar = navController::navigateToCalendar,
            popBackStack = navController::popBackStackIfCan,
            showSnackBar = showSnackBar,
            navigateToHomeSetting = navController::navigateToHomeSetting,
        )

        authNavGraph(
            navigateToSignUp = navController::navigateToSignUp,
            navigateToSignUpDetail = navController::navigateToSignUpDetail,
            navigateToFindId = navController::navigateToFindId,
            navigateToFindPassword = navController::navigateToFindPassword,
            popBackStack = navController::popBackStackIfCan,
            backStackToHome = {
                val navOptions = navOptions {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
                navController.navigateToHome(navOptions)
            },
            showSnackBar = showSnackBar
        )

        rankingNavGraph(
            navigateToRanking = {
                val navOptions = navOptions {
                    popUpTo(rankingRoute) {
                        inclusive = true
                    }
                }
                navController.navigateToRanking(navOptions)
            },
            popBackStack = navController::popBackStackIfCan,
            showSnackBar = showSnackBar,
            navigateToRankingUserDetail = navController::navigateToRankingUserDetail,
            navigateToLogin = navController::navigateToLogin,
            navigateToNoti = navController::navigateToNotification,
        )

        missionNavGraph(
            navigateToMissionDetail = navController::navigateToMissionDetail,
            navigateToLogin = navController::navigateToLogin,
            popBackStack = navController::popBackStackIfCan,
        )

        profileNavigation(
            navigateToProfile = {
                val navOptions = navOptions {
                    popUpTo(profileRoute) {
                        inclusive = true
                    }
                }
                navController.navigateToProfile(navOptions)
            },
            navigateToEditUser = navController::navigateToEditUser,
            navigateToTerms = navController::navigateToTerms,
            navigateToLogin = navController::navigateToLogin,
            popBackStack = navController::popBackStackIfCan,
            showSnackBar = showSnackBar
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
internal fun NavigationSuiteScope.stepMateNavigationSuiteItems(
    currentDestination: NavDestination?,
    itemColors: NavigationSuiteItemColors,
    onClick: (NavigationDestination) -> Unit,
) {
    NavigationDestination.entries.forEach { destination ->
        val selected = currentDestination.isDestinationInHierarchy(destination)

        item(
            selected = selected,
            onClick = { onClick(destination) },
            icon = {
                if (!selected)
                    Icon(
                        imageVector = ImageVector.vectorResource(id = destination.icon),
                        contentDescription = "clickIcon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                else
                    Icon(
                        imageVector = ImageVector.vectorResource(id = destination.iconClicked),
                        contentDescription = "clickedIcon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
            },
            colors = itemColors,
        )
    }
}

@Immutable
internal object NavigationDefaults {
    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.surface

    @Composable
    fun containerColor() = MaterialTheme.colorScheme.surface

    @Composable
    fun contentColor() = MaterialTheme.colorScheme.onSurface
}