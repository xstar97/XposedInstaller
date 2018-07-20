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
        return when(getFragPos()){
            NavigationPosition.DOWNLOAD.getPos() -> getString(R.string.nav_item_download)
            NavigationPosition.MODULES.getPos() -> getString(R.string.nav_item_modules)
            NavigationPosition.HOME.getPos() -> getString(R.string.nav_item_install)
            NavigationPosition.LOGS.getPos() -> getString(R.string.nav_item_logs)
            NavigationPosition.SETTINGS.getPos() -> getString(R.string.nav_item_settings)
            NavigationPosition.SUPPORT.getPos() -> getString(R.string.nav_item_support)
            NavigationPosition.ABOUT.getPos() -> getString(R.string.nav_item_about)
            //TODO add error fragment
            else -> getString(R.string.nav_item_install)
        }
    }
    private fun setActivityFragment(){
        val nav = NavigationPosition.values()
        val activity = nav[getFragPos()].createFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, activity).commit()
    }

    private fun getFragPos(): Int{
        val intent = this.intent.extras!!.get(INTENT_ACTIVITY_KEY).toString().toInt()
        Log.d(XposedApp.TAG, "$INTENT_ACTIVITY_KEY: $intent")
        return intent
    }
}