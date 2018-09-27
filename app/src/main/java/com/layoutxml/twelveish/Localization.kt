package com.layoutxml.twelveish

import android.content.Context

class ResolvedLocalization(
    val prefixes: Array<String>,
    val suffixes: Array<String>,
    val weekDays: Array<String>,
    var timeshift: Array<Int>,
    val prefixNewLine: Array<Boolean>,
    val suffixNewLine: Array<Boolean>
) {
    companion object {
        fun from(locale: String, ctx: Context) =
            Localization.forLocale(locale).let { l ->
                ResolvedLocalization(
                    prefixes = ctx.resources.getStringArray(l.Prefixes),
                    suffixes = ctx.resources.getStringArray(l.Suffixes),
                    weekDays = ctx.resources.getStringArray(l.WeekDays),
                    timeshift = l.TimeShift,
                    prefixNewLine = l.PrefixNewLine,
                    suffixNewLine = l.SuffixNewLine
                )
            }
    }
}

enum class Localization(
    val locale: String,
    val Prefixes: Int,
    val Suffixes: Int,
    val WeekDays: Int,
    var TimeShift: Array<Int>,
    val PrefixNewLine: Array<Boolean>,
    val SuffixNewLine: Array<Boolean>
) {
    ENGLISH("en",
        Prefixes = R.array.Prefixes,
        Suffixes = R.array.Suffixes,
        WeekDays = R.array.WeekDays,
        TimeShift = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1),
        PrefixNewLine = arrayOf(false, false, true, true, true, true, true, true, true, true, true, true),
        SuffixNewLine = arrayOf(false, true, false, true, false, false, false, true, false, true, false, false)
    ),
    GERMAN("de",
        Prefixes = R.array.PrefixesDE,
        Suffixes = R.array.SuffixesDE,
        WeekDays = R.array.WeekDaysDE,
        TimeShift = arrayOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        PrefixNewLine = arrayOf(true, true, true, true, true, true, true, true, true, true, true, true),
        SuffixNewLine = arrayOf(false, false, false, true, false, false, false, false, false, true, false, false)
    ),
    LT("lt",
        Prefixes = R.array.PrefixesLT,
        Suffixes = R.array.SuffixesLT,
        WeekDays = R.array.WeekDaysLT,
        TimeShift = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1),
        PrefixNewLine = arrayOf(true, true, true, true, true, true, true, true, true, true, true, true),
        SuffixNewLine = arrayOf(false, false, true, true, true, true, true, true, false, false, false, false)
    ),
    FI("fi",
        Prefixes = R.array.PrefixesFI,
        Suffixes = R.array.SuffixesFI,
        WeekDays = R.array.WeekDaysFI,
        TimeShift = arrayOf(0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
        PrefixNewLine = arrayOf(true, true, true, true, true, true, true, true, true, true, true, true),
        SuffixNewLine = arrayOf(false, false, false, false, false, false, false, false, false, false, false, false)
    ),
    RU("ru",
        Prefixes = R.array.PrefixesRU,
        Suffixes = R.array.SuffixesRU,
        WeekDays = R.array.WeekDaysRU,
        TimeShift = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1),
        PrefixNewLine = arrayOf(false, false, true, false, true, false, false, false, true, true, true, false),
        SuffixNewLine = arrayOf(false, true, true, true, true, true, true, true, false, false, false, false)
    ),
    HU("hu",
        Prefixes = R.array.PrefixesHU,
        Suffixes = R.array.SuffixesHU,
        WeekDays = R.array.WeekDaysHU,
        TimeShift = arrayOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        PrefixNewLine = arrayOf(false, false, true, true, true, true, true, true, true, true, false, true),
        SuffixNewLine = arrayOf(true, true, true, false, false, true, false, true, false, true, true, false)
    );

    companion object {
        fun forLocale(locale: String) = when (locale) {
            "en" -> ENGLISH
            "de" -> GERMAN
            "lt" -> LT
            "fi" -> FI
            "ru" -> RU
            "hu" -> HU
            else -> ENGLISH
        }
    }
}