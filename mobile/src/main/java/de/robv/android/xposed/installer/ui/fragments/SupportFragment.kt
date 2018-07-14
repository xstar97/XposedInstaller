package de.robv.android.xposed.installer.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.util.NavUtil
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoBaseAdapter
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoModel
import kotlinx.android.synthetic.main.fragment_view.*

class SupportFragment: BaseViewFragment()
{
    companion object {
        val TAG: String = SupportFragment::class.java.simpleName
        fun newInstance() = SupportFragment()
    }
    override fun onItemClick(infoItem: TabInfoModel) {
        val key = infoItem.key
        when (key) {
            getString(R.string.support_modules_label) -> {

            }
            getString(R.string.support_framework_label) -> {
                setNavUtil(getString(R.string.about_support))
            }
            getString(R.string.support_faq_label) -> {
                setNavUtil(getString(R.string.support_faq_url))
            }
            getString(R.string.support_donate_label) -> {
                setNavUtil(getString(R.string.support_donate_url))

            }
        }
    }
    private val supportAdapter by lazy { TabInfoBaseAdapter(activity!!, this) }
    private val supportList = ArrayList<TabInfoModel>()

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
        supportInfo()
    }

    @SuppressLint("StringFormatMatches")
    private fun supportInfo(){
        supportList.add(TabInfoModel(R.drawable.ic_info, getString(R.string.support_modules_label), getString(R.string.support_modules_description, getString(R.string.module_support))))
        supportList.add(TabInfoModel(R.drawable.ic_help, getString(R.string.support_framework_label), ""))
        supportList.add(TabInfoModel(R.drawable.ic_nav_logs, getString(R.string.support_faq_label), ""))
        supportList.add(TabInfoModel(R.drawable.ic_donate, getString(R.string.support_donate_label), getString(R.string.support_donate_description)))
        supportAdapter.addItems(supportAdapter.SECTION_SUPPORT, supportList)
    }
    private fun setNavUtil(string: String){
        NavUtil.startURL(activity, string)
    }
}