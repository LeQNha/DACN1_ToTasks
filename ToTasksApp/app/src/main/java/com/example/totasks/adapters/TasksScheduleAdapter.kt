package com.example.totasks.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.totasks.R
import com.example.totasks.interfaces.TaskItemClickListener
import com.example.totasks.ui.TasksSchedulePrototype
import com.example.totasks.viewmodels.TaskScheduleViewModel
import nha.kc.kotlincode.models.Task

class TasksScheduleAdapter(private val taskScheduleViewModel: TaskScheduleViewModel) : RecyclerView.Adapter<TasksScheduleAdapter.TaskScheduleViewHolder>() {
    inner class TaskScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskNameTxtView = itemView.findViewById<TextView>(R.id.taskNameTxtView)
        val taskTimeTxtView = itemView.findViewById<TextView>(R.id.taskTimeTxtView)
        val taskImportanceColumn = itemView.findViewById<RelativeLayout>(R.id.importanceColumn)
        val taskType = itemView.findViewById<TextView>(R.id.taskTypeTxtView)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
    }

    private var differCallBack = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.TaskId == newItem.TaskId
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TasksScheduleAdapter.TaskScheduleViewHolder {
        return TaskScheduleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_item_task_layout, parent, false)
        )

//        return MemberViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.assigned_member_rv_item_layout, parent, false))
    }


    override fun onBindViewHolder(
        holder: TasksScheduleAdapter.TaskScheduleViewHolder,
        position: Int
    ) {
        val currentTask = differ.currentList[position]
        holder.apply {
            taskNameTxtView.text = currentTask.TaskName
            taskTimeTxtView.text =
                currentTask.StartTime.toString() + " - " + currentTask.EndTime.toString()
            taskType.text = currentTask.Type

            // Change color of importance collumn
            taskImportanceColumn.setBackgroundColor(
                when (currentTask.Importance) {
                    "Very Important" -> android.graphics.Color.parseColor("#D32F2F") // Đỏ đậm
                    "Important" -> android.graphics.Color.parseColor("#FF9800") // Cam
                    "Normal" -> android.graphics.Color.parseColor("#2196F3") // Xanh dương
                    "Less Important" -> android.graphics.Color.parseColor("#4CAF50") // Xanh lá
                    else -> android.graphics.Color.GRAY // Mặc định
                }
            )


            // ⚠️ Bỏ listener cũ để tránh lặp lại hoặc trigger sai
            checkBox.setOnCheckedChangeListener(null)

            // ✅ Set checkbox state based on Done field
            checkBox.isChecked = currentTask.Done

            // ✅ Gán lại listener sau cùng
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                // Cập nhật vào model
                currentTask.Done = isChecked

                // Gửi cập nhật về ViewModel hoặc DB
                val updates = mapOf("done" to isChecked)
                taskScheduleViewModel.updateTask(
                    TasksSchedulePrototype.selectedDateId,
                    currentTask,
                    updates
                )
            }
//
//            checkBox.setOnCheckedChangeListener { _, isChecked ->
//                currentTask.Done = isChecked
//
//                val updates = mapOf("done" to isChecked)
//                taskScheduleViewModel.updateTask(TasksSchedulePrototype.selectedDateId, currentTask, updates)
//            }

        }

        holder.itemView.setOnClickListener{
            onItemClickListener?.let {
                it(currentTask)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Task) -> Unit)? = null

    fun setOnItemClickListener(listener: (Task) -> Unit){
        onItemClickListener = listener
    }
}