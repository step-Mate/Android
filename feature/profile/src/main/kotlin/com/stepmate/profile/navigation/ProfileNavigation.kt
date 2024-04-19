package com.stepmate.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.stepmate.core.SnackBarMessage
import com.stepmate.core.slideLeftIn
import com.stepmate.core.slideRightOut
import com.stepmate.design.component.TermsScreen
import com.stepmate.profile.screen.edit.EditScreen
import com.stepmate.profile.screen.profile.ProfileScreen

const val profileGraph = "profileGraph"
const val profileRoute = "profile"
private const val editRoute = "editUser"
private const val editLink = "$editRoute/{nickname}/{anonymous}/{designation}"
private const val termsRoute = "terms"

fun NavGraphBuilder.profileNavigation(
    navigateToProfile: () -> Unit,
    navigateToEditUser: (String, String, Boolean) -> Unit,
    navigateToTerms: () -> Unit,
    navigateToLogin: (NavOptions?) -> Unit,
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    navigation(
        startDestination = profileRoute,
        route = profileGraph
    ) {
        composable(
            route = profileRoute
        ) {
            ProfileScreen(
                navigateToEditUser = navigateToEditUser,
                navigateToTerms = navigateToTerms,
                navigateToLogin = navigateToLogin,
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = editLink,
            enterTransition = slideLeftIn(500),
            exitTransition = slideRightOut(700),
            arguments = listOf(
                navArgument("nickname") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("anonymous") {
                    type = NavType.BoolType
                },
                navArgument("designation") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            EditScreen(
                navigateToProfile = navigateToProfile,
                popBackStack = popBackStack,
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = termsRoute,
            enterTransition = slideLeftIn(500),
            exitTransition = slideRightOut(700)
        ) {
            TermsScreen(
                popBackStack = popBackStack
            )
        }

    }
}

fun NavController.navigateToProfile(navOptions: NavOptions?) {
    this.navigate(profileRoute, navOptions = navOptions)
}

fun NavController.navigateToEditUser(nickname: String, designation: String, anonymous: Boolean) {
    this.navigate("$editRoute/$nickname/$anonymous/${designation.ifEmpty { "-1" }}") {
        launchSingleTop = true
    }
}

fun NavController.navigateToTerms() {
    this.navigate(termsRoute) {
        launchSingleTop = true
    }
}