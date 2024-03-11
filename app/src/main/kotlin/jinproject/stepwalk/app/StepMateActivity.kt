package jinproject.stepwalk.app

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
import dagger.hilt.android.AndroidEntryPoint
import jinproject.stepwalk.app.ui.StepMateViewModel
import jinproject.stepwalk.app.ui.navigation.NavigationDefaults
import jinproject.stepwalk.app.ui.navigation.NavigationGraph
import jinproject.stepwalk.app.ui.navigation.Router
import jinproject.stepwalk.app.ui.navigation.isShownBar
import jinproject.stepwalk.app.ui.navigation.permission.permissionRoute
import jinproject.stepwalk.app.ui.navigation.stepMateNavigationSuiteItems
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.navigation.homeGraph
import jinproject.stepwalk.home.navigation.homeRoute
import jinproject.stepwalk.home.navigation.homeUserBody
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
                                StepWalkTheme {
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
            }
        }
    }
}