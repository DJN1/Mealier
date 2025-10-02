package com.davidniederweis.mealier.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    @SerialName("remember_me") val rememberMe: Boolean = true
)