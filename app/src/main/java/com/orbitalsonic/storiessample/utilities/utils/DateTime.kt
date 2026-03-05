package com.orbitalsonic.storiessample.utilities.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTime {
    fun todayDate(): Long {
        val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return format.format(Date()).toLong()
    }
}