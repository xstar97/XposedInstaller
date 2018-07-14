package de.robv.android.xposed.installer.ui.activities

import android.os.Bundle
import android.widget.Toast

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog

import java.io.File

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.ui.fragments.SettingsFragment
import de.robv.android.xposed.installer.logic.ThemeUtil
import kotlinx.android.synthetic.main.view_toolbar.*

class SettingsActivity : XposedBaseActivity(), FolderChooserDialog.FolderCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.setTheme(this)
        setContentView(R.layout.activity_container)

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { finish() }

        val ab = supportActionBar
        if (ab != null) {
            ab.setTitle(R.string.nav_item_settings)
            ab.setDisplayHomeAsUpEnabled(true)
        }

        setFloating(toolbar, 0)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SettingsFragment()).commit()
        }

    }

    override fun onFolderSelection(dialog: FolderChooserDialog, folder: File) {
        if (folder.canWrite()) {
            XposedApp.getPreferences().edit().putString("download_location", folder.path).apply()
        } else {
            Toast.makeText(this, R.string.sdcard_not_writable, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFolderChooserDismissed(dialog: FolderChooserDialog) {

    }

}