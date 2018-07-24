package de.robv.android.xposed.installer.mobile.ui.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.BaseAbout
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.TabInfoBaseAdapter
import de.robv.android.xposed.installer.mobile.ui.fragments.base.BaseViewFragment
import kotlinx.android.synthetic.main.fragment_view.*

class AboutFragment: BaseViewFragment() {

    companion object {
        val TAG: String = AboutFragment::class.java.simpleName
        fun newInstance() = AboutFragment()
    }
    private val aboutVersionLabel = 0
    private val aboutDevelopersLabel = 1
    private val aboutLibrariesLabel = 2
    private val aboutTranslatorLabel = 3
    private val aboutSourceLabel = 4

    override fun onItemClick(infoItem: InfoModel) {
        val pos = infoItem.pos
        when (pos) {
            aboutVersionLabel -> {

            }
            aboutDevelopersLabel -> {
                BaseAbout.showDevelopersDialog(activity!!)
            }
            aboutLibrariesLabel -> {
                BaseAbout.getLicenseDialog(activity!!)
            }
            aboutTranslatorLabel -> {

            }
            aboutSourceLabel -> {
                BaseAbout.showGitHubPage(activity!!)
            }
        }
    }

    private val aboutAdapter by lazy { TabInfoBaseAdapter(activity!!, this) }
    private val aboutList = ArrayList<InfoModel>()

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
         aboutList.add(InfoModel(aboutVersionLabel,R.drawable.ic_info, getString(R.string.about_version_label), BaseAbout.getVersion(activity!!)))
         aboutList.add(InfoModel(aboutDevelopersLabel, R.drawable.ic_person, getString(R.string.about_developers_label), ""))
         aboutList.add(InfoModel(aboutLibrariesLabel,R.drawable.ic_description, getString(R.string.about_libraries_label), ""))
         aboutList.add(InfoModel(aboutTranslatorLabel, R.drawable.ic_language, getString(R.string.about_translator_label), getString(R.string.translator)))
         aboutList.add(InfoModel(aboutSourceLabel, R.drawable.ic_github, getString(R.string.about_source_label), ""))
         aboutAdapter.addItems(aboutAdapter.SECTION_ABOUT, aboutList)
    }
}