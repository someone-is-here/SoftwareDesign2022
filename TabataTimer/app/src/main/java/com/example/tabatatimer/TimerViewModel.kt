package com.example.tabatatimer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabatatimer.db.TimerDao
import com.example.tabatatimer.db.TimerItem
import com.example.tabatatimer.db.Training
import com.example.tabatatimer.db.TrainingDao
import kotlinx.coroutines.launch

class TimerViewModel(private val dao: TimerDao): ViewModel() {
    var trainingId:Int = 0
    var timers: LiveData<List<TimerItem>> = dao.getAllTrainingWithTimers(trainingId)

    fun updateTimerList(trainingId:Int){
        this.trainingId = trainingId
        this.timers = dao.getAllTrainingWithTimers(this.trainingId)
    }

    fun insertTimerItem(timer: TimerItem) = viewModelScope.launch {
        dao.insertTimer(timer)
    }

    fun updateTimerItem(timer: TimerItem) = viewModelScope.launch {
        dao.updateTimer(timer)
    }

    fun deleteTimerItem(timer: TimerItem) = viewModelScope.launch {
        dao.deleteTimer(timer)
    }
}