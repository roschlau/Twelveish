/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PorterDuff.Mode
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.wear.widget.WearableLinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.layoutxml.twelveish.R
import com.layoutxml.twelveish.objects.Color
import kotlinx.android.synthetic.main.imageview_and_textview_item.view.*
import kotlinx.android.synthetic.main.wearablerecyclerview_activity.*

class ColorOptionsActivity : Activity() {
    private var mAdapter: ColorsAdapter? = null
    private var prefs: SharedPreferences? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wearablerecyclerview_activity)

        prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        mAdapter = ColorsAdapter()
        wearable_recycler_view.apply {
            layoutManager = WearableLinearLayoutManager(this@ColorOptionsActivity)
            isEdgeItemsCenteringEnabled = true
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
        mAdapter!!.notifyDataSetChanged()
    }

    inner class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.MyViewHolder>() {

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            var name: TextView = view.settingsListTextView
            var icon: ImageView = view.settingsListImagetView

            init {
                view.setOnClickListener {
                    val position = adapterPosition // gets item position
                    val selectedMenuItem = COLORS[position]
                    prefs!!.edit().putInt(getString(R.string.preference_background_color), selectedMenuItem.colorcode)
                        .apply()
                    Toast.makeText(
                        applicationContext,
                        "\"" + selectedMenuItem.name + "\" set",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ColorsAdapter.MyViewHolder {
            Log.d(TAG, "MyViewHolder onCreateViewHolder")
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.imageview_and_textview_item, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ColorsAdapter.MyViewHolder, position: Int) {
            Log.d(TAG, "MyViewHolder onBindViewHolder")
            val color = COLORS[position]
            holder.name.text = color.name
            holder.icon.setColorFilter(color.colorcode, Mode.SRC_IN)
        }

        override fun getItemCount(): Int {
            return COLORS.size
        }
    }

    companion object {
        private const val TAG = "ColorOptionsActivity"

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
