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
        binding.taskTitleTxtView.text = task.TaskName
        binding.taskPriorityTxtView.text = task.Importance
        binding.taskTypeTxtView.text = task.Type
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