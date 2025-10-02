package com.davidniederweis.mealier.data.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.davidniederweis.mealier.util.Logger

class BiometricAuthHelper(private val context: Context) {

    fun canAuthenticateWithBiometrics(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Logger.d("BiometricAuth", "Device supports biometric authentication")
                true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Logger.w("BiometricAuth", "No biometric hardware available")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Logger.w("BiometricAuth", "Biometric hardware unavailable")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Logger.w("BiometricAuth", "No biometric credentials enrolled")
                false
            }
            else -> {
                Logger.w("BiometricAuth", "Biometric authentication not available")
                false
            }
        }
    }

    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Logger.d("BiometricAuth", "Starting biometric authentication")

        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Logger.i("BiometricAuth", "Authentication succeeded")
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Logger.e("BiometricAuth", "Authentication error: $errString (code: $errorCode)")
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Logger.w("BiometricAuth", "Authentication failed")
                    onError("Authentication failed")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate to access your account")
            .setNegativeButtonText("Use password")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun isBiometricEnabled(): Boolean {
        val prefs = context.getSharedPreferences("biometric_prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("biometric_enabled", false)
        Logger.d("BiometricAuth", "Biometric enabled: $enabled")
        return enabled
    }

    fun setBiometricEnabled(enabled: Boolean) {
        Logger.i("BiometricAuth", "Setting biometric enabled: $enabled")
        val prefs = context.getSharedPreferences("biometric_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
    }
}
