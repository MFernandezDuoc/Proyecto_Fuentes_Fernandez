package com.panalbit.panalsuite.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.panalbit.panalsuite.data.local.PanalSuiteDatabase
import com.panalbit.panalsuite.data.local.dao.*
import com.panalbit.panalsuite.data.remote.api.*
import com.panalbit.panalsuite.data.repository.*
import com.panalbit.panalsuite.domain.repository.*
import com.panalbit.panalsuite.util.ConnectivityHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.panalbit.example.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideTicketApiService(retrofit: Retrofit): TicketApiService =
        retrofit.create(TicketApiService::class.java)

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideDashboardApiService(retrofit: Retrofit): DashboardApiService =
        retrofit.create(DashboardApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PanalSuiteDatabase {
        return Room.databaseBuilder(
            context,
            PanalSuiteDatabase::class.java,
            "panalsuite.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides fun provideTicketDao(db: PanalSuiteDatabase): TicketDao = db.ticketDao()
    @Provides fun provideCommentDao(db: PanalSuiteDatabase): CommentDao = db.commentDao()
    @Provides fun provideEvidenceDao(db: PanalSuiteDatabase): EvidenceDao = db.evidenceDao()
    @Provides fun provideUserDao(db: PanalSuiteDatabase): UserDao = db.userDao()
    @Provides fun provideDashboardStatsDao(db: PanalSuiteDatabase): DashboardStatsDao = db.dashboardStatsDao()
    @Provides fun providePendingOperationDao(db: PanalSuiteDatabase): PendingOperationDao = db.pendingOperationDao()
}

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("panalsuite_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideConnectivityHelper(@ApplicationContext context: Context): ConnectivityHelper {
        return ConnectivityHelper(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTicketRepository(impl: TicketRepositoryImpl): TicketRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository
}
