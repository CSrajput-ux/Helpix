package com.healthai.app.di

import android.app.Application
import androidx.room.Room
import com.healthai.app.data.local.dao.HealthMetricDao
import com.healthai.app.data.local.dao.UserDao
import com.healthai.app.data.local.database.HealthDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.healthai.app.data.remote.api.HealthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://10.0.2.2:8000/"

    @Provides
    @Singleton
    fun provideHealthDatabase(app: Application): HealthDatabase {
        return Room.databaseBuilder(
            app,
            HealthDatabase::class.java,
            "health_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideHealthMetricDao(db: HealthDatabase): HealthMetricDao {
        return db.healthMetricDao()
    }

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

    @Provides
    @Singleton
    fun provideHealthApiService(): HealthApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HealthApiService::class.java)
    }
}