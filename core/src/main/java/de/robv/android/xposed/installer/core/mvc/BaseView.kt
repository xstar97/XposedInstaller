package de.robv.android.xposed.installer.core.mvc

import android.view.View
import de.robv.android.xposed.installer.core.models.InfoDelegate

interface BaseView
{
    fun getRootView() : View

    fun setDelegate(delegate: InfoDelegate)
}