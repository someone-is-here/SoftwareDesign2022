package com.example.tabatatimer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tabatatimer.db.TimerDao

class TimerViewModelFactory (private val dao: TimerDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TimerViewModel::class.java)){
            return TimerViewModel(dao) as T
        }
        throw  IllegalArgumentException("Unknown View Model Class")
    }
}