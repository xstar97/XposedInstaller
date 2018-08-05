package de.robv.android.xposed.installer.tv.logic

import android.support.v4.app.Fragment
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.tv.ui.fragments.*
import de.robv.android.xposed.installer.tv.ui.fragments.download.DownloadFragment

enum class NavigationPosition(val pos: Int, val icon: Int, val title: Int) {
    ERROR(-1, R.drawable.lb_ic_sad_cloud, R.string.error_fragment_title),
    HOME(0, R.drawable.ic_nav_install, R.string.nav_item_install),
    MODULES(1, R.drawable.ic_nav_modules, R.string.nav_item_modules),
    DOWNLOAD(2, R.drawable.ic_nav_downloads, R.string.nav_item_download),
    LOGS(3, R.drawable.ic_nav_logs, R.string.nav_item_logs),
    SUPPORT(4, R.drawable.ic_nav_support, R.string.nav_item_support),
    ABOUT(5, R.drawable.ic_nav_about, R.string.nav_item_about),
    SETTINGS(6, R.drawable.ic_nav_settings, R.string.nav_item_settings);
}

fun findNavByPos(pos: Int): NavigationPosition = when (pos) {
    NavigationPosition.DOWNLOAD.pos -> NavigationPosition.DOWNLOAD
    NavigationPosition.MODULES.pos -> NavigationPosition.MODULES
    NavigationPosition.HOME.pos -> NavigationPosition.HOME
    NavigationPosition.LOGS.pos -> NavigationPosition.LOGS
    NavigationPosition.SETTINGS.pos -> NavigationPosition.SETTINGS
    NavigationPosition.SUPPORT.pos -> NavigationPosition.SUPPORT
    NavigationPosition.ABOUT.pos -> NavigationPosition.ABOUT
//TODO add error fragment
    else -> NavigationPosition.ERROR
}
fun NavigationPosition.createFragment(): Fragment = when (this) {
    NavigationPosition.HOME -> StatusInstallerFragment.newInstance()
    NavigationPosition.MODULES -> ModulesFragment.newInstance()
    NavigationPosition.DOWNLOAD -> DownloadFragment.newInstance()
    //NavigationPosition.LOGS -> LogsFragment.newInstance()
    NavigationPosition.SUPPORT -> SupportFragment.newInstance()
    NavigationPosition.ABOUT -> AboutFragment.newInstance()
    NavigationPosition.SETTINGS -> SettingsFragment.newInstance()
    else -> ErrorFragment.newInstance()
}

fun NavigationPosition.getTag(): String = when (this) {
    NavigationPosition.HOME -> StatusInstallerFragment.TAG
    NavigationPosition.MODULES -> ModulesFragment.TAG
    NavigationPosition.DOWNLOAD -> DownloadFragment.TAG
    //NavigationPosition.LOGS -> LogsFragment.TAG
    NavigationPosition.SUPPORT -> SupportFragment.TAG
    NavigationPosition.ABOUT -> AboutFragment.TAG
    NavigationPosition.SETTINGS -> SettingsFragment.TAG
    else -> ErrorFragment.TAG
}