package de.robv.android.xposed.installer.core.logic.mvc

import android.view.View
import de.robv.android.xposed.installer.core.logic.delegates.NavigationDelegate

interface NavigationViewMvc
{
    fun getRootView() : View

    fun setDelegate(delegate: NavigationDelegate)
}