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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.stepmate.app.ui.StepMateViewModel
import com.stepmate.app.ui.navigation.NavigationDefaults
import com.stepmate.app.ui.navigation.NavigationGraph
import com.stepmate.app.ui.navigation.Router
import com.stepmate.app.ui.navigation.isShownBar
import com.stepmate.app.ui.navigation.stepMateNavigationSuiteItems
import com.stepmate.design.component.StepMateSnackBar
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.home.service.StepException
import com.stepmate.login.navigation.navigateToLogin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StepMateActivity : ComponentActivity() {

    private val stepMateViewModel: StepMateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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

        val isNeedReLogin by stepMateViewModel.isNeedReLogin.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = isNeedReLogin) {
            if (isNeedReLogin) {
                navController.navigateToLogin(null)
                stepMateViewModel.updateIsNeedLogin(false)
            }
        }

        val navBarItemColors = NavigationBarItemDefaults.colors(
            indicatorColor = NavigationDefaults.navigationIndicatorColor()
        )
        val railBarItemColors = NavigationRailItemDefaults.colors(
            indicatorColor = NavigationDefaults.navigationIndicatorColor()
        )
        val drawerItemColors = NavigationDrawerItemDefaults.colors()

        val navigationSuiteItemColors = remember {
            NavigationSuiteItemColors(
                navigationBarItemColors = navBarItemColors,
                navigationRailItemColors = railBarItemColors,
                navigationDrawerItemColors = drawerItemColors,
            )
        }

        val currentDestination by rememberUpdatedState(newValue = router.currentDestination)
        val currentWindowAdaptiveInfo =
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

        val layoutType by rememberUpdatedState(
            newValue = if (!currentDestination.isShownBar())
                NavigationSuiteType.None
            else
                currentWindowAdaptiveInfo
        )

        val startDestinationInfo by stepMateViewModel.startDestinationInfo.collectAsStateWithLifecycle()

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
                        modifier = Modifier,
                        paddingValues = paddingValues,
                        router = router,
                        startDestinationInfo = startDestinationInfo,
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
                }
            }
        }
    }
}