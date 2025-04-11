package com.example.totasks.repositories

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.totasks.MainActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import com.example.totasks.models.User
import nha.tu.tup.firebase.FirebaseInstance
import java.util.Date

class UserRepository() {
    val userCollectionRef = FirebaseInstance.firebaseFirestoreInstance.collection("users")
    val auth = FirebaseInstance.auth
    var currentUserAuth = auth.currentUser

    fun registerUser(
        email: String,
        password: String,
        context: Context,
        activity: MainActivity
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Khai báo lại để lấy currentUser vì khi khai báo lần đầu khi khởi tạo nó sẽ là null
                    currentUserAuth = auth.currentUser
                    val user = User(
                        userId = currentUserAuth?.uid ?: "",
                        email = currentUserAuth?.email
                    )
                    addNewUser(user)

                    context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    Toast.makeText(
                        context,
                        "Register Failed: ${it.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //    fun loginUser(email: String, password: String, context: Context, activity: AutthenciateScreen) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // Đăng nhập thành công
//                    currentUserAuth = auth.currentUser
//                    Toast.makeText(
//                        context,
//                        "Login Successfully: ${currentUserAuth?.email}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    Log.i("Firebase", "Login Successfully: ${currentUserAuth?.email}")
//                    context.startActivity(Intent(context, MainActivity::class.java))
//                } else {
//                    // Đăng nhập thất bại
//                    Toast.makeText(
//                        context,
//                        "Login Failed: ${task.exception?.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//    }
    suspend fun loginUser(email: String, password: String, context: Context): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            // Đăng nhập thành công
            currentUserAuth = result.user
            Toast.makeText(context, "Login Successfully: ${currentUserAuth?.email}", Toast.LENGTH_SHORT).show()
            Log.i("Firebase", "Login Successfully: ${currentUserAuth?.email}")
            true

        } catch (e: Exception) {
            Toast.makeText(context, "Login Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

//    fun getUser(callback: (User?) -> Unit) {
//        val currentUserId = currentUserAuth?.uid
//        if (currentUserId != null) {
//            userCollectionRef
//                .whereEqualTo("userId", currentUserId)
//                .get()
//                .addOnSuccessListener { querySnapshot ->
//
//                    if (!querySnapshot.isEmpty) {
//                        // Lấy tài liệu đầu tiên
//                        val document = querySnapshot.documents[0]
//                        val user = document.toObject(User::class.java)
//                        println("DA GET DC USER")
//                        callback(user) // user có thể là null
//                    } else {
//                        callback(null) // Trả về null nếu không có tài liệu nào
//                    }
//
//                }
//                .addOnFailureListener {
//                    callback(null) // Trả về null nếu xảy ra lỗi
//                }
//        } else {
//            callback(null) // Trả về null nếu người dùng chưa đăng nhập
//        }
//    }

    suspend fun getUser(context: Context, callback: (User?) -> Unit) {
        var user: User? = null
        val currentUserId = currentUserAuth?.uid
        if (currentUserId != null) {
            val document = userCollectionRef.document(currentUserId).get().await()
            user = document.toObject<User>(User::class.java)
            println("___da nhan dc user = ${user?.email}")
        }
        callback(user)

//        context.startActivity(Intent(context, MainActivity::class.java))
    }

    fun signOutUser(context: Context, activity: MainActivity) {
        auth.signOut()
        context.startActivity(Intent(context, MainActivity::class.java))
        activity.finish()
    }

    fun addNewUser(user: User) {
        userCollectionRef
            .document(user.userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Add Successfully")
            }
    }

}