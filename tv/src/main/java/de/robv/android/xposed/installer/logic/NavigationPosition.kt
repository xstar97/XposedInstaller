package de.robv.android.xposed.installer.logic

import android.support.v4.app.Fragment
import de.robv.android.xposed.installer.ui.fragments.*

enum class NavigationPosition(val pos: Int) {
    HOME(0),
    MODULES(1),
    DOWNLOAD(2),
    LOGS(3),
    SUPPORT(4),
    ABOUT(5),
    SETTINGS(6),
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