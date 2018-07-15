package de.robv.android.xposed.installer.logic

import android.support.v4.app.Fragment
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.ui.fragments.*

// positions in menu(bottom nav and drawer nav)
var NAV_DOWNLOAD = if (Utils().getNav() == Utils().NAV_BOTTOM) 0 else 2
var NAV_MODULES = 1
var NAV_HOME = if (Utils().getNav() == Utils().NAV_BOTTOM) 2 else 0
var NAV_LOGS = 3
var NAV_SETTINGS = 4
var NAV_SUPPORT = 5
var NAV_ABOUT = 6

enum class NavigationPosition(val position: Int, val id: Int) {

    DOWNLOAD(NAV_DOWNLOAD, R.id.nav_item_downloads),
    MODULES(NAV_MODULES, R.id.nav_item_modules),
    HOME(NAV_HOME, R.id.nav_item_framework),
    LOGS(NAV_LOGS, R.id.nav_item_logs),
    SETTINGS(NAV_SETTINGS, R.id.nav_item_settings),
    SUPPORT(NAV_SUPPORT, R.id.nav_item_support),
    ABOUT(NAV_ABOUT, R.id.nav_item_about);
}


fun findNavPosById(id: Int): NavigationPosition = when (id) {
    NavigationPosition.DOWNLOAD.id -> NavigationPosition.DOWNLOAD
    NavigationPosition.MODULES.id -> NavigationPosition.MODULES
    NavigationPosition.HOME.id -> NavigationPosition.HOME
    NavigationPosition.LOGS.id -> NavigationPosition.LOGS
    NavigationPosition.SETTINGS.id -> NavigationPosition.SETTINGS
    NavigationPosition.SUPPORT.id -> NavigationPosition.SUPPORT
    NavigationPosition.ABOUT.id -> NavigationPosition.ABOUT
    //TODO add error fragment view
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
}

fun NavigationPosition.getTag(): String = when (this) {
    NavigationPosition.DOWNLOAD -> DownloadFragment.TAG
    NavigationPosition.MODULES -> ModulesFragment.TAG
    NavigationPosition.HOME -> StatusInstallerFragment.TAG
    NavigationPosition.LOGS -> LogsFragment.TAG
    NavigationPosition.SETTINGS -> SettingsFragment.TAG
    NavigationPosition.SUPPORT -> SupportFragment.TAG
    NavigationPosition.ABOUT -> AboutFragment.TAG
}