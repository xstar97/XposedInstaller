package de.robv.android.xposed.installer.core.logic.mvc

import android.view.View

interface DownloadViewMvc
{
    fun getRootView() : View

    interface DownloadDelegate
    {
        /**
         * Callback function set cursor when selected
         */
        fun onModuleSelected(pkg: String?)

        fun onQueryFilter(data: String?)

        fun onSortingDialogOptionSelected(sort: Int)

    }
    fun setDelegate(delegate: DownloadDelegate)
}