package com.phoenix.ainformation.feature_news.domain.util

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import com.phoenix.ainformation.R
import java.util.Date
import java.util.Locale

class DateUtil(
    private val context: Context
) {

    fun getTimeAgo(pubDate: String): String {
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
        val publishedDate: Date? = dateFormat.parse(pubDate)

        publishedDate?.let {
            val currentTime = Date()
            val differenceInMillis = currentTime.time - it.time

            val secondsInMillis = 1000
            val minutesInMillis = secondsInMillis * 60
            val hoursInMillis = minutesInMillis * 60
            val daysInMillis = hoursInMillis * 24

            val daysAgo = differenceInMillis / daysInMillis
            val hoursAgo = (differenceInMillis % daysInMillis) / hoursInMillis

            return when {
                daysAgo > 0 -> context.getString(R.string.days_ago_format, daysAgo)
                hoursAgo > 0 -> context.getString(R.string.hours_ago_format, hoursAgo)
                else -> context.getString(R.string.just_now)
            }
        }

        return ""

    }

    fun formatPubDate(pubDate: String): String {
        val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("GMT")
        val date = inputFormat.parse(pubDate)

        val userTimeZone = TimeZone.getDefault()
        val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.ENGLISH)
        outputFormat.timeZone = userTimeZone

        val calendar = Calendar.getInstance(userTimeZone)
        calendar.time = date

        return "Published ${outputFormat.format(calendar.time)} ${userTimeZone.getDisplayName(false, TimeZone.SHORT)}"
    }


}