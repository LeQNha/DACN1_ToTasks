package com.example.totasks.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.totasks.R
import com.example.totasks.databinding.ActivityTaskDetailsBinding
import nha.kc.kotlincode.models.Task

class TaskDetailsActivity : BaseActivity() {
    private lateinit var binding : ActivityTaskDetailsBinding
    lateinit var task: Task
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getExtraTask()
        taskDetailsSetUp()
    }

    private fun getExtraTask(){
        val parcelTask = intent.getParcelableExtra<Task>("task")
        parcelTask?.let{
            task = it
        }
    }

    private fun taskDetailsSetUp(){
        binding.taskTitleTxtView.text = task.TaskName
        binding.taskPriorityTxtView.text = task.Importance
        binding.taskTypeTxtView.text = task.Type
    }
}