package com.davidniederweis.mealier.data.model.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val admin: Boolean,
    val canInvite: Boolean,
    val canManage: Boolean,
    val canManageHousehold: Boolean,
    val canOrganize: Boolean,
)
