package de.robv.android.xposed.installer.tv.logic

import android.content.Context
import android.support.v4.app.Fragment
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.tv.ui.fragments.*

enum class NavigationPosition(val pos: Int) {
    HOME(0),
    MODULES(1),
    DOWNLOAD(2),
    LOGS(3),
    SUPPORT(4),
    ABOUT(5),
    SETTINGS(6);
}
fun NavigationPosition.createFragment(): Fragment = when (this) {
    //NavigationPosition.HOME -> StatusInstallerFragment.newInstance()
    //NavigationPosition.MODULES -> ModulesFragment.newInstance()
    //NavigationPosition.DOWNLOAD -> DownloadFragment.newInstance()
    //NavigationPosition.LOGS -> LogsFragment.newInstance()
    NavigationPosition.SUPPORT -> SupportFragment.newInstance()
    NavigationPosition.ABOUT -> AboutFragment.newInstance()
    NavigationPosition.SETTINGS -> SettingsFragment.newInstance()
    else -> ErrorFragment.newInstance()
}

fun NavigationPosition.getTag(): String = when (this) {
    //NavigationPosition.HOME -> StatusInstallerFragment.TAG
    //NavigationPosition.MODULES -> ModulesFragment.TAG
    //NavigationPosition.DOWNLOAD -> DownloadFragment.TAG
    //NavigationPosition.LOGS -> LogsFragment.TAG
    NavigationPosition.SUPPORT -> SupportFragment.TAG
    NavigationPosition.ABOUT -> AboutFragment.TAG
    NavigationPosition.SETTINGS -> SettingsFragment.TAG
    else -> ErrorFragment.TAG
}

fun NavigationPosition.getTitle(context: Context): String = when (this) {
    NavigationPosition.HOME -> context.getString(R.string.nav_item_install)
    NavigationPosition.MODULES -> context.getString(R.string.nav_item_modules)
    NavigationPosition.DOWNLOAD -> context.getString(R.string.nav_item_download)
    NavigationPosition.LOGS -> context.getString(R.string.nav_item_logs)
    NavigationPosition.SUPPORT -> context.getString(R.string.nav_item_support)
    NavigationPosition.ABOUT -> context.getString(R.string.nav_item_about)
    NavigationPosition.SETTINGS -> context.getString(R.string.nav_item_settings)
    //else -> context.getString(R.string.error_fragment)
}