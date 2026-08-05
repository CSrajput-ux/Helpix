package com.healthai.app.di

import android.app.Application
import androidx.room.Room
import com.healthai.app.data.local.dao.HealthMetricDao
import com.healthai.app.data.local.dao.ReminderDao
import com.healthai.app.data.local.dao.UserDao
import com.healthai.app.data.local.database.HelpixDatabase
import com.healthai.app.data.remote.api.HelpixApi
import com.healthai.app.data.remote.api.HelpixRetrofitClient
import com.healthai.app.data.remote.api.TokenStore
import com.healthai.app.data.repository.AppointmentRepository
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── Room Database ──────────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideHelpixDatabase(app: Application): HelpixDatabase {
        return Room.databaseBuilder(
            app,
            HelpixDatabase::class.java,
            "helpix_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideHealthMetricDao(db: HelpixDatabase): HealthMetricDao = db.healthMetricDao()

    @Provides
    @Singleton
    fun provideUserDao(db: HelpixDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideReminderDao(db: HelpixDatabase): ReminderDao = db.reminderDao()

    // ── Retrofit / Remote API ──────────────────────────────────────────────────

    /**
     * Single HelpixApi instance with auth + token-refresh + retry interceptors.
     * FIX #6: Uses the TokenStore in-memory cache — no runBlocking on network thread.
     */
    @Provides
    @Singleton
    fun provideHelpixApi(app: Application): HelpixApi {
        return HelpixRetrofitClient.create(BASE_URL, app)
    }

    // ── Repositories (FIX #2 & #2b: injected via Hilt, not newed-up) ──────────

    /**
     * FIX #2: UserRepository now takes HelpixApi — routes through backend,
     * not Firebase Firestore.
     */
    @Provides
    @Singleton
    fun provideUserRepository(api: HelpixApi): UserRepository = UserRepository(api)

    /**
     * FIX #2b: AppointmentRepository now takes HelpixApi — routes through
     * backend, not Firebase Firestore.
     */
    @Provides
    @Singleton
    fun provideAppointmentRepository(api: HelpixApi): AppointmentRepository =
        AppointmentRepository(api)
}
