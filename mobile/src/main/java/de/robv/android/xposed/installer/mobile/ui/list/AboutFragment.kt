package de.robv.android.xposed.installer.mobile.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseAbout
import de.robv.android.xposed.installer.core.logic.delegates.InfoDelegate
import de.robv.android.xposed.installer.core.logic.models.InfoModel
import de.robv.android.xposed.installer.core.logic.mvc.BaseViewMvc
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter.Companion.mSectionAbout

class AboutFragment: Fragment(), InfoDelegate
{
    companion object {
        val TAG: String = AboutFragment::class.java.simpleName
        fun newInstance() = AboutFragment()
    }

    private lateinit var mBaseViewMvc : BaseViewMvc

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val section = mSectionAbout
        val list = BaseAbout().getAboutList(activity!!)
        mBaseViewMvc = BaseViewMvcImp(activity!!, layoutInflater, section, list, this)
        mBaseViewMvc.setDelegate(this)
        return mBaseViewMvc.getRootView()
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