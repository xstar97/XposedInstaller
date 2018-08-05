package de.robv.android.xposed.installer.core.models

interface NavDelegate
{
    fun onNavSelected(nav: NavModel): Boolean
}