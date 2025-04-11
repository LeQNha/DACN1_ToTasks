package com.example.totasks

import android.os.Bundle
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

        userViewModelSetUp()
    }

    fun userViewModelSetUp(){
        val userRepository = UserRepository()
        val userViewModelProviderFactory = UserViewModel.UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this , userViewModelProviderFactory).get(UserViewModel::class.java)
    }
}