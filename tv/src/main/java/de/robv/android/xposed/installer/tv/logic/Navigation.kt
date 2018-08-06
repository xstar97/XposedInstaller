package de.robv.android.xposed.installer.tv.logic

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.tv.ui.fragments.*
import de.robv.android.xposed.installer.tv.ui.fragments.browse.StatusInstallerBrowseFragment
import de.robv.android.xposed.installer.tv.ui.fragments.download.DownloadFragment
import de.robv.android.xposed.installer.tv.ui.fragments.download.DownloadSettingsFragment

enum class Navigation(val pos: Int, @DrawableRes val icon: Int, @StringRes val title: Int) {
    NAV_HOME(0, R.drawable.ic_nav_install, R.string.nav_item_install),
    NAV_MODULES(1, R.drawable.ic_nav_modules, R.string.nav_item_modules),
    NAV_DOWNLOAD(2, R.drawable.ic_nav_downloads, R.string.nav_item_download),
    NAV_LOGS(3, R.drawable.ic_nav_logs, R.string.nav_item_logs),
    NAV_SUPPORT(4, R.drawable.ic_nav_support, R.string.nav_item_support),
    NAV_ABOUT(5, R.drawable.ic_nav_about, R.string.nav_item_about),
    NAV_SETTINGS(6, R.drawable.ic_nav_settings, R.string.nav_item_settings),
    FRAG_DEVICE(7, R.drawable.ic_nav_about, R.string.framework_device_info),
    FRAG_ERROR(8, R.drawable.lb_ic_sad_cloud, R.string.error_fragment_title),
    FRAG_DOWNLOAD_SETTINGS(9, R.drawable.ic_nav_settings, R.string.nav_item_settings);
}
fun findNavByPos(pos: Int): Navigation = when (pos) {
    Navigation.NAV_HOME.pos -> Navigation.NAV_HOME
    Navigation.NAV_MODULES.pos -> Navigation.NAV_MODULES
    Navigation.NAV_DOWNLOAD.pos -> Navigation.NAV_DOWNLOAD
    Navigation.NAV_LOGS.pos -> Navigation.NAV_LOGS
    Navigation.NAV_SUPPORT.pos -> Navigation.NAV_SUPPORT
    Navigation.NAV_ABOUT.pos -> Navigation.NAV_ABOUT
    Navigation.NAV_SETTINGS.pos -> Navigation.NAV_SETTINGS
    Navigation.FRAG_DEVICE.pos -> Navigation.FRAG_DEVICE
    Navigation.FRAG_DOWNLOAD_SETTINGS.pos -> Navigation.FRAG_DOWNLOAD_SETTINGS
    else -> Navigation.FRAG_ERROR
}
fun Navigation.createFragment(): Fragment = when (this) {
    Navigation.NAV_HOME -> StatusInstallerBrowseFragment.newInstance()
    Navigation.NAV_MODULES -> ModulesFragment.newInstance()
    Navigation.NAV_DOWNLOAD -> DownloadFragment.newInstance()
  //Navigation.NAV_LOGS -> LogsFragment.newInstance()
    Navigation.NAV_SUPPORT -> SupportFragment.newInstance()
    Navigation.NAV_ABOUT -> AboutFragment.newInstance()
    Navigation.NAV_SETTINGS -> SettingsFragment.newInstance()
    Navigation.FRAG_DEVICE -> DeviceInfoFragment.newInstance()
    Navigation.FRAG_DOWNLOAD_SETTINGS -> DownloadSettingsFragment.newInstance()
    else -> ErrorFragment.newInstance()
}

fun Navigation.getTag(): String = when (this) {
    Navigation.NAV_HOME -> StatusInstallerBrowseFragment.TAG
    Navigation.NAV_MODULES -> ModulesFragment.TAG
    Navigation.NAV_DOWNLOAD -> DownloadFragment.TAG
  //Navigation.NAV_LOGS -> LogsFragment.TAG
    Navigation.NAV_SUPPORT -> SupportFragment.TAG
    Navigation.NAV_ABOUT -> AboutFragment.TAG
    Navigation.NAV_SETTINGS -> SettingsFragment.TAG
    Navigation.FRAG_DEVICE -> DeviceInfoFragment.TAG
    Navigation.FRAG_DOWNLOAD_SETTINGS -> DownloadSettingsFragment.TAG
    else -> ErrorFragment.TAG
}