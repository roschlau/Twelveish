package com.layoutxml.twelveish.activities

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import android.widget.TextView
import com.layoutxml.twelveish.R

class FontOptionsActivity : PreferencesActivity<FontOption>(
    values = listOf(
        FontOption("Roboto light (default)", "robotolight") { Typeface.create("sans-serif-light", Typeface.NORMAL) },
        FontOption("Alegreya", "alegreya", R.font.alegreya),
        FontOption("Cabin", "cabin", R.font.cabin),
        FontOption("IBM Plex Sans", "ibmplexsans", R.font.ibmplexsans),
        FontOption("Inconsolata", "inconsolata", R.font.inconsolata),
        FontOption("Merriweather", "merriweather", R.font.merriweather),
        FontOption("Nunito", "nunito", R.font.nunito),
        FontOption("Pacifico", "pacifico", R.font.pacifico),
        FontOption("Quattrocento", "quattrocento", R.font.quattrocento),
        FontOption("Quicksand", "quicksand", R.font.quicksand),
        FontOption("Rubik", "rubik", R.font.rubik)
    ),
    viewLayout = R.layout.textview_item,
    viewHolder = ::MyViewHolder,
    confirmationMessage = { "\"" + it.name + "\" set" }
) {

    override fun SharedPreferences.Editor.save(position: Int, item: FontOption) {
        putString(getString(R.string.preference_font), item.key)
    }

    class MyViewHolder(view: View) : BindableHolder<FontOption>(view) {
        var name: TextView = view.findViewById(R.id.dateoptionslistListTextView)

        override fun bind(item: FontOption) {
            name.text = item.name
            name.typeface = item.typeface(name.context)
        }
    }

}

class FontOption(
    val name: String,
    val key: String,
    val typeface: (Context) -> Typeface?
) {
    constructor(name: String, key: String, fontId: Int) : this(name, key, fontGetter(fontId))
}

private fun fontGetter(id: Int) =
    { ctx: Context -> ResourcesCompat.getFont(ctx, id) }