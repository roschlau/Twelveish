package com.layoutxml.twelveish.activities

import android.app.Activity
import android.os.Bundle

import com.layoutxml.twelveish.BuildConfig
import com.layoutxml.twelveish.R
import kotlinx.android.synthetic.main.licenses.*

class Licenses : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.licenses)
        license2.text = BuildConfig.VERSION_NAME
        license3.text = BuildConfig.VERSION_CODE.toString()
    }
}
