package de.robv.android.xposed.installer.core.logic.delegates

interface DownloadDelegate
{
    /**
     * Callback function set cursor when selected
     */
    fun onModuleSelected(pkg: String?)

    fun onQueryFilter(data: String?)

    fun onSortingDialogOptionSelected(sort: Int)

}