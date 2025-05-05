package com.example.totasks.repositories

import android.util.Log
import com.google.firebase.firestore.SetOptions
import nha.kc.kotlincode.api.RetrofitInstance
import nha.kc.kotlincode.models.Task
import nha.tu.tup.firebase.FirebaseInstance

class TaskRepository {

    val taskCollectionRef = FirebaseInstance.firebaseFirestoreInstance.collection("tasks")

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

    fun addTask(task: Task) {
        val taskId = taskCollectionRef.document().id
        task.TaskId = taskId
        taskCollectionRef
            .document(taskId)
            .set(task)
            .addOnSuccessListener {
                Log.d("Firestore", "Add Successfully")
            }
    }

    fun updateTask(task: Task) {
        taskCollectionRef.document(task.TaskId!!)
            .set(
                task,
                SetOptions.merge()
            ) // merge() giúp chỉ cập nhật trường có dữ liệu, không ghi đè toàn bộ
            .addOnSuccessListener {
                Log.d("Firestore", "Task updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating task: ${e.message}")
            }
    } //ghi đè

    fun deleteTasks() {
//        taskCollectionRef.whereEqualTo("dayId", "12").get()
//            .addOnSuccessListener { querySnapshot ->
//                for (document in querySnapshot) {
//                    collectionRef.document(document.id).delete()
//                        .addOnSuccessListener {
//                            println("Document ${document.id} deleted successfully")
//                        }
//                        .addOnFailureListener { e ->
//                            println("Error deleting document: ${e.message}")
//                        }
//                }
//            }
//            .addOnFailureListener { e ->
//                println("Error fetching documents: ${e.message}")
//            }

        taskCollectionRef.get()
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

    fun getTasks(listener: (List<Task>) -> Unit) {
        taskCollectionRef
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
    }
}