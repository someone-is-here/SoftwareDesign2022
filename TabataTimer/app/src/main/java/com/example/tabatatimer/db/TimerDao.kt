package com.example.tabatatimer.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tabatatimer.db.relations.TrainingWithTimers
import java.util.*

@Dao
interface TimerDao {
    @Insert
    suspend fun insertTimer(timer: TimerItem)

    @Update
    suspend fun updateTimer(timer: TimerItem)

    @Delete
    suspend fun deleteTimer(timer: TimerItem)

    @Transaction
    @Query("SELECT * FROM timer_items_data_table WHERE training_id =:training_id ORDER BY timer_priority ASC")
    fun getAllTrainingWithTimers(training_id: Int): LiveData<List<TimerItem>>

}