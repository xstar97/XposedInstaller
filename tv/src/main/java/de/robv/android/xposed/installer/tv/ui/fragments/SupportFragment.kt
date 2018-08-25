package de.robv.android.xposed.installer.tv.ui.fragments

import android.os.Bundle
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseSupport
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseGuidedFragment

class SupportFragment : BaseGuidedFragment()
{
    companion object {
        val TAG: String = SupportFragment::class.java.simpleName
        fun newInstance() = SupportFragment()
    }
    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.nav_item_support),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        try {
            val list = getActionsFromList(activity!!, BaseSupport().getSupportList(activity!!))
            for (about in list) {
                actions.add(about)
            }
        }catch (npe: NullPointerException){
            Log.e(XposedApp.TAG, "npe: ${npe.message}")
        }catch (e: Exception){
            Log.e(XposedApp.TAG, "e: ${e.message}")
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        val pos = action!!.id.toInt()
        when (pos) {
            BaseSupport.supportModulesLabel -> {

            }
            BaseSupport.supportFrameworkLabel -> {
                BaseSupport().showSupportPage(activity!!)
            }
            BaseSupport.supportFaqLabel -> {
                BaseSupport().showIssuesPage(activity!!)
            }
            BaseSupport.supportDonateLabel -> {
                BaseSupport().showDonationPage(activity!!)
            }
        }
    }
}