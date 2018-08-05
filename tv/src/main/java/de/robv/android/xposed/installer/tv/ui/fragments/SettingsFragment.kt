package de.robv.android.xposed.installer.tv.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import android.support.v17.leanback.widget.GuidedAction.CHECKBOX_CHECK_SET_ID
import android.util.Log
import android.widget.Toast
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseGuidedFragment
import java.io.File
import java.io.IOException

//TODO add more preferences!
class SettingsFragment: BaseGuidedFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener
{
    companion object {
        val TAG: String = SettingsFragment::class.java.simpleName
        private val mDisableResourcesFlag = File(XposedApp().BASE_DIR + "conf/disable_resources")
        fun newInstance() = SettingsFragment()
    }

    private val releaseTypeGlobal = 0
    private val releaseTypeGlobalStable = 1
    private val releaseTypeGlobalBeta = 2
    private val releaseTypeGlobalExperimental = 3

    private val disableResources = 4

    private val PREF_TYPE = "release_type_global"
    private val PREF_RES = "disable_resources"
    private val PREF_THEME = "theme"
    private val PREF_VIEW ="default_view"

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        Log.d(XposedApp.TAG, "onSharedPreferenceChanged key: $key")
        when(key){
            PREF_TYPE ->{
                findActionById(releaseTypeGlobal.toLong()).description = getReleaseTypeGlobalSummary()
                notifyActionChanged(findActionPositionById(releaseTypeGlobal.toLong()))
            }
            PREF_RES ->{
                //nothing to do here:/
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
            actions.add(getReleaseTypeGlobal())
            actions.add(getDisableResources())
        }catch (npe: NullPointerException){
            Log.e(XposedApp.TAG, "onCreateActions: npe: ${npe.message}")
        }catch (e: Exception){
            Log.e(XposedApp.TAG, "onCreateActions: e: ${e.message}")
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        val pos = action!!.id.toInt()
        Log.v(XposedApp.TAG, "onGuidedActionClicked: pos: $pos")


        when (pos) {
            releaseTypeGlobal -> {
                //nothing to do here:/
            }
            disableResources -> {
                if(action.isChecked){
                    try {
                        mDisableResourcesFlag.createNewFile()
                        Log.v(XposedApp.TAG, "creating...${mDisableResourcesFlag.name}")
                    } catch (e: IOException) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    mDisableResourcesFlag.delete()
                    Log.v(XposedApp.TAG, "deleting...${mDisableResourcesFlag.name}")
                }
            }
        }
    }

    override fun onSubGuidedActionClicked(action: GuidedAction?): Boolean {
        val pos = action!!.id.toInt()
        Log.v(XposedApp.TAG, "onSubGuidedActionClicked: posSub: $pos")

        when(pos){
            releaseTypeGlobalStable -> {
                val stable = releaseTypeValues()[0]
                XposedApp.getPreferences().edit().putString(PREF_TYPE, stable).apply()
            }
            releaseTypeGlobalBeta -> {
                val beta = releaseTypeValues()[1]
                XposedApp.getPreferences().edit().putString(PREF_TYPE, beta).apply()
            }
            releaseTypeGlobalExperimental -> {
                val experimental = releaseTypeValues()[2]
                XposedApp.getPreferences().edit().putString(PREF_TYPE, experimental).apply()
            }
        }


        return true
    }

    //actions
    private fun getReleaseTypeGlobal(): GuidedAction{
        return GuidedAction.Builder(activity!!)
                .id(releaseTypeGlobal.toLong())
                .title(activity!!.getString(R.string.settings_release_type))
                .description(getReleaseTypeGlobalSummary())
                .subActions(getActionsFromList(activity!!, releaseTypeGlobalSubList()))
                .build()
    }
    private fun getDisableResources(): GuidedAction{
        return GuidedAction.Builder(activity!!)
                .id(disableResources.toLong())
                .title(activity!!.getString(R.string.settings_disable_resources))
                .description(activity!!.getString(R.string.settings_disable_resources_summary))
                .checkSetId(CHECKBOX_CHECK_SET_ID)
                .checked(getDisableResourcesCheckState())
                .build()
    }

    private fun getReleaseTypeGlobalSummary(): String{
        return XposedApp.getPreferences().getString("release_type_global", releaseTypeValues()[0])
    }
    private fun getDisableResourcesCheckState(): Boolean{
        return mDisableResourcesFlag.exists()
    }

    private fun releaseTypeGlobalSubList(): ArrayList<InfoModel>{
        val list = ArrayList<InfoModel>()
        val stable = releaseTypeText()[0]
        val beta = releaseTypeText()[1]
        val experimental = releaseTypeText()[2]
        Log.v(XposedApp.TAG, "stable: $stable\nbeta: $beta\nexperimental: $experimental")
        list.add(InfoModel(releaseTypeGlobalStable, null, stable, ""))
        list.add(InfoModel(releaseTypeGlobalBeta, null, beta, ""))
        list.add(InfoModel(releaseTypeGlobalExperimental, null, experimental, ""))
        return list
    }
    private fun releaseTypeText(): Array<String>{
        return resources.getStringArray(R.array.release_type_texts)
    }
    private fun releaseTypeValues(): Array<String>{
        return activity!!.resources.getStringArray(R.array.release_type_values)
    }
}