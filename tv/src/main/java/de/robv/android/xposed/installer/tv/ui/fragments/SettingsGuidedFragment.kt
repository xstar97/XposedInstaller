package de.robv.android.xposed.installer.tv.ui.fragments

import android.os.Bundle
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.ui.fragments.base.BasePreferenceGuidedFragment

class SettingsGuidedFragment: BasePreferenceGuidedFragment()
{
    companion object {
        val TAG: String = SettingsGuidedFragment::class.java.simpleName
        fun newInstance() = SettingsGuidedFragment()
    }

    private val checkAble = 12

    private val releaseTypeGlobal = 0
    private val releaseTypeGlobalStable = 1
    private val releaseTypeGlobalBeta = 2
    private val releaseTypeGlobalExperimental = 3

    private val disableResources = 4
    private val disableResourcesChecked = disableResources + checkAble

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.nav_item_settings),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        try {

            actions.add(addReleaseTypeGlobal())

            actions.add(addDisableResources())

        }catch (npe: NullPointerException){
            Log.e(XposedApp.TAG, "npe: ${npe.message}")
        }catch (e: Exception){
            Log.e(XposedApp.TAG, "e: ${e.message}")
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        val pos = action!!.id.toInt()
        Log.v(XposedApp.TAG, "pos: $pos")


        when (pos) {
            releaseTypeGlobal -> {
                //
            }
            disableResources -> {
                /*if (action.checkSetId == disableResourcesChecked) {
                //mSelectedOption = selectedActionPosition -1
                    action.isChecked = false
                }*/
                notifyActionChanged(findActionPositionById(disableResources.toLong()))
            }
        }
    }

    override fun onSubGuidedActionClicked(action: GuidedAction?): Boolean {
        val pos = action!!.id.toInt()
        Log.v(XposedApp.TAG, "pos: $pos")
        val releaseTypePos = pos == releaseTypeGlobalStable || pos == releaseTypeGlobalBeta || pos == releaseTypeGlobalExperimental

        var releaseTypeTitle = ""
        if (releaseTypePos)
         releaseTypeTitle = action.title.toString()

        when (pos) {
            releaseTypeGlobalStable ->{
                findActionById(releaseTypeGlobal.toLong()).description = releaseTypeTitle
                notifyActionChanged(findActionPositionById(releaseTypeGlobal.toLong()))
            }
            releaseTypeGlobalBeta ->{
                findActionById(releaseTypeGlobal.toLong()).description = releaseTypeTitle
                notifyActionChanged(findActionPositionById(releaseTypeGlobal.toLong()))
            }
            releaseTypeGlobalExperimental -> {
            findActionById(releaseTypeGlobal.toLong()).description = releaseTypeTitle
                notifyActionChanged(findActionPositionById(releaseTypeGlobal.toLong()))
            }
        }
        return true
    }

    private fun addReleaseTypeGlobal(): GuidedAction{
        return GuidedAction.Builder(activity!!)
                .id(releaseTypeGlobal.toLong())
                .title(activity!!.getString(R.string.settings_release_type))
                .description(getReleaseTypeGlobalSummary())
                .subActions(getActionsFromList(releaseTypeGlobalSubList()))
                .build()
    }

    private fun addDisableResources(): GuidedAction{
        return GuidedAction.Builder(activity!!)
                .id(disableResources.toLong())
                .title(activity!!.getString(R.string.settings_disable_resources))
                .description(activity!!.getString(R.string.settings_disable_resources_summary))
                .checkSetId(disableResourcesChecked)
                .checked(getDisableResourcesCheckState())
                .build()
    }

    private fun getReleaseTypeGlobalSummary(): String{
        return XposedApp.getPreferences().getString("release_type_global", "stable")
    }
    private fun getDisableResourcesCheckState(): Boolean{
        return XposedApp.getPreferences().getBoolean("disable_resources", false)
    }
    private fun releaseTypeGlobalSubList(): ArrayList<InfoModel>{
        val infoList = ArrayList<InfoModel>()
        infoList.add(InfoModel(releaseTypeGlobalStable, 0, activity!!.getString(R.string.reltype_stable_summary), ""))
        infoList.add(InfoModel(releaseTypeGlobalBeta, 0, activity!!.getString(R.string.reltype_beta_summary), ""))
        infoList.add(InfoModel(releaseTypeGlobalExperimental, 0,activity!!.getString(R.string.reltype_experimental_summary), ""))
        return infoList
    }
}