package com.example.totasks.repositories

import android.util.Log
import nha.kc.kotlincode.models.Task
import nha.tu.tup.firebase.FirebaseInstance

class TaskScheduleRepository {

    val dateCollectionRef = FirebaseInstance.firebaseFirestoreInstance.collection("dates")

    fun addTask(dateId: String, task: Task) {
        val taskCollectionRef = dateCollectionRef.document(dateId).collection("tasks")

        val taskId = taskCollectionRef.document().id
        task.TaskId = taskId
        taskCollectionRef
            .document(taskId)
            .set(task)
            .addOnSuccessListener {
                Log.d("Firestore", "Add Successfully")
            }
    }

    fun deleteAllTasks(dateId: String) {
        val taskCollectionRef = dateCollectionRef.document(dateId).collection("tasks")

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

    fun getTasks(dateId: String, listener: (List<Task>) -> Unit) {
        val taskCollectionRef = dateCollectionRef.document(dateId).collection("tasks")

        Log.d("Firestore", "___GETTTTTTTT TASKS ${dateId}")

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
//                        println("___GET TASKS ${dateId}")
                        Log.d("Firestore", "___GET TASKS ${dateId}")
                        listener(taskList)
                    }
                }
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
}