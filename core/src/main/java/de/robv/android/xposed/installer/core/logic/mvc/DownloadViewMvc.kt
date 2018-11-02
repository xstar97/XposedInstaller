package de.robv.android.xposed.installer.core.logic.mvc

import android.view.View
import de.robv.android.xposed.installer.core.logic.delegates.DownloadDelegate

interface DownloadViewMvc
{
    fun getRootView() : View

    fun setDelegate(delegate: DownloadDelegate)
}