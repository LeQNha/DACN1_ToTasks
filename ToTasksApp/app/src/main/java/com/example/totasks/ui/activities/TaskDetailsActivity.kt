package com.example.totasks.ui.activities

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
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
        getSupportActionBar()!!.hide()
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

        binding.titleEditBtn.setOnClickListener {
            showEditTextDialog("Edit Title", task.TaskName) { newTitle ->
                task.TaskName = newTitle
                binding.taskTitleTxtView.text = newTitle.ifEmpty { "Untitled Task" }
            }
        }

        binding.priorityEditBtn.setOnClickListener {
            showSelectionDialog("Edit Priority", task.Importance, listOf("Very Important", "Important", "Normal", "Less Important")) { newPriority ->
                task.Importance = newPriority
                updatePriorityStars(newPriority)
            }
        }

        binding.typeEditBtn.setOnClickListener {
            showSelectionDialog("Edit Type", task.Type, listOf("Work", "Personal", "Education")) { newType ->
                task.Type = newType
                binding.taskTypeTxtView.text = newType.ifEmpty { "No Type" }
            }
        }
    }

    private fun taskDetailsSetUp(){
        binding.taskTitleTxtView.text = task.TaskName.ifEmpty { "Untitled Task" }

        binding.taskTypeTxtView.text = task.Type.ifEmpty { "No Type" }
        binding.taskStartTimeText.text = task.StartTime
        binding.taskEndTimeText.text = task.EndTime

        updatePriorityStars(task.Importance)


    }

    private fun updatePriorityStars(priority: String) {
        val starLayout = binding.priorityStarsLayout
        starLayout.removeAllViews()

        val (starCount, color) = when (priority) {
            "Very Important" -> 5 to "#D32F2F"
            "Important" -> 4 to "#FF9800"
            "Normal" -> 3 to "#2196F3"
            "Less Important" -> 2 to "#4CAF50"
            else -> 1 to "#9E9E9E"
        }

        repeat(starCount) {
            val star = ImageView(this)
            star.setImageResource(android.R.drawable.btn_star_big_on)
            star.setColorFilter(Color.parseColor(color))
            val params = LinearLayout.LayoutParams(48, 48)
            params.setMargins(4, 0, 4, 0)
            star.layoutParams = params
            starLayout.addView(star)
        }

        val emptyStars = 5 - starCount
        repeat(emptyStars) {
            val emptyStar = ImageView(this)
            emptyStar.setImageResource(android.R.drawable.btn_star_big_off)
            emptyStar.setColorFilter(Color.LTGRAY)
            val params = LinearLayout.LayoutParams(48, 48)
            params.setMargins(4, 0, 4, 0)
            emptyStar.layoutParams = params
            starLayout.addView(emptyStar)
        }
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

    private fun showEditTextDialog(title: String, currentValue: String, onSave: (String) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)

        val input = EditText(this)
        input.setText(currentValue)
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _ ->
            val newValue = input.text.toString()
            onSave(newValue)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun showSelectionDialog(title: String, currentValue: String, options: List<String>, onSave: (String) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)

        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinner.adapter = adapter
        spinner.setSelection(options.indexOf(currentValue))

        builder.setView(spinner)

        builder.setPositiveButton("Save") { dialog, _ ->
            val newValue = spinner.selectedItem.toString()
            onSave(newValue)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
}