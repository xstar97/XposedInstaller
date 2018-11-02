package de.robv.android.xposed.installer.mobile.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.robv.android.xposed.installer.mobile.ui.main.WelcomeActivity
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.startActivity<WelcomeActivity>()
        finish()
    }
}