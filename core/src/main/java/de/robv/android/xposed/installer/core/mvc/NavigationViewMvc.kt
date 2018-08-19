package de.robv.android.xposed.installer.core.mvc

import android.view.View

interface NavigationViewMvc: StateViewMvc.StateDelegate
{
    fun getRootView() : View

    interface NavigationDelegate
    {
        fun onNavSelected(nav: Any)
        fun onNavReSelected(nav: Any)
    }
    fun setDelegate(delegate: NavigationDelegate)
}