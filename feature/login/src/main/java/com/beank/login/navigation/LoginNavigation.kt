package com.beank.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.beank.login.utils.slideDownOut
import com.beank.login.utils.slideUpIn
import jinproject.stepwalk.home.screen.state.SnackBarMessage
import jinproject.stepwalk.login.screen.LoginScreen

const val loginRoute = "login"

fun NavGraphBuilder.loginNavGraph(
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
){
    composable(
        route = loginRoute,
        enterTransition = slideUpIn(500),
        exitTransition = slideDownOut(500)
    ){
        LoginScreen(
            showSnackBar = showSnackBar
        )
    }
}

fun NavController.navigateToLogin(navOptions: NavOptions?) {
    this.navigate(loginRoute, navOptions = navOptions)
}