package com.davidniederweis.mealier.data.repository

import com.davidniederweis.mealier.data.api.UserApi
import com.davidniederweis.mealier.data.model.user.CreateInvitationRequest
import com.davidniederweis.mealier.data.model.user.SendInvitationEmailRequest
import com.davidniederweis.mealier.data.model.user.User
import com.davidniederweis.mealier.util.Logger

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun inviteUser(email: String)
    suspend fun deleteUser(userId: String)
    suspend fun updateUser(user: User)
}

class UserRepositoryImpl(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getAllUsers(): List<User> {
        Logger.i("UserRepository", "Getting all users")
        return try {
            val users = userApi.getAllUsers()
            Logger.i("UserRepository", "Successfully got all users")
            users.items
        } catch (e: Exception) {
            Logger.e("UserRepository", "Error getting all users: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun inviteUser(email: String) {
        Logger.i("UserRepository", "Inviting user with email: $email")
        try {
            val invitation = userApi.createInvitation(CreateInvitationRequest(uses = 1))
            userApi.sendInvitationEmail(SendInvitationEmailRequest(email = email, token = invitation.token))
            Logger.i("UserRepository", "Successfully invited user with email: $email")
        } catch (e: Exception) {
            Logger.e("UserRepository", "Error inviting user with email: $email, ${e.message}", e)
        }
    }

    override suspend fun deleteUser(userId: String) {
        Logger.i("UserRepository", "Deleting user with id: $userId")
        try {
            userApi.deleteUser(userId)
            Logger.i("UserRepository", "Successfully deleted user with id: $userId")
        } catch (e: Exception) {
            Logger.e("UserRepository", "Error deleting user with id: $userId, ${e.message}", e)
        }
    }

    override suspend fun updateUser(user: User) {
        Logger.i("UserRepository", "Updating user with id: ${user.id}")
        try {
            userApi.updateUser(user.id, user)
            Logger.i("UserRepository", "Successfully updated user with id: ${user.id}")
        } catch (e: Exception) {
            Logger.e("UserRepository", "Error updating user with id: ${user.id}, ${e.message}", e)
        }
    }
}
