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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notiflow.app.domain.model.ThemePreference
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
            val darkTheme = when (uiState.themePreference) {
                ThemePreference.DARK -> true
                ThemePreference.LIGHT -> false
                ThemePreference.SYSTEM -> isSystemInDarkTheme()
            }

            MaterialTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
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

    override fun onPause() {
        super.onPause()
        viewModel.onAppBackgrounded()
    }
}

@Composable
private fun PinLockScreen(onPinVerified: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "App Locked", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onPinVerified) {
            Text("Unlock")
        }
    }
}

@Composable
private fun AppNavGraph(onboardingCompleted: Boolean) {
    val message = if (onboardingCompleted) "NotiFlow Home" else "Welcome to NotiFlow"
    PlaceholderScreen(message)
}

@Composable
private fun PlaceholderScreen(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.headlineSmall)
    }
}
