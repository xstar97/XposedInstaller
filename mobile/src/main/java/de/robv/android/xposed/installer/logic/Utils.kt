package de.robv.android.xposed.installer.logic

import android.util.Log
import de.robv.android.xposed.installer.XposedApp

open class Utils
{
    val PREF_NAV = "default_navigation"
    val PREF_VIEW = "default_view"
    val NAV_BOTTOM = 0

    fun getNav(): Int{
        val  id = getPrefValue(PREF_NAV)
        Log.d(XposedApp.TAG, "PREF_NAV: id: $id")
        return id
    }
    fun getPrefValue(key: String): Int{
        return try {
            XposedApp.getPreferences().getString(key, "0").toInt()
        }catch (e: Exception){
            Log.w(XposedApp.TAG, e.message)
            0
        }
    }
}