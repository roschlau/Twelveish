package com.layoutxml.twelveish

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

fun Context.registerReceiver(action: String, onReceive: (Context, Intent) -> Unit) {
    registerReceiver(object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onReceive(context, intent)
        }
    }, IntentFilter(action))
}