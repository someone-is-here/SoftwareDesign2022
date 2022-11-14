package com.example.tabatatimer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tabatatimer.db.TrainingDao

class TrainingViewModelFactory(
    private val dao: TrainingDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TrainingViewModel::class.java)){
            return TrainingViewModel(dao) as T
        }
        throw  IllegalArgumentException("Unknown View Model Class")
    }
}
