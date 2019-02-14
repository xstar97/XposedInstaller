package de.robv.android.xposed.installer.mobile.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.robv.android.xposed.installer.mobile.ui.main.WelcomeActivity
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.startActivity<WelcomeActivity>()
        finish()
    }
}