package com.example.totasks.repositories

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.totasks.utils.TaskNotificationReceiver
import nha.kc.kotlincode.api.RetrofitInstance
import nha.kc.kotlincode.models.Task
import nha.tu.tup.firebase.FirebaseInstance
import java.util.Calendar

class TaskScheduleRepository {

    val auth = FirebaseInstance.auth
    var currentUserAuth = auth.currentUser

    val dateCollectionRef = FirebaseInstance.firebaseFirestoreInstance.collection("dates")
    val datasetCollectionRef = FirebaseInstance.firebaseFirestoreInstance.collection("dataset")

    suspend fun predictTaskSchedule(task: Task): Task? {
        return try {
            RetrofitInstance.taskApi.predictTasks(task) // ✅ Gọi API trong coroutine
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error fetching task: ${e.message}")
            null
        }
    }

    suspend fun optimizeTasks(currentDayTaskList: List<Task>): List<Task>? {
        return try {
            RetrofitInstance.taskApi.optimizeTasks(currentDayTaskList) // ✅ Gọi API trong coroutine
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error fetching tasks: ${e.message}")
            null
        }
    }


    fun addTask(dateId: String, task: Task) {
        val taskCollectionRef = dateCollectionRef.document(dateId).collection("tasks")

        if(task.TaskId.isEmpty()) {
            val taskId = taskCollectionRef.document().id
            task.TaskId = taskId
        }
        taskCollectionRef
            .document(task.TaskId)
            .set(task)
            .addOnSuccessListener {
                Log.d("Firestore", "Add Successfully")
            }
    }

    fun deleteAllTasks(dateId: String) {
        val taskCollectionRef = dateCollectionRef.document(dateId).collection("tasks")
        val currentUserId = currentUserAuth?.uid

        if (currentUserId == null) {
            println("No user logged in.")
            return
        }

        taskCollectionRef
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot) {
                    taskCollectionRef.document(doc.id).delete()
                        .addOnSuccessListener {
                            println("Document ${doc.id} deleted successfully")
                        }
                        .addOnFailureListener { e ->
                            println("Error deleting document: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("Error fetching documents: ${e.message}")
            }
    }

    fun getTasks(dateId: String, listener: (List<Task>) -> Unit) {
        val taskCollectionRef = dateCollectionRef.document(dateId).collection("tasks")
        val currentUserId = currentUserAuth?.uid

        if (currentUserId == null) {
            println("No user logged in.")
            listener(emptyList())
            return
        }

        taskCollectionRef
//            .orderBy("startTimeInMinute")
            .whereEqualTo("userId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("Firestore", "Listen failed", error)
                    return@addSnapshotListener
                } else {
                    if (snapshot != null && !snapshot.isEmpty) {
                        val taskList = snapshot.documents.mapNotNull { documentSnapshot ->
                            documentSnapshot.toObject(Task::class.java)
                        }
                        Log.w("Firestore", "Get tasks", error)
                        Log.d("Firestore", "Snapshot updated with ${snapshot?.size()} tasks")
                        listener(taskList)
                    }
                }
            }
        // Trả về list rỗng nếu dateId đó không tồn tại
        listener(emptyList())
    }

    fun getTasksByType(dateId: String, taskType: String, listener: (List<Task>) -> Unit) {
        val taskCollectionRef = dateCollectionRef.document(dateId).collection("tasks")
        val currentUserId = currentUserAuth?.uid

        taskCollectionRef
            .whereEqualTo("type", taskType)
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
//                for (document in documents) {
//
//                    Log.d("Firestore", "${document.id} => ${document.data}")
//                }
                val taskList = documents.mapNotNull { queryDocumentSnapshot ->
                    queryDocumentSnapshot.toObject(Task::class.java)
                }

                listener(taskList)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }

        // Trả về list rỗng nếu dateId đó không tồn tại
        listener(emptyList())
    }

    fun deleteTask(dateId: String, taskId: String) {
        val taskCollectionRef = dateCollectionRef.document(dateId).collection("tasks")
        taskCollectionRef.document(taskId)
            .delete()
            .addOnSuccessListener {
                println("✅ Task deleted successfully!")
            }
            .addOnFailureListener { e ->
                println("❌ Error deleting task: ${e.message}")
            }
    }

    fun doneStatusUpdate(dateId: String, task: Task) {
        val taskCollectionRef = dateCollectionRef.document(dateId).collection("tasks")
        taskCollectionRef.document(task.TaskId)
            .update("Done", task.Done)
            .addOnSuccessListener {
                println("✅ Task updated successfully: Done = ${task.Done}")
            }
            .addOnFailureListener { e ->
                println("❌ Error updating task: ${e.message}")
            }
    }

    fun updateTask(dateId: String, task: Task, updates: Map<String, Any>){
        val taskCollectionRef = dateCollectionRef.document(dateId).collection("tasks")

        taskCollectionRef
            .document(task.TaskId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("Firestore", "Task fields updated successfully")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating task fields", e)
            }
    }

    fun addTaskToDataset(task: Task) {
        val currentUserId = currentUserAuth?.uid

        val taskData = hashMapOf(
            "UserID" to currentUserId,
            "TaskID" to task.TaskId,
            "TaskName" to task.TaskName,
            "Type" to task.Type,
            "Duration" to task.Duration,
            "Importance" to task.Importance,
            "DayOfWeek" to task.DayOfWeek,
            "StartTime" to task.StartTime,
            "EndTime" to task.EndTime
        )

        datasetCollectionRef
            .document(task.TaskId) // 🔥 Dùng TaskID làm Document ID
            .set(taskData) // 🔥 Nếu có thì ghi đè, nếu chưa có thì thêm mới
            .addOnSuccessListener {
                println("✅ Task has been added or updated to database successfully!")
            }
            .addOnFailureListener { e ->
                println("❌ Error writing task in database: $e")
            }
    }

    fun updateTaskInDataset(task: Task, updates: Map<String, Any>){
        datasetCollectionRef
            .document(task.TaskId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("Firestore", "Task fields dataset updated successfully")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating dataset task fields", e)
            }
    }

    fun scheduleNotification(context: Context, task: Task, dateId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra("task_name", task.TaskName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.TaskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Parse "HH:mm" -> Calendar
        val timeParts = task.StartTime.split(":")
        if (timeParts.size == 2) {
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val triggerTime = calendar.timeInMillis - 1 * 60 * 1000 // nhắc trước 10 phút

            if (triggerTime > System.currentTimeMillis()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        }
    }
}