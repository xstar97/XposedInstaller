package de.robv.android.xposed.installer.tv.ui.settings

import android.os.Bundle
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedAction.CHECKBOX_CHECK_SET_ID
import android.util.Log
import android.widget.Toast
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseSettings
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.ui.base.BaseGuidedFragment
import java.io.IOException

//TODO add more preferences!
class SettingsFragment: BaseGuidedFragment()
{
    companion object {
        val TAG: String = SettingsFragment::class.java.simpleName
        fun newInstance() = SettingsFragment()
    }

    private val releaseTypeGlobal = 0
    private val childReleaseTypeGlobalStable = 0
    private val childReleaseTypeGlobalBeta = 1
    private val childReleaseTypeGlobalExperimental = 2

    private val disableResources = 4

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
                        BaseSettings.mDisableResourcesFlag.createNewFile()
                        Log.v(XposedApp.TAG, "creating...${BaseSettings.mDisableResourcesFlag.name}")
                    } catch (e: IOException) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    BaseSettings.mDisableResourcesFlag.delete()
                    Log.v(XposedApp.TAG, "deleting...${BaseSettings.mDisableResourcesFlag.name}")
                }
            }
        }
    }

    override fun onSubGuidedActionClicked(action: GuidedAction?): Boolean {
        val pos = action!!.id.toInt()
        Log.v(XposedApp.TAG, "onSubGuidedActionClicked: posSub: $pos")

        when(pos){
            childReleaseTypeGlobalStable, childReleaseTypeGlobalBeta, childReleaseTypeGlobalExperimental ->{
                val type = releaseTypeValues()[pos]
                Log.d(XposedApp.TAG, "pos: $pos\nType: $type")
                XposedApp.getPreferences().edit().putString(BaseSettings.prefType, type).apply()
                findActionById(releaseTypeGlobal.toLong()).description = type//getReleaseTypeGlobalSummary()
                notifyActionChanged(findActionPositionById(releaseTypeGlobal.toLong()))
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
                .subActions(getActionsFromList(activity!!, releaseTypeText()))
                .build()
    }
    private fun getDisableResources(): GuidedAction{
        return GuidedAction.Builder(activity!!)
                .id(disableResources.toLong())
                .title(activity!!.getString(R.string.settings_disable_resources))
                .description(activity!!.getString(R.string.settings_disable_resources_summary))
                .checkSetId(CHECKBOX_CHECK_SET_ID)
                .checked(getDisableResourcesCheckState())
                .enabled(false)
                .build()
    }

    private fun getReleaseTypeGlobalSummary(): String?{
        return XposedApp.getPreferences().getString(BaseSettings.prefType, releaseTypeValues()[0])
    }
    private fun getDisableResourcesCheckState(): Boolean{
        return BaseSettings.mDisableResourcesFlag.exists()
    }

    private fun releaseTypeText(): Array<String>{
        return resources.getStringArray(R.array.release_type_texts)
    }
    private fun releaseTypeValues(): Array<String>{
        return resources.getStringArray(R.array.release_type_values)
    }
}