package de.robv.android.xposed.installer.tv.ui.activities.containers

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.logic.NavigationPosition
import de.robv.android.xposed.installer.tv.logic.createFragment

class ViewActivity: FragmentActivity()
{
    companion object {
        val TAG: String = ViewActivity::class.java.simpleName
        const val INTENT_NAV_KEY = "initNAV"
        fun newInstance() = ViewActivity()
    }

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)

        if (savedInstanceBundle == null) {
            setActivityFragment()
        }
    }
    private fun setActivityFragment(){
        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, getFrag().createFragment()).commit()
    }

    private fun getFrag(): NavigationPosition {
        return try {
            val nav = this.intent.extras!!.get(INTENT_NAV_KEY) as NavigationPosition
            Log.d(XposedApp.TAG, "$INTENT_NAV_KEY: ${getString(nav.title)}")
            nav
        }catch (e: Exception){
            Log.w(XposedApp.TAG, e.message)
            NavigationPosition.ERROR
        }
    }
}