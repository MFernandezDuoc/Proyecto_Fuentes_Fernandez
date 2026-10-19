package com.panalbit.panalsuite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.panalbit.panalsuite.data.local.dao.*
import com.panalbit.panalsuite.data.local.entity.*

@Database(
    entities = [
        TicketEntity::class,
        CommentEntity::class,
        EvidenceEntity::class,
        UserEntity::class,
        DashboardStatsEntity::class,
        PendingOperationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PanalSuiteDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketDao
    abstract fun commentDao(): CommentDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun userDao(): UserDao
    abstract fun dashboardStatsDao(): DashboardStatsDao
    abstract fun pendingOperationDao(): PendingOperationDao
}
