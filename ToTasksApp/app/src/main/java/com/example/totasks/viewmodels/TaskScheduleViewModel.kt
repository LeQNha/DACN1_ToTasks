package com.example.totasks.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.totasks.repositories.TaskRepository
import com.example.totasks.repositories.TaskScheduleRepository
import nha.kc.kotlincode.models.Task

class TaskScheduleViewModel(val taskScheduleRepository: TaskScheduleRepository) : ViewModel() {

    var _tasks: MutableLiveData<List<Task>> = MutableLiveData()

    fun addTask(dateId: String,task: Task) {
        taskScheduleRepository.addTask(dateId,task)
    }

    fun deleteAllTasks(dateId: String){
        taskScheduleRepository.deleteAllTasks(dateId)
    }

    fun getTasks(dateId: String) {
        taskScheduleRepository.getTasks(dateId) {
            _tasks.postValue(it)
        }
    }

    fun deleteTask(dateId: String, taskId: String){
        taskScheduleRepository.deleteTask(dateId, taskId)
    }

    fun doneStatusUpdate(dateId: String, task: Task){
        taskScheduleRepository.doneStatusUpdate(dateId, task)
    }

    class TaskScheduleViewModelProviderFactory(val taskScheduleRepository: TaskScheduleRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskScheduleViewModel(taskScheduleRepository) as T
        }
    }
}