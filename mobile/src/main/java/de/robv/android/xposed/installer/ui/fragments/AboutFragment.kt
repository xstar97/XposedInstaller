package de.robv.android.xposed.installer.ui.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.licenses.MITLicense
import de.psdev.licensesdialog.licenses.SILOpenFontLicense11
import de.psdev.licensesdialog.model.Notice
import de.psdev.licensesdialog.model.Notices
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.util.NavUtil
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoBaseAdapter
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoModel
import kotlinx.android.synthetic.main.fragment_view.*

class AboutFragment: BaseViewFragment()
{
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
                val dialog = MaterialDialog.Builder(activity!!)
                        .title(R.string.about_developers_label)
                        .content(R.string.about_developers)
                        .positiveText(android.R.string.ok)
                        .show()

                (dialog.findViewById(R.id.md_content) as TextView).movementMethod = LinkMovementMethod.getInstance()
            }
            getString(R.string.about_libraries_label) -> {
                getLicenseDialog()
            }
            getString(R.string.about_translator_label) -> {

            }
            getString(R.string.about_source_label) -> {
                NavUtil.startURL(activity, getString(R.string.about_source))
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

    private fun initViews(){
        fragment_view_recyclerView.adapter = aboutAdapter
        fragment_view_recyclerView.layoutManager = LinearLayoutManager(activity)
        if (aboutList.isEmpty())
        aboutInfo()
    }

    private fun aboutInfo(){
        aboutList.add(TabInfoModel(R.drawable.ic_info, getString(R.string.about_version_label), getVersion()))
        aboutList.add(TabInfoModel(R.drawable.ic_person, getString(R.string.about_developers_label), ""))
        aboutList.add(TabInfoModel(R.drawable.ic_description, getString(R.string.about_libraries_label), ""))
        aboutList.add(TabInfoModel(R.drawable.ic_language, getString(R.string.about_translator_label), getString(R.string.translator)))
        aboutList.add(TabInfoModel(R.drawable.ic_github, getString(R.string.about_source_label), ""))
        aboutAdapter.addItems(aboutAdapter.SECTION_ABOUT, aboutList)
    }
    private fun getVersion(): String{
        val packageName = activity!!.packageName

        return try {
            activity!!.packageManager.getPackageInfo(packageName, 0).versionName
        } catch (ignored: PackageManager.NameNotFoundException) {
            "-1"
        }
    }
    private fun getLicenseDialog() {
        val notices = Notices()
        notices.addNotice(Notice("material-dialogs", "https://github.com/afollestad/material-dialogs", "Copyright (c) 2014-2016 Aidan Michael Follestad", MITLicense()))
        notices.addNotice(Notice("StickyListHeaders", "https://github.com/emilsjolander/StickyListHeaders", "Emil Sjölander", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("PreferenceFragment-Compat", "https://github.com/Machinarius/PreferenceFragment-Compat", "machinarius", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("libsuperuser", "https://github.com/Chainfire/libsuperuser", "Copyright (C) 2012-2015 Jorrit \"Chainfire\" Jongma", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("picasso", "https://github.com/square/picasso", "Copyright 2013 Square, Inc.", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("materialdesignicons", "http://materialdesignicons.com", "Copyright (c) 2014, Austin Andrews", SILOpenFontLicense11()))

        LicensesDialog.Builder(activity!!)
                .setNotices(notices)
                .setIncludeOwnLicense(true)
                .build()
                .show()
    }
}