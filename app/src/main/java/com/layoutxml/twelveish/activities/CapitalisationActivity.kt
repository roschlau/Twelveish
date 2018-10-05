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
import com.layoutxml.twelveish.objects.Capitalisation
import kotlinx.android.synthetic.main.textview_item.view.*

class CapitalisationActivity : PreferencesActivity<Capitalisation>(
    values = CAPITALISATIONS,
    viewLayout = R.layout.textview_item,
    viewHolder = ::MyViewHolder
) {

    override fun SharedPreferences.Editor.save(position: Int, item: Capitalisation) {
        putInt(getString(R.string.preference_capitalisation), position)
    }

    override fun getConfirmationMessage(item: Capitalisation) =
        "Capitalisation mode set"

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
