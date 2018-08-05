package de.robv.android.xposed.installer.core.models

interface DownloadDelegate
{
    fun onItemClick(infoItem: DownloadModel)
}