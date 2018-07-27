package de.robv.android.xposed.installer.mobile.logic

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.View
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.ui.activities.containers.ViewActivity
import de.robv.android.xposed.installer.mobile.ui.fragments.containers.ViewBottomSheetFragment
import de.robv.android.xposed.installer.mobile.ui.fragments.containers.ViewDialogFragment
import org.jetbrains.anko.startActivity

open class Utils
{
    companion object {
        const val PREF_NAV = "default_navigation"
        const val PREF_VIEW = "default_view"
        const val NAV_BOTTOM = 0
        const val NAV_DRAWER = 1
    }

    fun isBottomNav(): Boolean {
        return try {
            val nav = getNav() == NAV_BOTTOM
            Log.d(XposedApp.TAG, "${PREF_NAV}_pref -> boolean: $nav")
            nav
        }catch (e: Exception){
            Log.e(XposedApp.TAG, e.message)
            true
        }
    }

    fun getNav(): Int {
        val id = getPrefValue(PREF_NAV)
        Log.d(XposedApp.TAG, "${PREF_NAV}_pref -> id: $id")
        return id
    }

    fun getView(): Int {
        val id = getPrefValue(PREF_VIEW)
        Log.d(XposedApp.TAG, "${PREF_VIEW}_pref -> id: $id")
        return id
    }

    fun getPrefValue(key: String): Int {
        return try {
            val value = XposedApp.getPreferences().getString(key, "0").toInt()
            Log.d(XposedApp.TAG, "key: $key\nvalue: $value")
            value
        } catch (e: Exception) {
            Log.w(XposedApp.TAG, e.message)
            0
        }
    }

    fun launchViewActivity(context: Context, nav: NavigationPosition){
        context.startActivity<ViewActivity>(ViewActivity.INTENT_NAV_KEY to nav)
    }
    fun launchSheet(fragmentManager: android.support.v4.app.FragmentManager, nav: NavigationPosition){
        val bottomSheetFragment = ViewBottomSheetFragment.newInstance(nav)
        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
    }
    fun launchDialog(fragmentManager: android.support.v4.app.FragmentManager, nav: NavigationPosition){
        val dialog = ViewDialogFragment.newInstance(nav)
        dialog.show(fragmentManager, dialog.tag)
    }
    fun launchMenu(context: Context, v: View, menu: Int, delegate: PopupMenu.OnMenuItemClickListener): PopupMenu{
        val popUp = PopupMenu(context, v)
        popUp.menuInflater.inflate(menu, popUp.menu)
        popUp.setOnMenuItemClickListener(delegate)
        return popUp
    }
}