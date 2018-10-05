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

    companion object {

        private fun color(name: String, hexCode: String) =
            Color(name, android.graphics.Color.parseColor(hexCode))

        private val COLORS = listOf(
            color("Black", "#000000"),
            color("Red", "#ff0000"),
            color("Magenta", "#ff00ff"),
            color("Yellow", "#ffff00"),
            color("Green", "#00ff00"),
            color("Cyan", "#00ffff"),
            color("Blue", "#0000ff"),
            color("White", "#ffffff"),
            color("Material Red", "#A62C23"),
            color("Material Pink", "#A61646"),
            color("Material Purple", "#9224A6"),
            color("Material Deep Purple", "#5E35A6"),
            color("Material Indigo", "#3A4AA6"),
            color("Material Blue", "#1766A6"),
            color("Material Light Blue", "#0272A6"),
            color("Material Cyan", "#0092A6"),
            color("Material Teal", "#00A695"),
            color("Material Green", "#47A64A"),
            color("Material Light Green", "#76A63F"),
            color("Material Lime", "#99A62B"),
            color("Material Yellow", "#A69926"),
            color("Material Amber", "#A67E05"),
            color("Material Orange", "#A66300"),
            color("Material Deep Orange", "#A63716"),
            color("Material Brown", "#A67563"),
            color("Material Gray", "#676767"),
            color("Material Blue Gray", "#7295A6"),
            color("Gold", "#FFD700"),
            color("Sunset", "#F8B195"),
            color("Fog", "#A8A7A7"),
            color("Summer Red", "#fe4a49"),
            color("Aqua", "#2ab7ca"),
            color("Sun", "#fed766"),
            color("Dawn", "#451e3e")
        )
    }
}
