/*
NotiFlow Production Checklist:
[ ] Replace AdMob test IDs with production IDs in gradle.properties
[ ] Set up keystore and signing config for release APK
[ ] Submit Notification Listener permission justification to Google Play
[ ] Add app screenshots to Play Store listing
[ ] Host privacy policy HTML page and update URL in SettingsScreen
[ ] Test on Android 12, 13, 14, 15 devices
[ ] Enable Play App Signing
[ ] Complete Data Safety form in Play Console (declare notification access, AI data transmission)
*/
package com.notiflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notiflow.app.domain.model.ThemePreference
import com.notiflow.app.presentation.navigation.AppNavGraph
import com.notiflow.app.presentation.theme.NotiFlowTheme
import com.notiflow.app.presentation.screens.pin.PinLockScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val showPinLock by viewModel.showPinLock.collectAsStateWithLifecycle()

            NotiFlowTheme(darkTheme = when (uiState.themePreference) {
                ThemePreference.DARK -> true
                ThemePreference.LIGHT -> false
                else -> isSystemInDarkTheme()
            }) {
                if (showPinLock) {
                    PinLockScreen(onPinVerified = { viewModel.onPinVerified() })
                } else {
                    AppNavGraph(onboardingCompleted = uiState.onboardingCompleted)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onAppResumed()
    }
}

@Composable
fun isSystemInDarkTheme(): Boolean {
    return androidx.compose.foundation.isSystemInDarkTheme()
}
