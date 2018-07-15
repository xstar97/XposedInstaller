package de.robv.android.xposed.installer.logic

import android.util.Log
import de.robv.android.xposed.installer.XposedApp

open class Utils
{
    companion object {
        val PREF_NAV = "default_navigation"
        val PREF_VIEW = "default_view"
        val NAV_BOTTOM = 0
        val NAV_DRAWER = 1

        fun isBottomNav(): Boolean {
            val nav = getNav() == NAV_BOTTOM
            Log.d(XposedApp.TAG, "isBottomNav: $nav")
            return nav
        }

        fun getNav(): Int {
            val id = getPrefValue(PREF_NAV)
            Log.d(XposedApp.TAG, "$PREF_NAV: id: $id")
            return id
        }

        fun getView(): Int {
            val id = getPrefValue(PREF_VIEW)
            Log.d(XposedApp.TAG, "$PREF_VIEW: id: $id")
            return id
        }

        fun getPrefValue(key: String): Int {
            return try {
                XposedApp.getPreferences().getString(key, "0").toInt()
            } catch (e: Exception) {
                Log.w(XposedApp.TAG, e.message)
                0
            }
        }

    }
}