package com.example.tabatatimer.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Training::class, TimerItem::class], version = 1, exportSchema = true)
abstract class TrainingDatabase: RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
    abstract fun timerDao(): TimerDao

    companion object{
        private var INSTANCE: TrainingDatabase? = null
        fun getInstance(context: Context):TrainingDatabase{
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TrainingDatabase::class.java,
                        "TrainingsDB"
                    ).build()
                }
                return instance
            }
        }

    }
}