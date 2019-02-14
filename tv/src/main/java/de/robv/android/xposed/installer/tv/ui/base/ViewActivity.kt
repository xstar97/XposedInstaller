package de.robv.android.xposed.installer.tv.ui.base

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.util.Log
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.logic.Navigation
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
    private fun getFrag(): Navigation {
        return try {
            val nav = this.intent.extras!!.get(INTENT_NAV_KEY) as Navigation
            Log.d(XposedApp.TAG, "$INTENT_NAV_KEY: $nav")
            nav
        }catch (e: Exception){
            Log.w(XposedApp.TAG, e.message)
            Navigation.FRAG_ERROR
        }
    }
}