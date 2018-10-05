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

class DateOrderActivity : PreferencesActivity<DateOrder>(
    values = listOf(
        DateOrder("MDY"),
        DateOrder("DMY"),
        DateOrder("YMD"),
        DateOrder("YDM")
    ),
    viewLayout = R.layout.textview_item,
    viewHolder = ::MyViewHolder,
    confirmationMessage = { "\"" + it.name + "\" set" }
) {

    override fun SharedPreferences.Editor.save(position: Int, item: DateOrder) {
        putInt(getString(R.string.preference_date_order), position)
    }

    class MyViewHolder(view: View) : BindableHolder<DateOrder>(view) {
        val name: TextView = view.dateoptionslistListTextView

        override fun bind(item: DateOrder) {
            name.text = item.name
        }
    }
}

class DateOrder(val name: String)