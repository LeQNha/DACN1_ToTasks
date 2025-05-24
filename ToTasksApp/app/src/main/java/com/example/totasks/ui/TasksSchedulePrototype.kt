package com.example.totasks.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.totasks.adapters.TasksScheduleAdapter
import com.example.totasks.databinding.ActivityTasksSchedulePrototypeBinding
import com.example.totasks.interfaces.TaskDialogListener
import com.example.totasks.models.Date
import com.example.totasks.ui.activities.BaseActivity
import com.example.totasks.ui.activities.TaskDetailsActivity
import com.example.totasks.ui.fragments.AddTaskDialogFragment
import nha.kc.kotlincode.models.Task
import nha.tu.tup.firebase.FirebaseInstance
import java.text.SimpleDateFormat
import java.util.Calendar
//import java.util.Date
import java.util.Locale

class TasksSchedulePrototype : BaseActivity(), TaskDialogListener {
    private lateinit var binding: ActivityTasksSchedulePrototypeBinding

    private lateinit var tasksScheduleAdapter: TasksScheduleAdapter

    lateinit var predictedTaskArrayList: ArrayList<Task>
    lateinit var taskArrayList: ArrayList<Task>

    lateinit var selectedDate: Date
    lateinit var selectedDayOfWeek: String

    companion object {
        var selectedDateId: String = ""
        var todaySelectedDateId: String = ""
    }

    lateinit var filterAllBtn: TextView
    lateinit var filterWorkBtn: TextView
    lateinit var filterPersonalBtn: TextView
    lateinit var filterEducationBtn: TextView

    private val scheduledTaskTimeMap = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksSchedulePrototypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskArrayList = arrayListOf()
        predictedTaskArrayList = arrayListOf()

        dateSetUp()
        filterButtonsSetUp()
        taskScheduleRvSetUp()
        taskScheduleRvUpdate()
        onClickListennerSetUp()
    }


    fun taskScheduleRvSetUp() {
        tasksScheduleAdapter = TasksScheduleAdapter(taskScheduleViewModel)

        binding.taskScheduleRv.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = tasksScheduleAdapter
        }

        taskScheduleViewModel.getTasks(selectedDateId)

        taskScheduleViewModel._tasks.observe(this) { tasks ->
            predictedTaskArrayList.clear()
            for (t in tasks) {
                predictedTaskArrayList.add(t)

                //set notification
                val currentStartTime = t.StartTime
                val previousStartTime = scheduledTaskTimeMap[t.TaskId]

                if (previousStartTime == null || previousStartTime != currentStartTime) {
                    val now = System.currentTimeMillis()
                    val taskTime = convertToMillis(currentStartTime)

                    if (taskTime - 60 * 1000 > now) {
                        taskScheduleViewModel.scheduleNotification(this, t, todaySelectedDateId)
                        scheduledTaskTimeMap[t.TaskId] = currentStartTime // Cập nhật thời gian mới
                    }
                }

                //add to dataset
                taskScheduleViewModel.addTaskToDataSet(t)
            }

//            taskArrayList.sortBy { it.StartTimeInMinute }
            predictedTaskArrayList.sortBy { it.StartTimeInMinute }

            tasksScheduleAdapter.differ.submitList(predictedTaskArrayList.toList()) // Cập nhật danh sách
            tasksScheduleAdapter.notifyDataSetChanged()
        }

        taskScheduleViewModel._optimizedTasks.observe(this) { tasks ->
            tasks?.let {
                predictedTaskArrayList.clear()
                for (t in tasks) {
                    predictedTaskArrayList.add(t)

                    //update task
                    val updates = mapOf(
                        "duration" to t.Duration,
                        "startTime" to t.StartTime,
                        "endTime" to t.EndTime,
                        "startTimeInMinute" to t.StartTimeInMinute,
                        "endTimeInMinute" to t.EndTimeInMinute
                    )
                    val datasetUpdates = mapOf(
                        "Duration" to t.Duration,
                        "StartTime" to t.StartTime,
                        "EndTime" to t.EndTime
                    )
                    taskScheduleViewModel.updateTask(selectedDateId, t, updates)
                    taskScheduleViewModel.updateTaskInDataset(t, datasetUpdates)

                    //add to dataset
                    taskScheduleViewModel.addTaskToDataSet(t)
                }

                //            taskArrayList.sortBy { it.StartTimeInMinute }
                predictedTaskArrayList.sortBy { it.StartTimeInMinute }

                tasksScheduleAdapter.differ.submitList(predictedTaskArrayList.toList()) // Cập nhật danh sách
                tasksScheduleAdapter.notifyDataSetChanged()
            }
        }

        tasksScheduleAdapter.setOnItemClickListener {
            println("___click on item")
            val intent = Intent(this, TaskDetailsActivity::class.java).apply {
                putExtra("task", it)
                putExtra("selectedDateId", selectedDateId)
            }
            startActivity(intent)
        }

    }

    fun onClickListennerSetUp() {

        binding.addTaskFab.setOnClickListener {
            val dialog = AddTaskDialogFragment(binding.dayOfWeekTxtView.text.toString())
            dialog.show(supportFragmentManager, "AddTaskDialogFragment")
        }

        binding.predictTaskScheduleBtn.setOnClickListener {

//            taskScheduleViewModel.deleteAllTasks(selectedDateId)
//            for (task in taskArrayList) {
//                taskViewModel.predictTaskSchedule(task)
//            }
//            binding.taskNumberTxtView.text = "0"
//            predictedTaskArrayList.clear()

            for(t in predictedTaskArrayList){
                println("=== ${t}")
            }
            taskScheduleViewModel.optimizeTasks(predictedTaskArrayList)

        }

        binding.openCalendarBtn.setOnClickListener {
            showDatePickerDialog()
        }

        val filters = listOf(filterAllBtn, filterWorkBtn, filterPersonalBtn, filterEducationBtn)

        // Mặc định chọn "All"
        filterAllBtn.isSelected = true

        filters.forEach { filter ->
            filter.setOnClickListener {
                // Đặt tất cả về trạng thái không được chọn
                filters.forEach { it.isSelected = false }
                // Chỉ đặt filter được nhấn là được chọn
                filter.isSelected = true

                if (filter.text == "All") {
                    taskScheduleViewModel.getTasks(selectedDateId)
                } else {
                    taskScheduleViewModel.getTasksByType(
                        selectedDateId,
                        filter.text.toString().trim()
                    )
                }
            }
        }
    }

    fun taskScheduleRvUpdate() {

        var predictedTask: Task? = null
        taskViewModel._predictedTask.observe(this) { it ->
            predictedTask = it
            addTaskToDatabase(predictedTask)
        }
    }

    fun addTaskToDatabase(predictedTask: Task?) {
        predictedTask?.let {
            val auth = FirebaseInstance.auth
            var currentUserAuth = auth.currentUser

            currentUserAuth?.let { user ->
                it.UserId = user.uid
                taskScheduleViewModel.addTask(selectedDateId, it)
            }

//            println("___addTaskToDatabase: ${predictedTask}")
//            taskScheduleViewModel.addTask(selectedDateId, it)
        }
    }

    override fun onTaskAdded(task: Task) {
//        taskArrayList = predictedTaskArrayList
//        taskArrayList.add(task)
//        binding.taskNumberTxtView.text =
//            (binding.taskNumberTxtView.text.toString().toInt() + 1).toString()
//
//        tasksScheduleAdapter.differ.submitList(taskArrayList.toList()) // Cập nhật danh sách
//        tasksScheduleAdapter.notifyDataSetChanged()

//        taskScheduleViewModel.addTask(selectedDateId, task)

        println("___onTaskAdded: ${task}")
        taskViewModel.predictTaskSchedule(task)
    }

    private fun filterButtonsSetUp() {
        filterAllBtn = binding.filterAll
        filterWorkBtn = binding.filterWork
        filterPersonalBtn = binding.filterPersonal
        filterEducationBtn = binding.filterEducation
    }

    private fun dateSetUp() {
        val calendar = Calendar.getInstance()
        selectedDate = Date(
            "",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )


        // Lấy tên ngày trong tuần (ví dụ: Monday)
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dayOfWeek = dayOfWeekFormat.format(calendar.time)

        // Lấy ngày tháng năm (định dạng: dd/MM/yyyy)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = dateFormat.format(calendar.time)

        // Hiển thị ngày được chọn
        binding.dayOfWeekTxtView.text = dayOfWeek
        binding.dayTxtView.text = date

        selectedDateId = "${selectedDate.day}-${selectedDate.month +1}-${selectedDate.year}"

        todaySelectedDateId = selectedDateId
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Chuyển đổi ngày thành định dạng mong muốn
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                selectedDate.year = selectedYear
                selectedDate.month = selectedMonth
                selectedDate.day = selectedDay

                // Lấy tên ngày trong tuần (ví dụ: Monday)
                val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                val dayOfWeek = dayOfWeekFormat.format(selectedCalendar.time)

                // Lấy ngày tháng năm (định dạng: dd/MM/yyyy)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = dateFormat.format(selectedCalendar.time)

                // Hiển thị ngày được chọn
                binding.dayOfWeekTxtView.text = dayOfWeek
                binding.dayTxtView.text = date

                selectedDateId = "${selectedDate.day}-${selectedDate.month + 1}-${selectedDate.year}"

                taskScheduleViewModel.getTasks(selectedDateId)

//        }, year, month, day)
            }, selectedDate.year, selectedDate.month, selectedDate.day)

        datePickerDialog.show()
    }

    fun convertToMillis(timeString: String): Long {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = format.parse(timeString)

        val calendar = Calendar.getInstance().apply {
            if (date != null) {
                time = date
                val now = Calendar.getInstance()
                set(Calendar.YEAR, now.get(Calendar.YEAR))
                set(Calendar.MONTH, now.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
            }
        }

        return calendar.timeInMillis
    }
}