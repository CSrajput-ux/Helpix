package com.healthai.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.healthai.app.data.local.entity.SkinScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: SkinScanEntity)

    @Query("SELECT * FROM skin_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<SkinScanEntity>>

    @Query("SELECT * FROM skin_scans WHERE id = :id")
    suspend fun getScanById(id: Int): SkinScanEntity?
}
