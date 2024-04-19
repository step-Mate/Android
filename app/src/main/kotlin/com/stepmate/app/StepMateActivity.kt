package com.stepmate.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.LocalTonalElevationEnabled
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.stepmate.app.ui.StepMateViewModel
import com.stepmate.app.ui.navigation.NavigationDefaults
import com.stepmate.app.ui.navigation.NavigationGraph
import com.stepmate.app.ui.navigation.Router
import com.stepmate.app.ui.navigation.isShownBar
import com.stepmate.app.ui.navigation.permission.permissionRoute
import com.stepmate.app.ui.navigation.stepMateNavigationSuiteItems
import com.stepmate.design.component.StepMateSnackBar
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.home.navigation.homeGraph
import com.stepmate.home.navigation.homeRoute
import com.stepmate.home.navigation.homeUserBody
import com.stepmate.home.service.StepException
import com.stepmate.login.navigation.navigateToLogin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StepMateActivity : ComponentActivity() {

    private val stepMateViewModel: StepMateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val content: View = findViewById(android.R.id.content)

        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return run callback@{
                        lifecycleScope.launch {
                            stepMateViewModel.checkPermission()
                            setContent {
                                StepMateTheme {
                                    StepMateApp()
                                }
                            }
                            content.viewTreeObserver.removeOnPreDrawListener(this@callback)
                        }.isCompleted
                    }
                }
            }
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val isNeedReLogin = intent?.getStringExtra(StepException.NEED_RE_LOGIN)

        isNeedReLogin?.let {
            stepMateViewModel.updateIsNeedLogin(true)
        }
    }

    @OptIn(
        ExperimentalMaterial3AdaptiveNavigationSuiteApi::class,
        ExperimentalMaterial3AdaptiveApi::class
    )
    @Composable
    private fun StepMateApp(
        coroutineScope: CoroutineScope = rememberCoroutineScope(),
    ) {
        val navController = rememberNavController()
        val router = remember(navController) { Router(navController) }
        val snackBarHostState = remember { SnackbarHostState() }

        val permissionState by stepMateViewModel.permissionState.collectAsStateWithLifecycle()
        val isBodyDataExist by stepMateViewModel.isBodyDataExist.collectAsStateWithLifecycle()
        val isNeedReLogin by stepMateViewModel.isNeedReLogin.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = isNeedReLogin,) {
            if(isNeedReLogin) {
                navController.navigateToLogin(null)
                stepMateViewModel.updateIsNeedLogin(false)
            }
        }

        val currentDestination = router.currentDestination
        val navigationSuiteItemColors = NavigationSuiteItemColors(
            navigationBarItemColors = NavigationBarItemDefaults.colors(
                indicatorColor = NavigationDefaults.navigationIndicatorColor()
            ),
            navigationRailItemColors = NavigationRailItemDefaults.colors(
                indicatorColor = NavigationDefaults.navigationIndicatorColor()
            ),
            navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
        )
        val currentWindowAdaptiveInfo =
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

        val layoutType by rememberUpdatedState(
            newValue = if (!currentDestination.isShownBar())
                NavigationSuiteType.None
            else
                currentWindowAdaptiveInfo
        )

        CompositionLocalProvider(value = LocalTonalElevationEnabled provides false) {
            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    stepMateNavigationSuiteItems(
                        currentDestination = currentDestination,
                        itemColors = navigationSuiteItemColors,
                        onClick = { destination ->
                            router.navigateTopLevelDestination(destination)
                        }
                    )
                },
                layoutType = layoutType,
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                navigationSuiteColors = NavigationSuiteDefaults.colors(
                    navigationBarContainerColor = NavigationDefaults.containerColor(),
                    navigationBarContentColor = NavigationDefaults.contentColor(),
                    navigationRailContainerColor = NavigationDefaults.containerColor(),
                    navigationRailContentColor = NavigationDefaults.contentColor(),
                    navigationDrawerContainerColor = NavigationDefaults.containerColor(),
                    navigationDrawerContentColor = NavigationDefaults.contentColor(),
                ),
            ) {
                Scaffold(
                    snackbarHost = {
                        StepMateSnackBar(
                            headerMessage = snackBarHostState.currentSnackbarData?.message ?: "",
                            contentMessage = snackBarHostState.currentSnackbarData?.actionLabel
                                ?: "",
                            snackBarHostState = snackBarHostState,
                            dismissSnackBar = { snackBarHostState.currentSnackbarData?.dismiss() })
                    }
                ) { paddingValues ->
                    NavigationGraph(
                        router = router,
                        modifier = Modifier,
                        startDestination = if (permissionState) homeGraph else permissionRoute,
                        homeStartDestination = if (isBodyDataExist) homeRoute else homeUserBody,
                        showSnackBar = { snackBarMessage ->
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(
                                    message = snackBarMessage.headerMessage,
                                    actionLabel = snackBarMessage.contentMessage,
                                    duration = SnackbarDuration.Indefinite
                                )
                            }
                        }
                    )
                    paddingValues
                }
            }
        }
    }
}