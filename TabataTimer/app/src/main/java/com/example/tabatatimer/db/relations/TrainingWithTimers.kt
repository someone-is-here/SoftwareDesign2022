package com.example.tabatatimer.db.relations

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.example.tabatatimer.db.TimerItem
import com.example.tabatatimer.db.Training

data class TrainingWithTimers (
    @Embedded val training: Training,

    @Relation(
        parentColumn = "training_id",
        entityColumn = "training_id"
    )

    val timers: List<TimerItem>
)