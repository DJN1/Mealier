package com.davidniederweis.mealier.data.model.user

import kotlinx.serialization.Serializable

@Serializable
data class CreateInvitationRequest(val uses: Int)

@Serializable
data class InvitationResponse(val token: String)

@Serializable
data class SendInvitationEmailRequest(val email: String, val token: String)
