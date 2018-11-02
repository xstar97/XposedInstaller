package de.robv.android.xposed.installer.mobile.ui.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseSupport
import de.robv.android.xposed.installer.core.logic.delegates.InfoDelegate
import de.robv.android.xposed.installer.core.logic.models.InfoModel
import de.robv.android.xposed.installer.core.logic.mvc.BaseViewMvc
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter.Companion.mSectionSupport

class SupportFragment: Fragment(), InfoDelegate
{
    companion object {
        val TAG: String = SupportFragment::class.java.simpleName
        fun newInstance() = SupportFragment()
    }

    private lateinit var mBaseViewMvc : BaseViewMvc

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val section = mSectionSupport
        val list = BaseSupport().getSupportList(activity!!)
        mBaseViewMvc = BaseViewMvcImp(activity!!, layoutInflater, section, list, this)
        mBaseViewMvc.setDelegate(this)
        return mBaseViewMvc.getRootView()
    }

    override fun onItemClick(infoItem: InfoModel) {
        val pos = infoItem.pos
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