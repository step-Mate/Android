package com.stepmate.app.ui.navigation.permission

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.stepmate.app.StepMateActivity
import com.stepmate.design.component.StepMateSnackBar
import com.stepmate.design.theme.StepMateTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PermissionActivity : ComponentActivity() {
    private val permissionViewModel: PermissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val content: View = findViewById(android.R.id.content)

        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return run callback@{
                        lifecycleScope.launch {
                            permissionViewModel.checkPermission()
                            setContent {
                                StepMateTheme {
                                    PermissionApp()
                                }
                            }
                            content.viewTreeObserver.removeOnPreDrawListener(this@callback)
                        }.isCompleted
                    }
                }
            }
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            PermissionViewModel.ACTIVITY_RECOGNITION_CODE ->
                permissionViewModel.onPermissionResult(
                    PermissionViewModel.Permission.ACTIVITY_RECOGNITION,
                    grantResults.first() == PackageManager.PERMISSION_GRANTED,
                    this
                )

            PermissionViewModel.HEALTH_CONNECT_CODE ->
                permissionViewModel.onPermissionResult(
                    PermissionViewModel.Permission.HEALTH_CONNECT,
                    grantResults.all { r -> r == PackageManager.PERMISSION_GRANTED },
                    this
                )
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Composable
    private fun PermissionApp(
        coroutineScope: CoroutineScope = rememberCoroutineScope(),
    ) {
        val snackBarHostState = remember { SnackbarHostState() }
        val isBodyDataExist by permissionViewModel.isBodyDataExist.collectAsStateWithLifecycle()

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
            PermissionScreen(
                modifier = Modifier.padding(paddingValues),
                permissionViewModel = permissionViewModel,
                showSnackBar = { snackBarMessage ->
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = snackBarMessage.headerMessage,
                            actionLabel = snackBarMessage.contentMessage,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                },
                navigateToHomeGraph = {
                    startActivity(Intent(this, StepMateActivity::class.java).apply {
                        putExtra("isBodyDataExist", isBodyDataExist)
                    })

                    finish()
                }
            )
        }
    }
}