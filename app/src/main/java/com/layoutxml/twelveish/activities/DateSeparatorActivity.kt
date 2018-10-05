/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities

import android.content.SharedPreferences
import android.view.View
import android.widget.TextView
import com.layoutxml.twelveish.R
import kotlinx.android.synthetic.main.textview_item.view.*

class DateSeparatorActivity : PreferencesActivity<DateSeparator>(
    values = listOf(
        DateSeparator("Slash /", "/"),
        DateSeparator("Period .", "."),
        DateSeparator("Hyphen -", "-"),
        DateSeparator("Space", " ")
    ),
    viewLayout = R.layout.textview_item,
    viewHolder = ::MyViewHolder,
    confirmationMessage = { "\"" + it.name + "\" set" }
) {
    override fun SharedPreferences.Editor.save(position: Int, item: DateSeparator) {
        putString(getString(R.string.preference_date_separator), item.symbol)
    }

    class MyViewHolder(view: View) : BindableHolder<DateSeparator>(view) {
        val name: TextView = view.dateoptionslistListTextView

        override fun bind(item: DateSeparator) {
            name.text = item.name
        }
    }
}

class DateSeparator(var name: String, var symbol: String)