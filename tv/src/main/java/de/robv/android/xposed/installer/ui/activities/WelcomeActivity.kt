package de.robv.android.xposed.installer.ui.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.ui.fragments.MainFragment

class WelcomeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //if (savedInstanceState == null) {
        //    supportFragmentManager.beginTransaction().replace(R.id.main_fragment, MainFragment()).commit()
        //}
    }
}