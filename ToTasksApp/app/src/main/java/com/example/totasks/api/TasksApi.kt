package nha.kc.kotlincode.api
import nha.kc.kotlincode.models.Task
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TasksApi {
    @POST("/predict_task")
//    suspend fun predictTasks(@Body task: Task): Call<List<Task>>
//    suspend fun predictTasks(@Body task: Task): Call<Task>
    suspend fun predictTasks(@Body task: Task): Task

    @POST("/optimize_schedule")
    suspend fun optimizeTasks(@Body tasks: List<Task>): List<Task>

}