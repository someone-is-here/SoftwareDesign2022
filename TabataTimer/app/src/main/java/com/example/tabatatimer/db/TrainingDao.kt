package com.example.tabatatimer.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tabatatimer.db.relations.TrainingWithTimers
import java.util.*

@Dao
interface TrainingDao {
    @Insert
    suspend fun insertTraining(training:Training)

    @Update
    suspend fun updateTraining(training: Training)

    @Delete
    suspend fun deleteTraining(training: Training)


    @Query("SELECT * FROM training_data_table")
    fun getAllTrainings(): LiveData<List<Training>>

//    @Transaction
//    @Query("SELECT * FROM timer_items_data_table WHERE timer_id =:timer_id")
//    fun getAllTimerById(timer_id: Int): LiveData<TimerItem>

}