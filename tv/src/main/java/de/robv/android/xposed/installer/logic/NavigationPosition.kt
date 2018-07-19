package de.robv.android.xposed.installer.logic

import android.support.v17.preference.LeanbackPreferenceFragment
import android.support.v4.app.Fragment
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.ui.fragments.*

enum class NavigationPosition(val id: Int) {
    //HOME(R.id.nav_item_framework),
    //MODULES(R.id.nav_item_modules),
    //DOWNLOAD(R.id.nav_item_downloads),
    //LOGS(R.id.nav_item_logs),
    //SUPPORT(R.id.nav_item_support),
    //ABOUT(R.id.nav_item_about),
    SETTINGS(R.id.nav_item_settings),
    ERROR(-1);
}

fun NavigationPosition.createFragment(): Fragment = when (this) {
    //NavigationPosition.HOME -> StatusInstallerFragment.newInstance()
    //NavigationPosition.MODULES -> ModulesFragment.newInstance()
    //NavigationPosition.DOWNLOAD -> DownloadFragment.newInstance()
    //NavigationPosition.LOGS -> LogsFragment.newInstance()
    //NavigationPosition.SUPPORT -> SupportFragment.newInstance()
    //NavigationPosition.ABOUT -> AboutFragment.newInstance()
    NavigationPosition.SETTINGS -> SettingsFragment.newInstance()
    else -> ErrorFragment.newInstance()
}

fun NavigationPosition.getTag(): String = when (this) {
    //NavigationPosition.HOME -> StatusInstallerFragment.TAG
    //NavigationPosition.MODULES -> ModulesFragment.TAG
    //NavigationPosition.DOWNLOAD -> DownloadFragment.TAG
    //NavigationPosition.LOGS -> LogsFragment.TAG
    //NavigationPosition.SUPPORT -> SupportFragment.TAG
    //NavigationPosition.ABOUT -> AboutFragment.TAG
    NavigationPosition.SETTINGS -> SettingsFragment.TAG
    else -> ErrorFragment.TAG
}

fun NavigationPosition.getPos(): Int = when (this) {
    //NavigationPosition.HOME -> 0
    //NavigationPosition.MODULES -> 1
    //NavigationPosition.DOWNLOAD -> 2
    //NavigationPosition.LOGS -> 3
    //NavigationPosition.SUPPORT -> 4
    //NavigationPosition.ABOUT -> 5
    NavigationPosition.SETTINGS -> 6
    else -> -1
}