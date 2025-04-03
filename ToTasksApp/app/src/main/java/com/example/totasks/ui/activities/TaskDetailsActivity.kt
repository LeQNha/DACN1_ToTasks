package com.example.totasks.ui.activities

import android.app.AlertDialog
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
    lateinit var dateId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getExtraTask()
        taskDetailsSetUp()
        onClickListenerSetUp()
    }

    private fun getExtraTask(){
        val parcelTask = intent.getParcelableExtra<Task>("task")
        parcelTask?.let{
            task = it
        }
        val extraDateId = intent.getStringExtra("selectedDateId")
        extraDateId?.let {
            dateId = it
        }
    }

    private fun onClickListenerSetUp(){
        binding.deleteTaskBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun taskDetailsSetUp(){
        binding.taskTitleTxtView.text = task.TaskName.ifEmpty { "Untitled Task" }

        binding.taskPriorityTxtView.apply {
            text = task.Importance.ifEmpty { "No Priority" }
            setBackgroundColor(
                when (task.Importance) {
                    "Very Important" -> android.graphics.Color.parseColor("#D32F2F") // Red
                    "Important" -> android.graphics.Color.parseColor("#FF9800") // Orange
                    "Normal" -> android.graphics.Color.parseColor("#2196F3") // Blue
                    "Less Important" -> android.graphics.Color.parseColor("#4CAF50") // Green
                    else -> android.graphics.Color.GRAY // Default
                }
            )
        }

        binding.taskTypeTxtView.text = task.Type.ifEmpty { "No Type" }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Task")
        builder.setMessage("Do you want to delete this task?")

        // Nếu chọn OK, thực hiện xóa task
        builder.setPositiveButton("OK") { dialog, _ ->
            deleteTask()
            dialog.dismiss()
        }

        // Nếu chọn Cancel, đóng dialog
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // Hiển thị AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun deleteTask(){
        taskScheduleViewModel.deleteTask(dateId, task.TaskId)
        finish()
    }
}