package de.robv.android.xposed.installer.ui.activities.containers

import android.os.Bundle
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.*
import de.robv.android.xposed.installer.ui.activities.XposedBaseActivity
import kotlinx.android.synthetic.main.view_toolbar.*

class ViewActivity: XposedBaseActivity()
{
    companion object {
        val TAG: String = ViewActivity::class.java.simpleName
        const val INTENT_ACTIVITY_KEY = "initActivity"
        fun newInstance() = ViewActivity()
    }

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        ThemeUtil.setTheme(this)
        setContentView(R.layout.activity_container)

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { finish() }

        val ab = supportActionBar
        if (ab != null) {
            ab.title = setFragTitle()
            ab.setDisplayHomeAsUpEnabled(true)
        }

        setFloating(toolbar, 0)

        if (savedInstanceBundle == null) {
            setActivityFragment()
        }
    }
    private fun setFragTitle(): String {
        return when(getFragTag()){
            NavigationPosition.DOWNLOAD.getTag() -> getString(R.string.nav_item_download)
            NavigationPosition.MODULES.getTag() -> getString(R.string.nav_item_modules)
            NavigationPosition.HOME.getTag() -> getString(R.string.nav_item_install)
            NavigationPosition.LOGS.getTag() -> getString(R.string.nav_item_logs)
            NavigationPosition.SETTINGS.getTag() -> getString(R.string.nav_item_settings)
            NavigationPosition.SUPPORT.getTag() -> getString(R.string.nav_item_support)
            NavigationPosition.ABOUT.getTag() -> getString(R.string.nav_item_about)
            else -> getString(R.string.nav_item_install)
        }
    }
    private fun setActivityFragment(){
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, Utils().getFragment(getFragTag())).commit()
    }

    private fun getFragTag(): String{
        val intent = this.intent.extras!!.get(INTENT_ACTIVITY_KEY).toString()
        Log.d(XposedApp.TAG, "$INTENT_ACTIVITY_KEY: $intent")
        return intent
    }
}