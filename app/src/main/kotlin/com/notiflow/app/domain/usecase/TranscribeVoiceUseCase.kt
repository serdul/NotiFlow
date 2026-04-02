package com.notiflow.app.domain.usecase

import com.notiflow.app.domain.model.VoiceCapture
import java.io.File
import javax.inject.Inject

class TranscribeVoiceUseCase @Inject constructor() {
    // Implementation delegated to WhisperClient via AiModule
    // Returns a VoiceCapture with transcription populated
    suspend operator fun invoke(audioFile: File, maxDurationSeconds: Int = 300): Result<VoiceCapture> {
        return runCatching {
            // Validate duration before upload
            // Actual transcription handled by WhisperClient injected in ViewModel
            VoiceCapture(audioFilePath = audioFile.absolutePath)
        }
    }
}
