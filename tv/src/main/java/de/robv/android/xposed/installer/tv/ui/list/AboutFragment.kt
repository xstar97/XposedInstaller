package de.robv.android.xposed.installer.tv.ui.list

import android.os.Bundle
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseAbout

import de.robv.android.xposed.installer.tv.ui.base.BaseGuidedFragment

class AboutFragment: BaseGuidedFragment()
{
    companion object {
        val TAG: String = AboutFragment::class.java.simpleName
        fun newInstance() = AboutFragment()
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.nav_item_about),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        try {
            val list = getActionsFromInfoList(activity!!, BaseAbout().getAboutList(activity!!))
            for (about in list) {
                actions.add(about)
            }
        }catch (npe: NullPointerException){
            error {"npe: ${npe.message}"}
        }catch (e: Exception){
            error {"e: ${e.message}"}
        }
}

    override fun onGuidedActionClicked(action: GuidedAction?) {

        val pos = action!!.id.toInt()
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