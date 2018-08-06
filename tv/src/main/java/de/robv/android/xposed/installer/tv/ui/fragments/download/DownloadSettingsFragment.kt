package de.robv.android.xposed.installer.tv.ui.fragments.download

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import android.support.v17.leanback.widget.GuidedAction.CHECKBOX_CHECK_SET_ID
import android.support.v17.leanback.widget.GuidedAction.DEFAULT_CHECK_SET_ID
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseGuidedFragment

class DownloadSettingsFragment: BaseGuidedFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener
{
    companion object {
    val TAG: String = DownloadSettingsFragment::class.java.simpleName
    fun newInstance() = DownloadSettingsFragment()
    const val PREF_SORT = "download_sorting_order"
}
    private val prefSortStatus = 0
    private val prefSortUpdate = 1
    private val prefSortCreate = 2

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(XposedApp.TAG, "onSharedPreferenceChanged key: $key")
        when(key){
            PREF_SORT ->{
                activity!!.recreate()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = XposedApp.getPreferences()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
    override fun onResume() {
        super.onResume()
        XposedApp.getPreferences().registerOnSharedPreferenceChangeListener(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        XposedApp.getPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.nav_item_settings),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        try {
            val list = sortingOrderAction()
            for (action in list) {
                Log.v(XposedApp.TAG, "action: ${action.title}")
                actions.add(action)
            }
        }catch (npe: NullPointerException){
            Log.e(XposedApp.TAG, "npe: ${npe.message}")
        }catch (e: Exception){
            Log.e(XposedApp.TAG, "e: ${e.message}")
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        val pos = action!!.id.toInt()
        when(pos){
            prefSortStatus -> {
                XposedApp.getPreferences().edit().putInt(PREF_SORT, pos).apply()
            }
            prefSortUpdate -> {
                XposedApp.getPreferences().edit().putInt(PREF_SORT, pos).apply()
            }
            prefSortCreate -> {
                XposedApp.getPreferences().edit().putInt(PREF_SORT, pos).apply()
            }
        }
    }


    private fun sortingOrderAction(): ArrayList<GuidedAction>{
        val initList  = downloadSortOrder()
        val actions = ArrayList<GuidedAction>()
        for ((i, status) in initList.withIndex()) {
            Log.v(XposedApp.TAG, "status: $status")
            actions.add(GuidedAction.Builder(activity!!)
            .id(i.toLong())
            .title(status)
            .checkSetId(DEFAULT_CHECK_SET_ID)
            .build())
        }
        return actions
    }

    private fun downloadSortOrder(): Array<String>{
        return resources.getStringArray(R.array.download_sort_order)
    }
}