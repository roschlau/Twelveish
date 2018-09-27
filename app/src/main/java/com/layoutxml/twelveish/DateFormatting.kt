package com.layoutxml.twelveish

import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

fun dateFormatter(index: Int, separator: String): DateTimeFormatter = when (index) {
    0 -> formatter(separator, "MM", "DD", "YYYY")
    1 -> formatter(separator, "DD", "MM", "YYYY")
    2 -> formatter(separator, "YYYY", "MM", "DD")
    3 -> formatter(separator, "YYYY", "DD", "MM")
    else -> dateFormatter(0, separator)
}

private fun formatter(
    separator: String,
    first: String,
    second: String,
    third: String
): DateTimeFormatter {
    return DateTimeFormat.forPattern("$first$separator$second$separator$third")
}