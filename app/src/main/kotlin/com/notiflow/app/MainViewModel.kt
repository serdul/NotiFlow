package com.notiflow.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notiflow.app.domain.model.ThemePreference
import com.notiflow.app.domain.repository.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val onboardingCompleted: Boolean = false,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val hasPinSet: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val uiState: StateFlow<MainUiState> = userSettingsRepository.getUserSettings()
        .map { settings ->
            MainUiState(
                onboardingCompleted = settings.onboardingCompleted,
                themePreference = settings.themePreference,
                hasPinSet = settings.pinHash != null
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState()
        )

    private val _showPinLock = MutableStateFlow(false)
    val showPinLock: StateFlow<Boolean> = _showPinLock.asStateFlow()

    private var lastBackgroundTime = 0L
    private val pinLockTimeoutMs = 60_000L

    init {
        viewModelScope.launch {
            uiState.collect { _isLoading.value = false }
        }
    }

    fun onAppResumed() {
        val elapsed = System.currentTimeMillis() - lastBackgroundTime
        if (elapsed > pinLockTimeoutMs && uiState.value.hasPinSet && lastBackgroundTime > 0L) {
            _showPinLock.value = true
        }
    }

    fun onPinVerified() {
        _showPinLock.value = false
        lastBackgroundTime = 0L
    }
}
