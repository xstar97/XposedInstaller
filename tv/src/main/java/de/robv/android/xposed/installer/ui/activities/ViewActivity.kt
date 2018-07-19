package de.robv.android.xposed.installer.ui.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.Utils

class ViewActivity: FragmentActivity()
{
    companion object {
        val TAG: String = ViewActivity::class.java.simpleName
        const val INTENT_ACTIVITY_KEY = "initActivity"
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
                .replace(android.R.id.content, Utils().getFragment(getFragTag())).commit()
    }
    private fun getFragTag(): String{
        val intent = this.intent.extras!!.get(INTENT_ACTIVITY_KEY).toString()
        Log.d(XposedApp.TAG, "$INTENT_ACTIVITY_KEY: $intent")
        return intent
    }
}