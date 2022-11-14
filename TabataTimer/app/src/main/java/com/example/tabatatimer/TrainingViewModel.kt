package com.example.tabatatimer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabatatimer.db.TimerItem
import com.example.tabatatimer.db.Training
import com.example.tabatatimer.db.TrainingDao
import kotlinx.coroutines.launch

class TrainingViewModel(private val dao: TrainingDao): ViewModel() {
    val trainings = dao.getAllTrainings()

    fun insertTraining(training: Training) = viewModelScope.launch {
        dao.insertTraining(training)
    }
    fun updateTraining(training: Training) = viewModelScope.launch {
        dao.updateTraining(training)
    }
    fun deleteTraining(training: Training) = viewModelScope.launch {
        dao.deleteTraining(training)
    }
}