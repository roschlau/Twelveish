/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.wear.widget.WearableLinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.layoutxml.twelveish.R
import com.layoutxml.twelveish.objects.Capitalisation
import kotlinx.android.synthetic.main.wearablerecyclerview_activity.*

class CapitalisationActivity : Activity() {
    private var mAdapter: CapitalisationAdapter? = null
    private var prefs: SharedPreferences? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wearablerecyclerview_activity)

        prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        mAdapter = CapitalisationAdapter()
        wearable_recycler_view.apply {
            layoutManager = WearableLinearLayoutManager(this@CapitalisationActivity)
            isEdgeItemsCenteringEnabled = true
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
        mAdapter!!.notifyDataSetChanged()
    }

    inner class CapitalisationAdapter : RecyclerView.Adapter<CapitalisationAdapter.MyViewHolder>() {

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            var name: TextView = view.findViewById(R.id.dateoptionslistListTextView)

            init {
                view.setOnClickListener {
                    val position = adapterPosition // gets item position
                    prefs!!.edit().putInt(getString(R.string.preference_capitalisation), position)
                        .apply()
                    Toast.makeText(
                        applicationContext,
                        "Capitalisation mode set",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CapitalisationAdapter.MyViewHolder {
            Log.d(TAG, "MyViewHolder onCreateViewHolder")
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.textview_item, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CapitalisationAdapter.MyViewHolder, position: Int) {
            Log.d(TAG, "MyViewHolder onBindViewHolder")
            holder.name.text = CAPITALISATIONS[position].name
        }

        override fun getItemCount(): Int {
            return CAPITALISATIONS.size
        }
    }

    companion object {
        private const val TAG = "CapitalisationOptionsAc"
        val CAPITALISATIONS = listOf(
            "All words title case",
            "All uppercase",
            "All lowercase",
            "First word title case",
            "First word in every line title case"
        ).map(::Capitalisation)
    }

}
