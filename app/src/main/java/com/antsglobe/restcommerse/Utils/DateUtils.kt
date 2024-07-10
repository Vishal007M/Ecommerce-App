package com.antsglobe.restcommerse.Utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateUtils {


    val utcFormat = SimpleDateFormat(DateFormat.UTC_FORMAT, Locale.getDefault())

    fun dateTimeFormatFromUTC(format: String, createdDate: String?): String {
        return if (createdDate == null || createdDate.isEmpty())
            ""
        else {
            // utcFormat.timeZone = TimeZone.getTimeZone("Etc/UTC")
            utcFormat.timeZone = TimeZone.getTimeZone("Asia/Calcutta")

            val fmt = SimpleDateFormat(format, Locale.getDefault())
            fmt.format(utcFormat.parse(createdDate))
        }
    }
}

object DateFormat {
    const val UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    const val DATE_TIME_FORMAT = "E dd-MMM-yyyy h:mm a"
}