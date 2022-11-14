package com.example.tabatatimer.db

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_data_table",)
data class Training (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="training_id")
    var id: Int,

    @ColumnInfo(name="training_name")
    var name: String,

    @ColumnInfo(name="training_color")
    var color: Int,
)