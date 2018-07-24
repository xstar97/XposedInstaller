package de.robv.android.xposed.installer.tv.ui.fragments

import android.os.Bundle
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.BaseSupport
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseGuidedFragment
import org.jetbrains.anko.AnkoLogger

class SupportFragment : BaseGuidedFragment(), AnkoLogger
{
    companion object {
        val TAG: String = SupportFragment::class.java.simpleName
        fun newInstance() = SupportFragment()
    }
    val supportModulesLabel = 0
    val supportFrameworkLabel = 1
    val supportFaqLabel = 2
    val supportDonateLabel = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.nav_item_support),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        try {
            val list = getActionsFromList(supportList())
            for (about in list) {
                actions.add(about)
            }
        }catch (npe: NullPointerException){
            error {"npe: ${npe.message}"}
        }catch (e: Exception){
            error {"e: ${e.message}"}
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        val pos = action!!.id.toInt()
        when (pos) {
            supportModulesLabel -> {

            }
            supportFrameworkLabel -> {
                BaseSupport.showSupportPage(activity!!)
            }
            supportFaqLabel -> {
                BaseSupport.showIssuesPage(activity!!)
            }
            supportDonateLabel -> {
                BaseSupport.showDonationPage(activity!!)

            }
        }
    }

    private fun supportList(): ArrayList<InfoModel>{
        val infoList = ArrayList<InfoModel>()
        infoList.add(InfoModel(supportModulesLabel, R.drawable.ic_info, getString(R.string.support_modules_label), getString(R.string.support_modules_description, getString(R.string.module_support))))
        infoList.add(InfoModel(supportFrameworkLabel, R.drawable.ic_help, getString(R.string.support_framework_label), ""))
        infoList.add(InfoModel(supportFaqLabel, R.drawable.ic_nav_logs, getString(R.string.support_faq_label), ""))
        infoList.add(InfoModel(supportDonateLabel, R.drawable.ic_donate, getString(R.string.support_donate_label), getString(R.string.support_donate_description)))
        return infoList
    }
}