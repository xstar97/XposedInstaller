package de.robv.android.xposed.installer.core.logic.base.fragments.download

import android.content.Context
import de.robv.android.xposed.installer.core.R

class BaseDownload
{
    companion object {
       // var mModuleUtil: ModuleUtil? = null
        var mSortingOrder: Int = 0
        //var mPref: SharedPreferences? = null
        const val prefSortStatus = 0
        const val prefSortUpdate = 1
        const val prefSortCreate = 2

        fun downloadSortOrder(context: Context): Array<String>{
            return context.resources.getStringArray(R.array.download_sort_order)
        }
    }
    //var mRepoLoader: RepoLoader? = null

    fun sectionHeadersStatus(context: Context): Array<String>{
        return arrayOf(context.getString(R.string.download_section_framework), context.getString(R.string.download_section_update_available), context.getString(R.string.download_section_installed), context.getString(R.string.download_section_not_installed))
    }
    fun sectionHeadersDate(context: Context): Array<String>{
        return arrayOf(context.getString(R.string.download_section_24h), context.getString(R.string.download_section_7d), context.getString(R.string.download_section_30d), context.getString(R.string.download_section_older))
    }
    fun downloadSortOrder(context: Context): Array<String>{
        return context.resources.getStringArray(R.array.download_sort_order)
    }
}