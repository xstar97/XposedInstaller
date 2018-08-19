package de.robv.android.xposed.installer.mobile.ui.activities.containers

import android.os.Bundle
import android.util.Log
import de.robv.android.xposed.installer.R.id.container
import de.robv.android.xposed.installer.R.layout.activity_container
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.Navigation
import de.robv.android.xposed.installer.mobile.logic.ThemeUtil
import de.robv.android.xposed.installer.mobile.logic.createFragment
import de.robv.android.xposed.installer.mobile.ui.activities.XposedBaseActivity
import kotlinx.android.synthetic.main.view_toolbar.*

class ViewActivity: XposedBaseActivity()
{
    companion object {
        val TAG: String = ViewActivity::class.java.simpleName
        const val INTENT_NAV_KEY = "initNav"
        fun newInstance() = ViewActivity()
    }

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        ThemeUtil.setTheme(this)
        setContentView(activity_container)

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
        return getString(getFrag().title)
    }
    private fun setActivityFragment(){
        supportFragmentManager.beginTransaction().replace(container, getFrag().createFragment()).commit()
    }

    private fun getFrag(): Navigation {
        return try {
            val nav = this.intent.extras!!.get(INTENT_NAV_KEY) as Navigation
            Log.d(XposedApp.TAG, "$INTENT_NAV_KEY: ${getString(nav.title)}")
            nav
        }catch (e: Exception){
            Log.w(XposedApp.TAG, e.message)
            Navigation.NAV_HOME
        }
    }
}