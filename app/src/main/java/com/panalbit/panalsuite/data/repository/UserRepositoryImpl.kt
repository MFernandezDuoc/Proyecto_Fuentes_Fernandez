package com.panalbit.panalsuite.data.repository

import com.panalbit.panalsuite.domain.model.User
import com.panalbit.panalsuite.domain.model.UserRole
import com.panalbit.panalsuite.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val usersList = mutableListOf(
        User("u1", "solicitante", "Juan Solicitante", "juan@panalbit.cl", UserRole.SOLICITANTE),
        User("u2", "resolutor", "Pedro Resolutor", "pedro@panalbit.cl", UserRole.RESOLUTOR),
        User("u3", "supervisor", "Maria Supervisora", "maria@panalbit.cl", UserRole.SUPERVISOR),
        User("u4", "gerencia", "Carlos Gerente", "carlos@panalbit.cl", UserRole.GERENCIA),
        User("u5", "admin", "Admin Sistema", "admin@panalbit.cl", UserRole.ADMINISTRADOR)
    )

    private val usersFlow = MutableStateFlow<List<User>>(usersList)

    override fun getUsers(): Flow<List<User>> = usersFlow

    override suspend fun getUserById(id: String): Result<User> = runCatching {
        usersList.find { it.id == id } ?: throw NoSuchElementException("Usuario no encontrado")
    }

    override suspend fun createUser(user: User): Result<User> = runCatching {
        usersList.add(user)
        usersFlow.value = usersList.toList()
        user
    }

    override suspend fun updateUser(user: User): Result<User> = runCatching {
        val index = usersList.indexOfFirst { it.id == user.id }
        if (index != -1) usersList[index] = user
        usersFlow.value = usersList.toList()
        user
    }

    override suspend fun deactivateUser(userId: String): Result<Unit> = runCatching {
        val index = usersList.indexOfFirst { it.id == userId }
        if (index != -1) {
            usersList[index] = usersList[index].copy(isActive = false)
            usersFlow.value = usersList.toList()
        }
    }
}
