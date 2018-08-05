package de.robv.android.xposed.installer.core.base.fragments.download

import android.content.Context
import android.content.SharedPreferences
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.repo.RepoDb
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.RepoLoader

class BaseDownload
{
    companion object {
        var mRepoLoader: RepoLoader? = null
        var mModuleUtil: ModuleUtil? = null
        var mSortingOrder: Int = 0
        var mPref: SharedPreferences? = null
    }
    fun sectionHeadersStatus(res: Context): Array<String>{
        return arrayOf(res.getString(R.string.download_section_framework), res.getString(R.string.download_section_update_available), res.getString(R.string.download_section_installed), res.getString(R.string.download_section_not_installed))
    }
    fun sectionHeadersDate(res: Context): Array<String>{
        return arrayOf(res.getString(R.string.download_section_24h), res.getString(R.string.download_section_7d), res.getString(R.string.download_section_30d), res.getString(R.string.download_section_older))
    }

    fun initBaseDownload(){
        mPref = BaseXposedApp.getPreferences()
        mRepoLoader = RepoLoader.getInstance()
        mModuleUtil = ModuleUtil.getInstance()
        mSortingOrder = mPref!!.getInt("download_sorting_order", RepoDb.SORT_STATUS)
    }
}