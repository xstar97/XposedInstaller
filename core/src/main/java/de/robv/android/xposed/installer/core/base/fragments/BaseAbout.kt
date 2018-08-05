package de.robv.android.xposed.installer.core.base.fragments

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.licenses.MITLicense
import de.psdev.licensesdialog.licenses.SILOpenFontLicense11
import de.psdev.licensesdialog.model.Notice
import de.psdev.licensesdialog.model.Notices
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.core.util.NavUtil

class BaseAbout
{
    companion object {
        //pos
        const val aboutVersionLabel = 0
        const val aboutDevelopersLabel = 1
        const val aboutLibrariesLabel = 2
        const val aboutTranslatorLabel = 3
        const val aboutSourceLabel = 4

        private fun getVersion(context: Context): String{
            val packageName = context.packageName
            return try {
                context.packageManager.getPackageInfo(packageName, 0).versionName
            } catch (ignored: PackageManager.NameNotFoundException) {
                "-1"
            }
        }
    }
    //actions
    fun showGitHubPage(context: Context){
        NavUtil.startURL(context as Activity, context.getString(R.string.about_source))
    }
    fun showDevelopersDialog(context: Context){
        val dialog = MaterialDialog.Builder(context)
                .title(R.string.about_developers_label)
                .content(R.string.about_developers)
                .positiveText(android.R.string.ok)
                .show()

        (dialog.findViewById(R.id.md_content) as TextView).movementMethod = LinkMovementMethod.getInstance()
    }
    //todo add other used libraries here
    fun getLicenseDialog(context: Context) {
        val notices = Notices()
        notices.addNotice(Notice("material-dialogs", "https://github.com/afollestad/material-dialogs", "Copyright (c) 2014-2016 Aidan Michael Follestad", MITLicense()))
        notices.addNotice(Notice("StickyListHeaders", "https://github.com/emilsjolander/StickyListHeaders", "Emil Sjölander", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("PreferenceFragment-Compat", "https://github.com/Machinarius/PreferenceFragment-Compat", "machinarius", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("libsuperuser", "https://github.com/Chainfire/libsuperuser", "Copyright (C) 2012-2015 Jorrit \"Chainfire\" Jongma", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("picasso", "https://github.com/square/picasso", "Copyright 2013 Square, Inc.", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("materialdesignicons", "http://materialdesignicons.com", "Copyright (c) 2014, Austin Andrews", SILOpenFontLicense11()))

        LicensesDialog.Builder(context)
                .setNotices(notices)
                .setIncludeOwnLicense(true)
                .build()
                .show()
    }

    fun getAboutList(context: Context): ArrayList<InfoModel>{
        val list = ArrayList<InfoModel>()
        list.add(InfoModel(aboutVersionLabel, ContextCompat.getDrawable(context,R.drawable.ic_info)!!, context.getString(R.string.about_version_label), BaseAbout.getVersion(context)))
        list.add(InfoModel(aboutDevelopersLabel, ContextCompat.getDrawable(context,R.drawable.ic_person)!!, context.getString(R.string.about_developers_label), ""))
        list.add(InfoModel(aboutLibrariesLabel, ContextCompat.getDrawable(context,R.drawable.ic_description)!!, context.getString(R.string.about_libraries_label), ""))
        list.add(InfoModel(aboutTranslatorLabel, ContextCompat.getDrawable(context,R.drawable.ic_language)!!, context.getString(R.string.about_translator_label), context.getString(R.string.translator)))
        list.add(InfoModel(aboutSourceLabel, ContextCompat.getDrawable(context,R.drawable.ic_github)!!, context.getString(R.string.about_source_label), ""))
        return list
    }
}