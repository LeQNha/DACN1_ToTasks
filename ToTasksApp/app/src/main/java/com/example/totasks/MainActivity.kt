package com.example.totasks

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.totasks.databinding.ActivityMainBinding
import com.example.totasks.repositories.UserRepository
import com.example.totasks.viewmodels.UserViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }

        userViewModelSetUp()
    }

    fun userViewModelSetUp(){
        val userRepository = UserRepository()
        val userViewModelProviderFactory = UserViewModel.UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this , userViewModelProviderFactory).get(UserViewModel::class.java)
    }
}