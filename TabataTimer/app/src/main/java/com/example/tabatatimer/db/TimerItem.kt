package com.example.tabatatimer.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "timer_items_data_table",)
data class TimerItem (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="timer_id")
    var id: Int,

    @ColumnInfo(name="timer_value")
    var value: Int,

    @ColumnInfo(name="timer_color")
    var color: Int,

    @ColumnInfo(name="repeats_number")
    var repeats: Int,

    @ColumnInfo(name="timer_for_name")
    var name: Int,

    @ColumnInfo(name="timer_description")
    var description: String,

    @ColumnInfo(name="training_id")
    var trainingId: Int,

    @ColumnInfo(name="timer_priority")
    var timerPriority: Int = id
)