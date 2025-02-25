package com.example.totasks.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.example.totasks.R
import com.example.totasks.databinding.FragmentAddTaskDialogBinding
import com.example.totasks.interfaces.TaskDialogListener
import nha.kc.kotlincode.models.Task

class AddTaskDialogFragment(val selectedDayOfWeek: String) : DialogFragment() {

    private var _binding: FragmentAddTaskDialogBinding? = null
    private val binding get() = _binding!!
    private var listener: TaskDialogListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_add_task_dialog, container, false)
        _binding = FragmentAddTaskDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Lấy ra window của dialog và chỉnh kích thước
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_corner_background))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TaskDialogListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement TaskDialogListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.taskDayEditText.text = selectedDayOfWeek

        setupSpinners()
        onClickListenerSetUp()
    }

    fun onClickListenerSetUp() {
        binding.addTaskButton.setOnClickListener {
            val name = binding.taskNameEditText.text.toString()
            val type = binding.taskTypeSpinner.selectedItem.toString() // Lấy giá trị từ Spinner
            val importance = binding.taskImportanceSpinner.selectedItem.toString() // Lấy giá trị từ Spinner
//            val day = binding.taskDayEditText.text.toString()
            val day = selectedDayOfWeek
            // Chuyển đổi duration từ String thành Int an toàn
            val durationText = binding.taskDurationEditText.text.toString()
            val duration: Int = if (durationText.isNotEmpty()) durationText.toInt() else 0
            val startTime = binding.taskStartTimeEditText.text.toString()
            val endTime = binding.taskEndTimeEditText.text.toString()

            var startTimeInMinute = 0
            if (startTime.isNotEmpty()) {
                val hm = startTime.split(":")
                startTimeInMinute = (hm[0].toInt()) * 60 + hm[1].toInt()
            }

            if (name.isNotEmpty() && day.isNotEmpty()) {
                val newTask = Task(
                    "",
                    name,
                    type,
                    importance,
                    day,
                    duration,
                    startTimeInMinute,
                    startTime,
                    0,
                    endTime
                )
                listener?.onTaskAdded(newTask)
                dismiss() // Đóng dialog sau khi thêm task
            }
        }
    }

    private fun setupSpinners() {
        // Danh sách loại công việc
        val taskTypes = listOf("", "Personal", "Education", "Work")
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, taskTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.taskTypeSpinner.adapter = typeAdapter

        // Danh sách mức độ quan trọng
        val importanceLevels = listOf("", "Less Important", "Normal", "Important", "Very Important")
        val importanceAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, importanceLevels)
        importanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.taskImportanceSpinner.adapter = importanceAdapter
    }

//    interface TaskDialogListener {
//        fun onTaskAdded(task: Task)
//    }
}