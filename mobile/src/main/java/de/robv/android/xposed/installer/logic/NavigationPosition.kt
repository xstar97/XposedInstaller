package de.robv.android.xposed.installer.logic

import android.support.v4.app.Fragment
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.ui.fragments.*
import de.robv.android.xposed.installer.ui.fragments.Download.DownloadFragment

enum class NavigationPosition(val id: Int) {

    DOWNLOAD(R.id.nav_item_downloads),
    MODULES(R.id.nav_item_modules),
    HOME(R.id.nav_item_framework),
    LOGS(R.id.nav_item_logs),
    SETTINGS(R.id.nav_item_settings),
    SUPPORT(R.id.nav_item_support),
    ABOUT(R.id.nav_item_about),
    DEVICEINFO(7);
}
fun findNavPosById(id: Int): NavigationPosition = when (id) {
    NavigationPosition.DOWNLOAD.id -> NavigationPosition.DOWNLOAD
    NavigationPosition.MODULES.id -> NavigationPosition.MODULES
    NavigationPosition.HOME.id -> NavigationPosition.HOME
    NavigationPosition.LOGS.id -> NavigationPosition.LOGS
    NavigationPosition.SETTINGS.id -> NavigationPosition.SETTINGS
    NavigationPosition.SUPPORT.id -> NavigationPosition.SUPPORT
    NavigationPosition.ABOUT.id -> NavigationPosition.ABOUT
    //TODO add error fragment
    else -> NavigationPosition.HOME
}

fun NavigationPosition.createFragment(): Fragment = when (this) {
    NavigationPosition.DOWNLOAD -> DownloadFragment.newInstance()
    NavigationPosition.MODULES -> ModulesFragment.newInstance()
    NavigationPosition.HOME -> StatusInstallerFragment.newInstance()
    NavigationPosition.LOGS -> LogsFragment.newInstance()
    NavigationPosition.SETTINGS -> SettingsFragment.newInstance()
    NavigationPosition.SUPPORT -> SupportFragment.newInstance()
    NavigationPosition.ABOUT -> AboutFragment.newInstance()
    NavigationPosition.DEVICEINFO -> DeviceInfoFragment.newInstance()
}

fun NavigationPosition.getTag(): String = when (this) {
    NavigationPosition.DOWNLOAD -> DownloadFragment.TAG
    NavigationPosition.MODULES -> ModulesFragment.TAG
    NavigationPosition.HOME -> StatusInstallerFragment.TAG
    NavigationPosition.LOGS -> LogsFragment.TAG
    NavigationPosition.SETTINGS -> SettingsFragment.TAG
    NavigationPosition.SUPPORT -> SupportFragment.TAG
    NavigationPosition.ABOUT -> AboutFragment.TAG
    NavigationPosition.DEVICEINFO -> DeviceInfoFragment.TAG
}

fun NavigationPosition.getPos(): Int = when (this) {
    NavigationPosition.DOWNLOAD -> setNavPos(0,2)
    NavigationPosition.MODULES -> 1
    NavigationPosition.HOME -> setNavPos(2,0)
    NavigationPosition.LOGS -> 3
    NavigationPosition.SETTINGS -> 4
    NavigationPosition.SUPPORT -> 5
    NavigationPosition.ABOUT -> 6
    NavigationPosition.DEVICEINFO -> 7
}
private fun setNavPos(bottom: Int, drawer: Int) = if (Utils().isBottomNav()) bottom else drawer
