package de.robv.android.xposed.installer.tv.ui.main

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import de.robv.android.xposed.installer.R

class WelcomeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}