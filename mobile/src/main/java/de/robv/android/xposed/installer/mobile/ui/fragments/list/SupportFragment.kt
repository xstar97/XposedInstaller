package de.robv.android.xposed.installer.mobile.ui.fragments.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.core.base.fragments.BaseSupport
import de.robv.android.xposed.installer.core.delegates.InfoDelegate
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter.Companion.mSectionSupport
import de.robv.android.xposed.installer.mobile.ui.anko.BaseViewUI
import org.jetbrains.anko.AnkoContext

class SupportFragment: Fragment(), InfoDelegate
{
    companion object {
        val TAG: String = SupportFragment::class.java.simpleName
        fun newInstance() = SupportFragment()
    }

    private lateinit var baseUI : BaseViewUI<Fragment>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val section = mSectionSupport
        val list = BaseSupport().getSupportList(activity!!)
        baseUI = BaseViewUI(activity!!, this, section, list)
        return this.baseUI.createView(AnkoContext.create(activity!!, this))
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