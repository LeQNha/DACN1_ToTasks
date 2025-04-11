package com.example.totasks.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String = "",
    var email: String? = null,
): Parcelable
