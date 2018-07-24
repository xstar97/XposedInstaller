package de.robv.android.xposed.installer.mobile.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.BaseSupport
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.TabInfoBaseAdapter
import de.robv.android.xposed.installer.mobile.ui.fragments.base.BaseViewFragment
import kotlinx.android.synthetic.main.fragment_view.*

class SupportFragment: BaseViewFragment()
{
    companion object {
        val TAG: String = SupportFragment::class.java.simpleName
        fun newInstance() = SupportFragment()
    }
    val supportModulesLabel = 0
    val supportFrameworkLabel = 1
    val supportFaqLabel = 2
    val supportDonateLabel = 3

    override fun onItemClick(infoItem: InfoModel) {
        val pos = infoItem.pos
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
    private val supportAdapter by lazy { TabInfoBaseAdapter(activity!!, this) }
    private val supportList = ArrayList<InfoModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews(){
        fragment_view_recyclerView.adapter = supportAdapter
        fragment_view_recyclerView.layoutManager = LinearLayoutManager(activity)
        if (supportList.isEmpty())
            populateList()
    }

    @SuppressLint("StringFormatMatches")
    private fun populateList(){
        supportList.add(InfoModel(supportModulesLabel, R.drawable.ic_info, getString(R.string.support_modules_label), getString(R.string.support_modules_description, getString(R.string.module_support))))
        supportList.add(InfoModel(supportFrameworkLabel, R.drawable.ic_help, getString(R.string.support_framework_label), ""))
        supportList.add(InfoModel(supportFaqLabel, R.drawable.ic_nav_logs, getString(R.string.support_faq_label), ""))
        supportList.add(InfoModel(supportDonateLabel, R.drawable.ic_donate, getString(R.string.support_donate_label), getString(R.string.support_donate_description)))
        supportAdapter.addItems(supportAdapter.SECTION_SUPPORT, supportList)
    }
}