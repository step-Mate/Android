package jinproject.stepwalk.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jinproject.stepwalk.app.ui.core.SnackBarMessage
import jinproject.stepwalk.app.ui.navigation.BottomNavigationGraph
import jinproject.stepwalk.app.ui.navigation.NavigationGraph
import jinproject.stepwalk.app.ui.navigation.Router
import jinproject.stepwalk.design.component.SnackBarHostCustom
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.HealthConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StepWalkActivity : ComponentActivity() {

    @Inject
    lateinit var healthConnector: HealthConnector

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.filter { it.value.not() }.isNotEmpty()) {
            Toast.makeText(
                applicationContext,
                "권한 설정에 동의하지 않으시면 이용하실 수 없습니다.",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        permissionLauncher.launch(PERMISSIONS)

        WindowCompat.setDecorFitsSystemWindows(window, false)

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
        val router = remember(navController) { Router(navController) }

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
                        modifier = Modifier,
                    )
                },
                snackbarHost = {
                    SnackBarHostCustom(headerMessage = snackBarHostState.currentSnackbarData?.message
                        ?: "",
                        contentMessage = snackBarHostState.currentSnackbarData?.actionLabel ?: "",
                        snackBarHostState = snackBarHostState,
                        disMissSnackBar = { snackBarHostState.currentSnackbarData?.dismiss() })
                }
            ) { paddingValues ->
                NavigationGraph(
                    router = router,
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
        val PERMISSIONS = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACTIVITY_RECOGNITION
            )

            else -> arrayOf(
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }
    }
}