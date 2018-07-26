package de.robv.android.xposed.installer.mobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.BaseAbout
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter.Companion.SECTION_ABOUT
import de.robv.android.xposed.installer.mobile.ui.fragments.base.BaseViewFragment

class AboutFragment: BaseViewFragment() {

    companion object {
        val TAG: String = AboutFragment::class.java.simpleName
        fun newInstance() = AboutFragment()
    }

    override fun onItemClick(infoItem: InfoModel) {
        val pos = infoItem.pos
        when (pos) {
            BaseAbout.aboutVersionLabel -> {

            }
            BaseAbout.aboutDevelopersLabel -> {
                BaseAbout.showDevelopersDialog(activity!!)
            }
            BaseAbout.aboutLibrariesLabel -> {
                BaseAbout.getLicenseDialog(activity!!)
            }
            BaseAbout.aboutTranslatorLabel -> {

            }
            BaseAbout.aboutSourceLabel -> {
                BaseAbout.showGitHubPage(activity!!)
            }
        }
    }

    private val adapter by lazy { InfoBaseAdapter(activity!!, this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = BaseAbout.getAboutList(activity!!)
        initViews(adapter, SECTION_ABOUT, list)
    }
}