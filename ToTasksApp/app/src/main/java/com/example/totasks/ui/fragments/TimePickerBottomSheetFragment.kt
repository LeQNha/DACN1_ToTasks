package com.example.totasks.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.totasks.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TimePickerBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_time_picker_bottom_sheet, container, false)

        // Ánh xạ NumberPicker thay vì TimePicker
        val numberPickerHour = view.findViewById<NumberPicker>(R.id.numberPickerHour)
        val numberPickerMin = view.findViewById<NumberPicker>(R.id.numberPickerMin)

        // Cấu hình NumberPicker cho giờ
        numberPickerHour.minValue = 0
        numberPickerHour.maxValue = 23
        numberPickerHour.value = 0  // Giá trị mặc định cho giờ
        val hourRange = numberPickerHour.maxValue - numberPickerHour.minValue + 1
        val displayedValuesHour = Array(hourRange) { String.format("%02d", it) }
        numberPickerHour.displayedValues = displayedValuesHour
        numberPickerHour.wrapSelectorWheel = true // Cho phép cuộn vòng qua các giá trị

        // Cấu hình NumberPicker cho phút
        numberPickerMin.minValue = 0
        numberPickerMin.maxValue = 59
        numberPickerMin.value = 0  // Giá trị mặc định cho phút
        val minRange = numberPickerMin.maxValue - numberPickerMin.minValue + 1
        val displayedValuesMin = Array(minRange) { String.format("%02d", it) }
        numberPickerMin.displayedValues = displayedValuesMin
        numberPickerMin.wrapSelectorWheel = true // Cho phép cuộn vòng qua các giá trị

        val btnCancel = view.findViewById<TextView>(R.id.btnCancel)
        val btnSave = view.findViewById<TextView>(R.id.btnSave)

        btnCancel.setOnClickListener {
            dismiss() // Đóng bottom sheet khi nhấn Cancel
        }

        btnSave.setOnClickListener {
            val selectedHour = numberPickerHour.value
            val selectedMinute = numberPickerMin.value

            dismiss() // Đóng bottom sheet sau khi chọn
        }


        return view
    }
}
