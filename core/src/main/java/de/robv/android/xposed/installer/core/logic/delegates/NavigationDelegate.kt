package de.robv.android.xposed.installer.core.logic.delegates

interface NavigationDelegate
{
    fun onNavSelected(nav: Any)
    fun onNavReSelected(nav: Any)
}