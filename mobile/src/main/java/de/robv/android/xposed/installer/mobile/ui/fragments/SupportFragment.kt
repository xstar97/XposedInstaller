package de.robv.android.xposed.installer.mobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.core.base.fragments.BaseSupport
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter.Companion.SECTION_SUPPORT
import de.robv.android.xposed.installer.mobile.ui.fragments.base.BaseViewFragment

class SupportFragment: BaseViewFragment()
{
    companion object {
        val TAG: String = SupportFragment::class.java.simpleName
        fun newInstance() = SupportFragment()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = BaseSupport().getSupportList(activity!!)
        initList(SECTION_SUPPORT, list)
    }
}