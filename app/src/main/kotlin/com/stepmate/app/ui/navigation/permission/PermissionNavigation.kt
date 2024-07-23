package com.stepmate.app.ui.navigation.permission

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.stepmate.app.ui.navigation.permission.PermissionRoute.permissionGraph
import com.stepmate.app.ui.navigation.permission.PermissionRoute.permissionRoute
import com.stepmate.core.SnackBarMessage

object PermissionRoute {
    const val permissionGraph = "permissionGraph"
    const val permissionRoute = "permissionRoute"
}

fun NavGraphBuilder.permissionNavGraph(
    paddingValues: PaddingValues,
    showSnackBar: (SnackBarMessage) -> Unit,
    navigateToHomeGraph: (NavOptions?) -> Unit,
) {
    navigation(
        route = permissionGraph,
        startDestination = permissionRoute,
    ) {
        composable(route = permissionRoute) {
            PermissionScreen(
                modifier = Modifier.padding(paddingValues),
                showSnackBar = showSnackBar,
                navigateToHomeGraph = {
                    navigateToHomeGraph(
                        navOptions {
                            popUpTo(permissionRoute) {
                                inclusive = true
                            }
                        }
                    )
                },
            )
        }
    }
}