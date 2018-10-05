package com.layoutxml.twelveish.activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.wear.widget.WearableLinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.layoutxml.twelveish.R
import kotlinx.android.synthetic.main.wearablerecyclerview_activity.*

abstract class PreferencesActivity<Item>(
    protected val values: List<Item>,
    protected val viewLayout: Int,
    protected val viewHolder: (View) -> BindableHolder<Item>
) : Activity() {

    protected abstract fun SharedPreferences.Editor.save(position: Int, item: Item)

    protected abstract fun getConfirmationMessage(item: Item): String

    private var mAdapter: RecyclerView.Adapter<BindableHolder<Item>>? = null
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wearablerecyclerview_activity)

        prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        mAdapter = getAdapter()
        wearable_recycler_view.apply {
            layoutManager = WearableLinearLayoutManager(this@PreferencesActivity)
            isEdgeItemsCenteringEnabled = true
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
        mAdapter!!.notifyDataSetChanged()
    }

    private fun getAdapter() = object : RecyclerView.Adapter<BindableHolder<Item>>() {

        override fun onBindViewHolder(holder: BindableHolder<Item>, position: Int) {
            val value = values[position]
            holder.itemView.setOnClickListener {
                val selectedMenuItem = values[position]
                prefs!!.edit().apply { save(position, selectedMenuItem) }.apply()
                Toast.makeText(
                    applicationContext,
                    getConfirmationMessage(selectedMenuItem),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            holder.bind(value)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableHolder<Item> {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(viewLayout, parent, false)
            return viewHolder(itemView)
        }

        override fun getItemCount() =
            values.count()
    }

}

abstract class BindableHolder<Item>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: Item)
}