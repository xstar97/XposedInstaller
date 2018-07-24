package de.robv.android.xposed.installer.mobile.ui.activities.containers

import android.os.Bundle
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.NavigationPosition
import de.robv.android.xposed.installer.mobile.logic.ThemeUtil
import de.robv.android.xposed.installer.mobile.logic.createFragment
import de.robv.android.xposed.installer.mobile.logic.getTitle
import de.robv.android.xposed.installer.mobile.ui.activities.XposedBaseActivity
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
        return NavigationPosition.values()[getFragPos()].getTitle(this)
    }
    private fun setActivityFragment(){
        val activity = NavigationPosition.values()[getFragPos()].createFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, activity).commit()
    }

    private fun getFragPos(): Int{
        val intent = this.intent.extras!!.get(INTENT_ACTIVITY_KEY).toString().toInt()
        Log.d(XposedApp.TAG, "$INTENT_ACTIVITY_KEY: $intent")
        return intent
    }
}