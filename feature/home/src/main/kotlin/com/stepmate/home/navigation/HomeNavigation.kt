package com.stepmate.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.stepmate.core.SnackBarMessage
import com.stepmate.home.screen.calendar.CalendarScreen
import com.stepmate.home.screen.home.HomeScreen
import com.stepmate.home.screen.homeSetting.HomeSettingScreen
import com.stepmate.home.screen.homeUserBody.HomeUserBodyScreen

const val homeGraph = "homeGraph"
const val homeRoute = "home"
const val homeUserBody = "homeUserBody"
private const val calendarRoute = "calendar"
private const val calendarLink = "$calendarRoute/{start}"
private const val homeSettingRoute = "homeSetting"

fun NavGraphBuilder.homeNavGraph(
    startDestination: String,
    navigateToCalendar: (Long) -> Unit,
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    navigateToHomeSetting: () -> Unit,
    navigateToHome: (NavOptions?) -> Unit,
) {
    navigation(
        route = homeGraph,
        startDestination = startDestination
    ) {
        composable(route = homeUserBody) {
            HomeUserBodyScreen(
                navigateToHome = navigateToHome,
            )
        }
        composable(route = homeRoute) {
            HomeScreen(
                navigateToCalendar = navigateToCalendar,
                showSnackBar = showSnackBar,
                navigateToHomeSetting = navigateToHomeSetting,
            )
        }
        composable(
            route = calendarLink,
            arguments = listOf(navArgument("start") { type = NavType.LongType })
        ) {
            CalendarScreen(
                popBackStack = popBackStack,
                showSnackBar = showSnackBar,
            )
        }
        composable(
            route = homeSettingRoute,
        ) {
            HomeSettingScreen(
                popBackStack = popBackStack,
                showSnackBar = showSnackBar,
            )
        }
    }
}

fun NavController.navigateToHome(navOptions: NavOptions?) {
    this.navigate(homeRoute, navOptions = navOptions)
}

fun NavController.navigateToCalendar(time: Long) {
    this.navigate("$calendarRoute/$time")
}

fun NavController.navigateToHomeSetting() {
    this.navigate(homeSettingRoute)
}

fun NavController.navigateToHomeGraph(navOptions: NavOptions?) {
    this.navigate(homeGraph, navOptions = navOptions)
}