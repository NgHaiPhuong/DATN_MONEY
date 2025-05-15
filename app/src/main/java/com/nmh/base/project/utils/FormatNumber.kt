package com.nmh.base.project.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object FormatNumber {

    fun convertNumberToNumberDotString(number: Long): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(number).replace(",", ".")
    }

    fun convertNumberToCommaString(number: Long): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(number)
    }

    fun convertStringToNumber(amount: String): Long {
        return amount.replace(".", "").toLong()
    }

    fun convertMonthFormat(input: String): String {
        val inputFormat = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH)

        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }

    fun convertDateToMonth(input: String) : String {
        val inputFormat = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)

        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }

    fun convertDateToMonthYear(input: String) : String {
        val inputFormat = SimpleDateFormat("MMMM, dd yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH)

        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }

    fun monthNameToNumber(monthName: String): Int {
        val inputFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
        val date = inputFormat.parse(monthName)
        val calendar = Calendar.getInstance().apply { time = date!! }
        return calendar.get(Calendar.MONTH) + 1 // Tháng bắt đầu từ 0 → +1
    }

}