package com.davidniederweis.mealier.data.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val username: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val admin: Boolean = false,
    val group: String? = null,
    val groupId: String? = null,
    val advancedRecipe: Boolean = false,
    val canInvite: Boolean = false,
    val canManage: Boolean = false,
    val canOrganize: Boolean = false
)
