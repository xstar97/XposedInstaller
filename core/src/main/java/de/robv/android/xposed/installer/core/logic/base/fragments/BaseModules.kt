package de.robv.android.xposed.installer.core.logic.base.fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.widget.Toast
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.logic.base.BaseXposedApp
import de.robv.android.xposed.installer.core.logic.base.fragments.download.BaseDownloadDetailsVersions
import de.robv.android.xposed.installer.core.logic.base.fragments.utils.ModulesUtil
import de.robv.android.xposed.installer.core.repo.Module
import de.robv.android.xposed.installer.core.repo.ModuleVersion
import de.robv.android.xposed.installer.core.repo.ReleaseType
import de.robv.android.xposed.installer.core.util.DownloadsUtil
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.RepoLoader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.ArrayList

open class BaseModules
{
    companion object {
        const val PLAY_STORE_PACKAGE = "com.android.vending"
        const val PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=%s"
        var PLAY_STORE_LABEL: String? = null

        const val XPOSED_REPO_LINK = "http://repo.xposed.info/module/%s"
        const val NOT_ACTIVE_NOTE_TAG = "NOT_ACTIVE_NOTE"
        var installedXposedVersion: Int = 0
        var mModuleUtil: ModuleUtil? = null
        var mPm: PackageManager? = null

        fun getSettingsIntent(context: Context, packageName: String): Intent? {
            // taken from
            // ApplicationPackageManager.getLaunchIntentForPackage(String)
            // first looks for an Xposed-specific category, falls back to
            // getLaunchIntentForPackage
            val pm = context.packageManager

            val intentToResolve = Intent(Intent.ACTION_MAIN)
            intentToResolve.addCategory(ModuleUtil.SETTINGS_CATEGORY)
            intentToResolve.setPackage(packageName)
            val ris = pm.queryIntentActivities(intentToResolve, 0)

            if (ris == null || ris.size <= 0) {
                return pm.getLaunchIntentForPackage(packageName)
            }

            val intent = Intent(intentToResolve)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setClassName(ris[0].activityInfo.packageName, ris[0].activityInfo.name)
            return intent
        }
    }
    open fun initModuleUtil(context: Context){
        mModuleUtil = ModuleUtil.getInstance()
        mPm = context.packageManager
        if (PLAY_STORE_LABEL == null) {
            try {
                val ai = mPm!!.getApplicationInfo(PLAY_STORE_PACKAGE,
                        0)
                PLAY_STORE_LABEL = mPm!!.getApplicationLabel(ai).toString()
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
        }
    }

    open fun isModuleEnabled(packageName: String, isChecked: Boolean){
        val changed = mModuleUtil!!.isModuleEnabled(packageName) xor isChecked
        if (changed) {
            mModuleUtil!!.setModuleEnabled(packageName, isChecked)
            mModuleUtil!!.updateModulesList(true)
        }
    }

    open fun importModules(context: Context, path: File): Boolean {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(context, R.string.sdcard_not_writable, Toast.LENGTH_LONG).show()
            return false
        }
        var ips: InputStream? = null
        val repoLoader = RepoLoader.getInstance()
        val list = ArrayList<Module>()
        if (!path.exists()) {
            Toast.makeText(context, context.getString(R.string.no_backup_found),
                    Toast.LENGTH_LONG).show()
            return false
        }
        try {
            @Suppress("UNUSED_VALUE")
            ips = FileInputStream(path)
        } catch (e: FileNotFoundException) {
            Log.e(BaseXposedApp.TAG, "Could not open $path", e)
        }

        if (path.length() == 0L) {
            Toast.makeText(context, R.string.file_is_empty, Toast.LENGTH_LONG).show()
            return false
        }

        ModulesUtil().onImportModulesUtil(context, ips, repoLoader, list)

        for (m in list) {
            var mv: ModuleVersion? = null
            for (i in m.versions.indices) {
                val mvTemp = m.versions[i]

                if (mvTemp.relType == ReleaseType.STABLE) {
                    mv = mvTemp
                    break
                }
            }

            if (mv != null) {
                DownloadsUtil.addModule(context, m.name, mv.downloadLink, BaseDownloadDetailsVersions.DownloadModuleCallback(mv))
            }
        }

        return true
    }

    open fun launchModule(context: Context, packageName: String){
        val launchIntent = getSettingsIntent(context,packageName)
        if (launchIntent != null)
            context.startActivity(launchIntent)
        else
            Toast.makeText(context,
                    context.getString(R.string.module_no_ui),
                    Toast.LENGTH_LONG).show()
    }

    open fun getModuleWarnDescription(context: Context, minVersion: Int, isInstalledOnExternalStorage: Boolean): Pair<String, Boolean>{
        var checkBox = false
        return when{
            minVersion == 0 ->{
                val description = context.getString(R.string.no_min_version_specified)
                Pair(description, checkBox)
            }
            BaseModules.installedXposedVersion != 0 && minVersion > BaseModules.installedXposedVersion ->{
                val description = String.format(context.getString(R.string.warning_xposed_min_version), minVersion)
                Pair(description, checkBox)
            }
            minVersion < ModuleUtil.MIN_MODULE_VERSION ->{
                val description = String.format(context.getString(R.string.warning_min_version_too_low), minVersion, ModuleUtil.MIN_MODULE_VERSION)
                Pair(description, checkBox)
            }
            isInstalledOnExternalStorage ->{
                val description = context.getString(R.string.warning_installed_on_external_storage)
                Pair(description, checkBox)
            }
            BaseModules.installedXposedVersion == 0 ->{
                val description = context.getString(R.string.framework_not_installed)
                Pair(description, checkBox)
            }
            else ->{
                val description = ""
                checkBox = true
                Pair(description, checkBox)
            }
        }
    }
}