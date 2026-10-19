package com.panalbit.panalsuite.domain.repository

import com.panalbit.panalsuite.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<List<User>>
    suspend fun getUserById(id: String): Result<User>
    suspend fun createUser(user: User): Result<User>
    suspend fun updateUser(user: User): Result<User>
    suspend fun deactivateUser(userId: String): Result<Unit>
}
