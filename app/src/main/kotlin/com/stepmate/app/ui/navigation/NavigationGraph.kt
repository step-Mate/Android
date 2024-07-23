package com.stepmate.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
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
import androidx.navigation.navOptions
import com.stepmate.app.ui.StartDestinationInfo
import com.stepmate.app.ui.navigation.permission.PermissionRoute
import com.stepmate.app.ui.navigation.permission.permissionNavGraph
import com.stepmate.core.SnackBarMessage
import com.stepmate.home.navigation.HomeRoute.homeGraph
import com.stepmate.home.navigation.homeNavGraph
import com.stepmate.home.navigation.navigateToCalendar
import com.stepmate.home.navigation.navigateToHome
import com.stepmate.home.navigation.navigateToHomeGraph
import com.stepmate.home.navigation.navigateToHomeSetting
import com.stepmate.login.navigation.authNavGraph
import com.stepmate.login.navigation.navigateToFindId
import com.stepmate.login.navigation.navigateToFindPassword
import com.stepmate.login.navigation.navigateToInformation
import com.stepmate.login.navigation.navigateToInformationTerms
import com.stepmate.login.navigation.navigateToLogin
import com.stepmate.login.navigation.navigateToSignUp
import com.stepmate.login.navigation.navigateToSignUpDetail
import com.stepmate.mission.navigation.missionNavGraph
import com.stepmate.mission.navigation.navigateToMissionDetail
import com.stepmate.profile.navigation.navigateToEditUser
import com.stepmate.profile.navigation.navigateToProfile
import com.stepmate.profile.navigation.navigateToTerms
import com.stepmate.profile.navigation.profileNavigation
import com.stepmate.profile.navigation.profileRoute
import com.stepmate.ranking.navigation.navigateToNotification
import com.stepmate.ranking.navigation.navigateToRanking
import com.stepmate.ranking.navigation.navigateToRankingUserDetail
import com.stepmate.ranking.navigation.rankingNavGraph
import com.stepmate.ranking.navigation.rankingRoute

@Composable
internal fun NavigationGraph(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    router: Router,
    startDestinationInfo: StartDestinationInfo,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val navController = router.navController

    NavHost(
        navController = navController,
        startDestination = if (startDestinationInfo.hasPermission) homeGraph else PermissionRoute.permissionGraph,
        modifier = modifier
    ) {
        permissionNavGraph(
            paddingValues = paddingValues,
            showSnackBar = showSnackBar,
            navigateToHomeGraph = navController::navigateToHomeGraph,
        )

        homeNavGraph(
            hasBodyData = startDestinationInfo.hasBodyData,
            navigateToCalendar = navController::navigateToCalendar,
            popBackStack = navController::popBackStackIfCan,
            showSnackBar = showSnackBar,
            navigateToHomeSetting = navController::navigateToHomeSetting,
            navigateToHome = navController::navigateToHome,
        )

        authNavGraph(
            navigateToInformation = navController::navigateToInformation,
            navigateToTerms = navController::navigateToInformationTerms,
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