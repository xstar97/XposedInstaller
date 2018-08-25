package de.robv.android.xposed.installer.core.logic.delegates

import de.robv.android.xposed.installer.core.logic.models.ZipModel

interface FrameworkDelegate
{
    /**
     * Callback function set a zip when selected
     */
    fun onFrameworkSelected(zip: ZipModel)

    /**
     * Callback function set uri when selected
     */
    fun onKnownIssueSelected(uri: String?)

    /**
     * Callback function to reboot device
     */
    fun onReboot(id: Int)

    /**
     * Callback function to show optimized apps
     */
    fun onOptimizedAppDialog()
}