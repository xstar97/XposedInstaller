package de.robv.android.xposed.installer.tv.ui.fragments

import android.os.Bundle
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.BaseAbout
import de.robv.android.xposed.installer.core.models.InfoModel

import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseGuidedFragment
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error

class AboutFragment : BaseGuidedFragment(), AnkoLogger
{
    companion object {
        val TAG: String = AboutFragment::class.java.simpleName
        fun newInstance() = AboutFragment()
    }

    private val aboutVersionLabel = 0
    private val aboutDevelopersLabel = 1
    private val aboutLibrariesLabel = 2
    private val aboutTranslatorLabel = 3
    private val aboutSourceLabel = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.nav_item_about),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        try {
            val list = getActionsFromList(aboutList())
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

    private fun aboutList(): ArrayList<InfoModel>{
        val infoList = ArrayList<InfoModel>()
        infoList.add(InfoModel(aboutVersionLabel, R.drawable.ic_info, this.activity!!.getString(R.string.about_version_label), BaseAbout.getVersion(activity!!)))
        infoList.add(InfoModel(aboutDevelopersLabel, R.drawable.ic_person, this.activity!!.getString(R.string.about_developers_label), ""))
        infoList.add(InfoModel(aboutLibrariesLabel, R.drawable.ic_description, this.activity!!.getString(R.string.about_libraries_label), ""))
        infoList.add(InfoModel(aboutTranslatorLabel, R.drawable.ic_language, this.activity!!.getString(R.string.about_translator_label), this.activity!!.getString(R.string.translator)))
        infoList.add(InfoModel(aboutSourceLabel, R.drawable.ic_github, this.activity!!.getString(R.string.about_source_label), ""))
        return infoList
    }

}