package jinproject.stepwalk.app

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jinproject.stepwalk.app.ui.core.SnackBarMessage
import jinproject.stepwalk.app.ui.navigation.Router
import jinproject.stepwalk.app.ui.navigation.BottomNavigationGraph
import jinproject.stepwalk.app.ui.navigation.NavigationGraph
import jinproject.stepwalk.design.component.SnackBarHostCustom
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.HealthConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StepWalkActivity : ComponentActivity() {

    @Inject lateinit var healthConnector: HealthConnector

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.filter { it.value.not() }.isNotEmpty()) {
            Toast.makeText(
                applicationContext,
                "권한 설정에 동의하셔야 합니다.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        permissionLauncher.launch(PERMISSIONS)

        WindowCompat.setDecorFitsSystemWindows(window,false)

        setContent {
            StepWalkTheme {
                StepWalkApp()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun StepWalkApp(
        coroutineScope: CoroutineScope = rememberCoroutineScope()
    ) {
        val navController = rememberNavController()
        val router = remember(navController){ Router(navController) }

        val snackBarHostState = remember { SnackbarHostState() }

        val showSnackBar = { snackBarMessage: SnackBarMessage ->
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    message = snackBarMessage.headerMessage,
                    actionLabel = snackBarMessage.contentMessage,
                    duration = SnackbarDuration.Indefinite
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    BottomNavigationGraph(
                        router = router,
                        modifier = Modifier
                    )
                },
                snackbarHost = {
                    SnackBarHostCustom(headerMessage = snackBarHostState.currentSnackbarData?.message ?: "",
                        contentMessage = snackBarHostState.currentSnackbarData?.actionLabel ?: "",
                        snackBarHostState = snackBarHostState,
                        disMissSnackBar = { snackBarHostState.currentSnackbarData?.dismiss() })
                }
            ) { paddingValues ->
                NavigationGraph(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal
                            )
                        ),
                    healthConnector = healthConnector,
                    showSnackBar = { snackBarMessage ->
                        showSnackBar(snackBarMessage)
                    }
                )
            }
        }
    }

    companion object {
        val PERMISSIONS = when(Build.VERSION.SDK_INT >= 33) {
            true -> arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
            false -> arrayOf(
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }
    }
}