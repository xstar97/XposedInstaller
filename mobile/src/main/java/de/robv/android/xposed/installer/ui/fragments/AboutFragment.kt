package de.robv.android.xposed.installer.ui.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.BaseAbout
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoBaseAdapter
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoModel
import kotlinx.android.synthetic.main.fragment_view.*

class AboutFragment: BaseViewFragment() {
    companion object {
        val TAG: String = AboutFragment::class.java.simpleName
        fun newInstance() = AboutFragment()
    }

    override fun onItemClick(infoItem: TabInfoModel) {
        val key = infoItem.key
        when (key) {
            getString(R.string.about_version_label) -> {

            }
            getString(R.string.about_developers_label) -> {
                BaseAbout.showDevelopersDialog(activity!!)
            }
            getString(R.string.about_libraries_label) -> {
                BaseAbout.getLicenseDialog(activity!!)
            }
            getString(R.string.about_translator_label) -> {

            }
            getString(R.string.about_source_label) -> {
                BaseAbout.showGitHubPage(activity!!)
            }
        }
    }

    private val aboutAdapter by lazy { TabInfoBaseAdapter(activity!!, this) }
    private val aboutList = ArrayList<TabInfoModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }
    private fun initViews() {
        fragment_view_recyclerView.adapter = aboutAdapter
        fragment_view_recyclerView.layoutManager = LinearLayoutManager(activity)
        if (aboutList.isEmpty())
            populateList()
    }

     private fun populateList() {
         aboutList.add(TabInfoModel(R.drawable.ic_info, getString(R.string.about_version_label), BaseAbout.getVersion(activity!!)))
         aboutList.add(TabInfoModel(R.drawable.ic_person, getString(R.string.about_developers_label), ""))
         aboutList.add(TabInfoModel(R.drawable.ic_description, getString(R.string.about_libraries_label), ""))
         aboutList.add(TabInfoModel(R.drawable.ic_language, getString(R.string.about_translator_label), getString(R.string.translator)))
         aboutList.add(TabInfoModel(R.drawable.ic_github, getString(R.string.about_source_label), ""))
         aboutAdapter.addItems(aboutAdapter.SECTION_ABOUT, aboutList)
    }
}