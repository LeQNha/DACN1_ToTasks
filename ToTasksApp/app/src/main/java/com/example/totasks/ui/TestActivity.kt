package com.example.totasks.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.totasks.databinding.ActivityTestBinding
import nha.kc.kotlincode.api.RetrofitInstance
import nha.kc.kotlincode.models.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.predictBtn.setOnClickListener {
            getTasks()
        }
    }

    fun getTasks(){
//        val task = Task(binding.taskNameEditTxt.text.toString(), "", "", "Monday", 0, 0, "")
//        RetrofitInstance.taskApi.predictTasks(task).enqueue(object : Callback<List<Task>>{
//            override fun onResponse(call: retrofit2.Call<List<Task>>, response: Response<List<Task>>) {
//                if(response.isSuccessful){
//                    response.body()?.let{ tasks ->
//                        for (task in tasks) {
//                            binding.newTaskContent.append("${task}\n")
//                        }
//                    }
//                }else{
////                    val errorMessage = response.errorBody()?.string() ?: "ERROR"
////                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
//                    Log.e("API_RESPONSE", "Error: ${response.code()}, ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
//                Log.e("API_RESPONSE", "Failed to fetch items: ${t.message}")
//            }
//
//        })
    }
}