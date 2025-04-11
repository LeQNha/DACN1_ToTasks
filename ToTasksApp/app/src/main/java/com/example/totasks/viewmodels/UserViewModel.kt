package com.example.totasks.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.totasks.MainActivity
import com.example.totasks.models.User
import com.example.totasks.repositories.UserRepository
import com.example.totasks.ui.TasksSchedulePrototype
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserViewModel(val userRepository: UserRepository) : ViewModel() {
    var _users: MutableLiveData<List<User>> = MutableLiveData()
    var _friendRequestSenders: MutableLiveData<List<User>> = MutableLiveData()
    var _foundUsers: MutableLiveData<List<User>> = MutableLiveData()
    var test: String? = null

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    //    var _currentUser: MutableLiveData<User> = MutableLiveData()
    var currentUser: User? = null
    fun register(
        email: String,
        password: String,
        context: Context,
        activity: MainActivity
    ) =
        userRepository.registerUser(email, password, context, activity)


    fun login(email: String, password: String, context: Context, activity: MainActivity) = viewModelScope.launch {
//        userRepository.loginUser(email, password, context)

        val loginSuccess = userRepository.loginUser(email, password, context)
        if (loginSuccess) {
            getUser(context, activity)
            _loginSuccess.postValue(true)
        } else {
            _loginSuccess.postValue(false)
        }

    }

    fun signOut(context: Context, activity: MainActivity) =
        userRepository.signOutUser(context, activity)

//    fun getUser() = userRepository.getUser {
//        currentUser = it
//        println("_USER getUser = ${currentUser?.username}")
//    }

    fun getUser(context: Context, activity: MainActivity) = viewModelScope.launch {
        userRepository.getUser(context = context) {
            currentUser = it
        }
//        val intent = Intent(context, TasksSchedulePrototype::class.java).apply {
//            putExtra("currentUser", currentUser)
//        }
//        context.startActivity(intent)
//
//        activity.finish()
    }

    class UserViewModelFactory(val userRepository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(userRepository) as T
        }
    }

}