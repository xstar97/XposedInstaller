package de.robv.android.xposed.installer.tv.ui.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import org.jetbrains.anko.startActivity

class SplashActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.startActivity<WelcomeActivity>()
        finish()
    }
}