package com.ash.flowr.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ash.flowr.data.local.entity.HoldingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingDao {

    @Query("SELECT * FROM holdings ORDER BY symbol ASC")
    fun observeAll(): Flow<List<HoldingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(holding: HoldingEntity): Long

    @Update
    suspend fun update(holding: HoldingEntity)

    @Delete
    suspend fun delete(holding: HoldingEntity)
}
