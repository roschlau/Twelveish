/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities

import android.content.SharedPreferences
import android.graphics.PorterDuff.Mode
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.layoutxml.twelveish.COLORS
import com.layoutxml.twelveish.R
import com.layoutxml.twelveish.objects.Color
import kotlinx.android.synthetic.main.imageview_and_textview_item.view.*

class ColorOptionsActivity : PreferencesActivity<Color>(
    values = COLORS,
    viewLayout = R.layout.imageview_and_textview_item,
    viewHolder = ::ColorOptionViewHolder
) {

    override fun SharedPreferences.Editor.save(position: Int, item: Color) {
        putInt(getString(R.string.preference_background_color), item.colorcode)
    }

    override fun getConfirmationMessage(item: Color) =
        "\"" + item.name + "\" set"

    class ColorOptionViewHolder(view: View): BindableHolder<Color>(view) {
        val name: TextView = view.settingsListTextView
        val icon: ImageView = view.settingsListImagetView

        override fun bind(item: Color) {
            name.text = item.name
            icon.setColorFilter(item.colorcode, Mode.SRC_IN)
        }
    }
}
