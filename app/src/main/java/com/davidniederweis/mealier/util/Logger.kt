package com.davidniederweis.mealier.util

import android.util.Log
import com.davidniederweis.mealier.BuildConfig
import timber.log.Timber

object Logger {
    private const val TAG = "Mealier"

    enum class Level(val priority: Int) {
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR),
        NONE(Int.MAX_VALUE)
    }

    private val currentLevel: Level by lazy {
        when (BuildConfig.LOG_LEVEL.uppercase()) {
            "VERBOSE" -> Level.VERBOSE
            "DEBUG" -> Level.DEBUG
            "INFO" -> Level.INFO
            "WARN" -> Level.WARN
            "ERROR" -> Level.ERROR
            "NONE" -> Level.NONE
            else -> Level.DEBUG
        }
    }

    fun v(tag: String = TAG, message: String, throwable: Throwable? = null) {
        if (currentLevel.priority <= Level.VERBOSE.priority) {
            if (throwable != null) {
                Timber.tag(tag).v(throwable, message)
            } else {
                Timber.tag(tag).v(message)
            }
        }
    }

    fun d(tag: String = TAG, message: String, throwable: Throwable? = null) {
        if (currentLevel.priority <= Level.DEBUG.priority) {
            if (throwable != null) {
                Timber.tag(tag).d(throwable, message)
            } else {
                Timber.tag(tag).d(message)
            }
        }
    }

    fun i(tag: String = TAG, message: String, throwable: Throwable? = null) {
        if (currentLevel.priority <= Level.INFO.priority) {
            if (throwable != null) {
                Timber.tag(tag).i(throwable, message)
            } else {
                Timber.tag(tag).i(message)
            }
        }
    }

    fun w(tag: String = TAG, message: String, throwable: Throwable? = null) {
        if (currentLevel.priority <= Level.WARN.priority) {
            if (throwable != null) {
                Timber.tag(tag).w(throwable, message)
            } else {
                Timber.tag(tag).w(message)
            }
        }
    }

    fun e(tag: String = TAG, message: String, throwable: Throwable? = null) {
        if (currentLevel.priority <= Level.ERROR.priority) {
            if (throwable != null) {
                Timber.tag(tag).e(throwable, message)
            } else {
                Timber.tag(tag).e(message)
            }
        }
    }

    // Network specific logging
    fun logRequest(url: String, method: String, headers: Map<String, String> = emptyMap()) {
        d("Network", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        d("Network", "→ $method $url")
        if (headers.isNotEmpty()) {
            d("Network", "Headers:")
            headers.forEach { (key, value) ->
                // Mask sensitive data
                val displayValue = if (key.equals("Authorization", ignoreCase = true)) {
                    value.take(20) + "..." + value.takeLast(10)
                } else {
                    value
                }
                d("Network", "  $key: $displayValue")
            }
        }
        d("Network", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }

    fun logResponse(url: String, code: Int, message: String, body: String? = null) {
        d("Network", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        d("Network", "← $code $message")
        d("Network", "   $url")
        if (body != null && body.length < 1000) {
            d("Network", "Response Body: $body")
        } else if (body != null) {
            d("Network", "Response Body: ${body.take(500)}... (truncated)")
        }
        d("Network", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }

    fun logImageLoad(url: String, recipeId: String, imagePath: String) {
        d("Images", "🖼️  Loading image:")
        d("Images", "   Recipe ID: $recipeId")
        d("Images", "   Image Path: $imagePath")
        d("Images", "   Full URL: $url")
    }

    fun logImageSuccess(url: String) {
        i("Images", "✅ Image loaded successfully: $url")
    }

    fun logImageError(url: String, error: Throwable) {
        e("Images", "❌ Image load failed: $url", error)
    }
}
