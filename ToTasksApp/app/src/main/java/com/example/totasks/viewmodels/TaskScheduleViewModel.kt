package com.example.totasks.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.totasks.repositories.TaskRepository
import com.example.totasks.repositories.TaskScheduleRepository
import kotlinx.coroutines.launch
import nha.kc.kotlincode.models.Task

class TaskScheduleViewModel(val taskScheduleRepository: TaskScheduleRepository) : ViewModel() {

    var _tasks: MutableLiveData<List<Task>> = MutableLiveData()

    val _predictedTask = MutableLiveData<Task?>()

    var _optimizedTasks : MutableLiveData<List<Task>?> = MutableLiveData()

    fun predictTaskSchedule(task: Task) = viewModelScope.launch {
        try {
            val result = taskScheduleRepository.predictTaskSchedule(task)
            _predictedTask.value = result // Cập nhật dữ liệu vào LiveData
            print("___${result}")
        } catch (e: Exception) {
            println("Error: ${e.message}")
            _predictedTask.value = null
        }
    }

    fun optimizeTasks(currentDayTaskList: List<Task>) = viewModelScope.launch {
        try {
            val optimizedTasks = taskScheduleRepository.optimizeTasks(currentDayTaskList)
            // Cập nhật UI với danh sách mới
            _optimizedTasks.postValue(optimizedTasks)
//            _tasks.postValue(optimizedTasks)
        } catch (e: Exception) {
            Log.e("API", "Lỗi khi tối ưu task: ${e.message}")
        }
    }


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

    fun getTasksByType(dateId: String, taskType: String){
        taskScheduleRepository.getTasksByType(dateId, taskType){
            _tasks.postValue(it)
        }
    }

    fun deleteTask(dateId: String, taskId: String){
        taskScheduleRepository.deleteTask(dateId, taskId)
    }

    fun doneStatusUpdate(dateId: String, task: Task){
        taskScheduleRepository.doneStatusUpdate(dateId, task)
    }

    fun updateTask(dateId: String, task: Task, updates: Map<String, Any>){
        taskScheduleRepository.updateTask(dateId, task, updates)
    }

    fun addTaskToDataSet(task: Task){
        taskScheduleRepository.addTaskToDataset(task)
    }

    fun updateTaskInDataset(task: Task, updates: Map<String, Any>){
        taskScheduleRepository.updateTaskInDataset(task, updates)
    }

    class TaskScheduleViewModelProviderFactory(val taskScheduleRepository: TaskScheduleRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskScheduleViewModel(taskScheduleRepository) as T
        }
    }
}