package de.robv.android.xposed.installer.tv.ui.installation

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.installation.Flashable
import de.robv.android.xposed.installer.tv.XposedApp

class InstallationActivity: FragmentActivity() {

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        setContentView(R.layout.activity_installation)

        val flashable = intent.getParcelableExtra<Flashable>(Flashable.KEY)
        if (flashable == null) {
            Log.e(XposedApp.TAG, InstallationActivity::class.java.name + ": Flashable is missing")
            finish()
            return
        }

        if (savedInstanceBundle == null) {
            val logFragment = InstallationFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content, logFragment).commit()
            logFragment.startInstallation(this, flashable)
        }
    }
}