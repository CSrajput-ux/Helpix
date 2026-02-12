package com.healthai.app.di

import android.app.Application
import androidx.room.Room
import com.healthai.app.data.local.dao.HealthMetricDao
import com.healthai.app.data.local.dao.UserDao
import com.healthai.app.data.local.database.HealthDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHealthDatabase(app: Application): HealthDatabase {
        return Room.databaseBuilder(
            app,
            HealthDatabase::class.java,
            "health_db"
        )
        .fallbackToDestructiveMigration() // Agar version change ho toh crash na ho, purana data clear kar de
        .build()
    }

    // --- YE MISSING THA (Fix for HealthMetricDao error) ---
    @Provides
    @Singleton
    fun provideHealthMetricDao(db: HealthDatabase): HealthMetricDao {
        return db.healthMetricDao()
    }

    // --- YE BHI MISSING THA (Fix for future UserDao error) ---
    @Provides
    @Singleton
    fun provideUserDao(db: HealthDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}