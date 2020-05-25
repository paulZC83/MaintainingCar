package com.example.maintainingcar.kt

import android.content.Context
import android.util.TypedValue
import androidx.annotation.FloatRange

fun Context.dp2px(@FloatRange(from = 0.0) dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}