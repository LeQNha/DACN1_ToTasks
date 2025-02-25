package nha.kc.kotlincode.models

import android.os.Parcelable

data class Task (
//    var TaskId: String = "",
//    var TaskName: String,
//    var Type: String,
//    var Importance: String,
//    var DayOfWeek: String,
//    var Duration: Int,
//    var StartTimeInMinute: Int,
//    var StartTime: String,
//    var EndTimeInMinute: Int,
//    var EndTime: String

    var TaskId: String = "",
    var TaskName: String = "",
    var Type: String = "",
    var Importance: String = "",
    var DayOfWeek: String = "",
    var Duration: Int = 0,
    var StartTimeInMinute: Int = 0,
    var StartTime: String = "",
    var EndTimeInMinute: Int = 0,
    var EndTime: String = ""
)
//{

//// Constructor mặc định (không tham số) để Firestore có thể deserialize
//constructor() : this("", "", "", "", "", 0, 0, "", 0, "")
//}