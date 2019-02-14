package de.robv.android.xposed.installer.mobile.logic

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseSettings
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.ui.base.ViewActivity
import de.robv.android.xposed.installer.mobile.ui.base.ViewBottomSheetFragment
import de.robv.android.xposed.installer.mobile.ui.base.ViewDialogFragment
import org.jetbrains.anko.startActivity

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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

    private fun getPrefValue(key: String): Int {
        return try {
            val value = XposedApp.getPreferences().getString(key, "0")!!.toInt()
            Log.d(XposedApp.TAG, "key: $key\nvalue: $value")
            value
        } catch (e: Exception) {
            Log.w(XposedApp.TAG, e.message)
            0
        }
    }

    fun launchView(context: Any?, nav: Navigation?){
        val subView = XposedApp.getPreferences().getString(BaseSettings.prefSubView, "0").toInt()
        when(context){
            is FragmentManager ->{
                when(subView){
                    0 -> {
                        val bottomSheetFragment = ViewBottomSheetFragment.newInstance(nav!!)
                        bottomSheetFragment.show(context, bottomSheetFragment.tag)
                    }
                    1 -> {
                        val dialog = ViewDialogFragment.newInstance(nav!!)
                        dialog.show(context, dialog.tag)
                    }
                }

            }
            is Context ->{
                context.startActivity<ViewActivity>(ViewActivity.INTENT_NAV_KEY to nav)
            }
        }
    }

}