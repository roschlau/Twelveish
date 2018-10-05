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

class CapitalisationActivity : PreferencesActivity<Capitalisation>(
    values = CAPITALISATIONS,
    viewLayout = R.layout.textview_item,
    viewHolder = ::MyViewHolder,
    confirmationMessage = { "Capitalisation mode set" }
) {

    override fun SharedPreferences.Editor.save(position: Int, item: Capitalisation) {
        putInt(getString(R.string.preference_capitalisation), position)
    }

    class MyViewHolder(view: View) : BindableHolder<Capitalisation>(view) {
        val name: TextView = view.dateoptionslistListTextView

        override fun bind(item: Capitalisation) {
            name.text = item.name
        }
    }

    companion object {
        val CAPITALISATIONS = listOf(
            "All words title case",
            "All uppercase",
            "All lowercase",
            "First word title case",
            "First word in every line title case"
        ).map(::Capitalisation)
    }

}

class Capitalisation(val name: String? = null)