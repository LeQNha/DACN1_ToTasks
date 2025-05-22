package com.example.totasks.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.totasks.R
import com.example.totasks.repositories.TaskRepository
import com.example.totasks.repositories.TaskScheduleRepository
import com.example.totasks.viewmodels.TaskScheduleViewModel
import com.example.totasks.viewmodels.TaskViewModel

open class BaseActivity : AppCompatActivity() {
    lateinit var taskViewModel: TaskViewModel
    lateinit var taskScheduleViewModel: TaskScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        createNotificationChannel()

        taskViewModelSetUp()
        taskScheduleViewModelSetUp()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }

    private fun taskViewModelSetUp() {
        val taskRepository = TaskRepository()
        val taskViewModelProviderFactory = TaskViewModel.TaskViewModelProviderFactory(taskRepository)
        taskViewModel = ViewModelProvider(this, taskViewModelProviderFactory).get(TaskViewModel::class.java)
    }

    private fun taskScheduleViewModelSetUp() {
        val taskScheduleRepository = TaskScheduleRepository()
        val taskScheduleViewModelProviderFactory = TaskScheduleViewModel.TaskScheduleViewModelProviderFactory(taskScheduleRepository)
        taskScheduleViewModel = ViewModelProvider(this, taskScheduleViewModelProviderFactory).get(TaskScheduleViewModel::class.java)
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "TaskReminderChannel"
            val descriptionText = "Channel for Task Reminder"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel("TASK_CHANNEL", name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}