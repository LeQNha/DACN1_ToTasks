package nha.kc.kotlincode.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task (
    var TaskId: String = "",
    var TaskName: String = "",
    var Type: String = "",
    var Importance: String = "",
    var DayOfWeek: String = "",
    var Duration: Int = 0,
    var StartTimeInMinute: Int = 0,
    var StartTime: String = "",
    var EndTimeInMinute: Int = 0,
    var EndTime: String = "",
    var Done: Boolean = false
):Parcelable
//{

//// Constructor mặc định (không tham số) để Firestore có thể deserialize
//constructor() : this("", "", "", "", "", 0, 0, "", 0, "")
//}