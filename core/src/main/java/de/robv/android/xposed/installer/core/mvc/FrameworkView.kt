package de.robv.android.xposed.installer.core.mvc

import android.view.View
import de.robv.android.xposed.installer.core.models.ZipModel

interface FrameworkView
{
    fun getRootView() : View

    interface FrameworkDelegate{
        /**
         * Callback function set a zip when selected
         */
        fun onFrameworkSelected(zip: ZipModel)

        /**
         * Callback function set uri when selected
         */
        fun onKnownIssueSelected(uri: String?)
    }
    fun setDelegate(listener: FrameworkDelegate)
}