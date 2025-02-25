package com.example.totasks.interfaces

import nha.kc.kotlincode.models.Task

interface TaskDialogListener {
    fun onTaskAdded(task: Task)
}