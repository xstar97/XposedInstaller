package de.robv.android.xposed.installer.mobile.ui.fragments.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.core.base.fragments.BaseAbout
import de.robv.android.xposed.installer.core.delegates.InfoDelegate
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter.Companion.mSectionAbout
import de.robv.android.xposed.installer.mobile.ui.anko.BaseViewUI
import org.jetbrains.anko.AnkoContext

class AboutFragment: Fragment(), InfoDelegate
{
    companion object {
        val TAG: String = AboutFragment::class.java.simpleName
        fun newInstance() = AboutFragment()
    }

    private lateinit var baseUI : BaseViewUI<Fragment>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val section = mSectionAbout
        val list = BaseAbout().getAboutList(activity!!)
        baseUI = BaseViewUI(activity!!, this, section, list)
        return this.baseUI.createView(AnkoContext.create(activity!!, this))
    }

    override fun onItemClick(infoItem: InfoModel) {
        val pos = infoItem.pos
        when (pos) {
            BaseAbout.aboutVersionLabel -> {

            }
            BaseAbout.aboutDevelopersLabel -> {
                BaseAbout().showDevelopersDialog(activity!!)
            }
            BaseAbout.aboutLibrariesLabel -> {
                BaseAbout().getLicenseDialog(activity!!)
            }
            BaseAbout.aboutTranslatorLabel -> {

            }
            BaseAbout.aboutSourceLabel -> {
                BaseAbout().showGitHubPage(activity!!)
            }
        }
    }
}