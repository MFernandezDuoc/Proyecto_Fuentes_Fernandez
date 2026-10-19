package com.panalbit.panalsuite.data.local.dao

import androidx.room.*
import com.panalbit.panalsuite.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE id = :id")
    suspend fun getById(id: String): TicketEntity?

    @Upsert
    suspend fun upsertAll(tickets: List<TicketEntity>)

    @Upsert
    suspend fun upsert(ticket: TicketEntity)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE ticketId = :ticketId ORDER BY createdAt ASC")
    fun getByTicketId(ticketId: String): Flow<List<CommentEntity>>

    @Upsert
    suspend fun upsertAll(comments: List<CommentEntity>)
}

@Dao
interface EvidenceDao {
    @Query("SELECT * FROM evidences WHERE ticketId = :ticketId")
    fun getByTicketId(ticketId: String): Flow<List<EvidenceEntity>>

    @Query("SELECT * FROM evidences WHERE isLocal = 1")
    suspend fun getLocalPending(): List<EvidenceEntity>

    @Upsert
    suspend fun upsertAll(evidences: List<EvidenceEntity>)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Upsert
    suspend fun upsertAll(users: List<UserEntity>)
}

@Dao
interface DashboardStatsDao {
    @Query("SELECT * FROM dashboard_stats WHERE id = 1")
    fun getStats(): Flow<DashboardStatsEntity?>

    @Upsert
    suspend fun upsert(stats: DashboardStatsEntity)
}

@Dao
interface PendingOperationDao {
    @Query("SELECT * FROM pending_operations WHERE status = 'PENDING'")
    fun getAllPending(): Flow<List<PendingOperationEntity>>

    @Query("SELECT * FROM pending_operations WHERE status = 'PENDING'")
    suspend fun getPendingList(): List<PendingOperationEntity>

    @Upsert
    suspend fun insert(operation: PendingOperationEntity)

    @Query("DELETE FROM pending_operations WHERE id = :id")
    suspend fun delete(id: String)
}
