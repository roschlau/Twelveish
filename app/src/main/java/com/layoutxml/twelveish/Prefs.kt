package com.layoutxml.twelveish

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import kotlin.properties.Delegates

class Prefs(val ctx: Context, val prefs: SharedPreferences) {

    var showedRateAlready by Delegates.observable(
        prefs.getBoolean(getString(R.string.showed_rate), false)
    ) { _, _, new -> prefs.edit().putBoolean(getString(R.string.showed_rate), new).apply() }

    var showedTutorialAlready by Delegates.observable(
        prefs.getBoolean(getString(R.string.showed_tutorial), false)
    ) { _, _, new -> prefs.edit().putBoolean(getString(R.string.showed_tutorial), new).apply() }

    var counter by Delegates.observable(
        int(R.string.counter, 0)
    ) { _, _, new -> if (new < 60) prefs.edit().putInt(getString(R.string.counter), new).apply() }

    val backgroundColor = int(R.string.preference_background_color, Color.parseColor("#000000"))
    val mainColor = int(R.string.preference_main_color, Color.parseColor("#ffffff"))
    val mainColorAmbient = int(R.string.preference_main_color_ambient, Color.parseColor("#ffffff"))
    val secondaryColor = int(R.string.preference_secondary_color, Color.parseColor("#ffffff"))
    val secondaryColorAmbient = int(R.string.preference_secondary_color_ambient, Color.parseColor("#ffffff"))
    val militaryTime = bool(R.string.preference_military_time, false)
    val militaryTextTime = bool(R.string.preference_militarytext_time, false)
    val dateFormat = dateFormatter(
        int(R.string.preference_date_order, 0),
        string(R.string.preference_date_separator, "/")
    )
    val capitalisation = int(R.string.preference_capitalisation, 0)
    val ampm = bool(R.string.preference_ampm, true)
    val showSecondary = bool(R.string.preference_show_secondary, true)
    val showSecondaryActive = bool(R.string.preference_show_secondary_active, true)
    val showSecondaryCalendarInactive = bool(R.string.preference_show_secondary_calendar, true)
    val showSecondaryCalendarActive = bool(R.string.preference_show_secondary_calendar_active, true)
    val showSuffixes = bool(R.string.preference_show_suffixes, true)
    val showBattery = bool(R.string.preference_show_battery, true)
    val showBatteryAmbient = bool(R.string.preference_show_battery_ambient, true)
    val showDay = bool(R.string.preference_show_day, true)
    val showDayAmbient = bool(R.string.preference_show_day_ambient, true)
    val showWords = bool(R.string.preference_show_words, true)
    val showWordsAmbient = bool(R.string.preference_show_words_ambient, true)
    val showSeconds = bool(R.string.preference_show_seconds, true)
    val showComplication = bool(R.string.preference_show_complications, true)
    val showComplicationAmbient = bool(R.string.preference_show_complications_ambient, true)
    val language = string(R.string.preference_language, "en")
    val font = string(R.string.preference_font, "robotolight")

    private fun getString(id: Int) = ctx.getString(id)

    private fun int(keyStringId: Int, defValue: Int) =
        prefs.getInt(getString(keyStringId), defValue)

    private fun bool(keyStringId: Int, defValue: Boolean) =
        prefs.getBoolean(getString(keyStringId), defValue)

    private fun string(keyStringId: Int, defValue: String) =
        prefs.getString(getString(keyStringId), defValue)

}