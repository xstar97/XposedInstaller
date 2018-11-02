package de.robv.android.xposed.installer.mobile.logic

import android.support.annotation.IntegerRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.mobile.ui.download.DownloadDetailsSettingsFragment
import de.robv.android.xposed.installer.mobile.ui.download.DownloadDetailsVersionsFragment
import de.robv.android.xposed.installer.mobile.ui.error.ErrorFragment
import de.robv.android.xposed.installer.mobile.ui.logs.LogsFragment
import de.robv.android.xposed.installer.mobile.ui.settings.SettingsFragment
import de.robv.android.xposed.installer.mobile.ui.framework.StatusInstallerFragment
import de.robv.android.xposed.installer.mobile.ui.download.DownloadDetailsFragment
import de.robv.android.xposed.installer.mobile.ui.download.DownloadFragment
import de.robv.android.xposed.installer.mobile.ui.list.AboutFragment
import de.robv.android.xposed.installer.mobile.ui.list.DeviceInfoFragment
import de.robv.android.xposed.installer.mobile.ui.list.SupportFragment
import de.robv.android.xposed.installer.mobile.ui.module.ModulesBookmarkFragment
import de.robv.android.xposed.installer.mobile.ui.module.ModulesFragment

enum class Navigation(@IntegerRes val id: Int, @StringRes val title: Int)
{
    NAV_DOWNLOAD(R.id.nav_item_downloads, R.string.nav_item_download),
    NAV_MODULES(R.id.nav_item_modules, R.string.nav_item_modules),
    NAV_HOME(R.id.nav_item_framework, R.string.nav_item_install),
    NAV_LOGS(R.id.nav_item_logs, R.string.nav_item_logs),
    NAV_SETTINGS(R.id.nav_item_settings, R.string.nav_item_settings),
    NAV_SUPPORT(R.id.nav_item_support, R.string.nav_item_support),
    NAV_ABOUT(R.id.nav_item_about, R.string.nav_item_about),
    FRAG_DEVICE(R.id.frag_device_info, R.string.framework_device_info),
    FRAG_MODULE_BOOKMARK(R.id.frag_module_bookmark, R.string.bookmarks),
    FRAG_DOWNLOAD_DESCRIPTION(R.id.frag_download_description, R.string.download_details_page_description),
    FRAG_DOWNLOAD_VERSION(R.id.frag_download_version, R.string.download_details_page_versions),
    FRAG_DOWNLOAD_SETTINGS(R.id.frag_download_settings, R.string.download_details_page_settings),
    FRAG_ERROR(R.id.frag_item_error, R.string.error_fragment_title);
}
fun findNavPosById(id: Int): Navigation = when (id) {
    Navigation.NAV_DOWNLOAD.id -> Navigation.NAV_DOWNLOAD
    Navigation.NAV_MODULES.id -> Navigation.NAV_MODULES
    Navigation.NAV_HOME.id -> Navigation.NAV_HOME
    Navigation.NAV_LOGS.id -> Navigation.NAV_LOGS
    Navigation.NAV_SETTINGS.id -> Navigation.NAV_SETTINGS
    Navigation.NAV_SUPPORT.id -> Navigation.NAV_SUPPORT
    Navigation.NAV_ABOUT.id -> Navigation.NAV_ABOUT
    else -> Navigation.FRAG_ERROR
}

fun Navigation.createFragment(): Fragment = when (this) {
    Navigation.NAV_DOWNLOAD -> DownloadFragment.newInstance()
    Navigation.NAV_MODULES -> ModulesFragment.newInstance()
    Navigation.NAV_HOME -> StatusInstallerFragment.newInstance()
    Navigation.NAV_LOGS -> LogsFragment.newInstance()
    Navigation.NAV_SETTINGS -> SettingsFragment.newInstance()
    Navigation.NAV_SUPPORT -> SupportFragment.newInstance()
    Navigation.NAV_ABOUT -> AboutFragment.newInstance()
    Navigation.FRAG_DEVICE -> DeviceInfoFragment.newInstance()
    Navigation.FRAG_MODULE_BOOKMARK -> ModulesBookmarkFragment.newInstance()
    Navigation.FRAG_DOWNLOAD_DESCRIPTION -> DownloadDetailsFragment.newInstance()
    Navigation.FRAG_DOWNLOAD_VERSION -> DownloadDetailsVersionsFragment.newInstance()
    Navigation.FRAG_DOWNLOAD_SETTINGS -> DownloadDetailsSettingsFragment.newInstance()
    else -> ErrorFragment.newInstance()
}

fun Navigation.getTag(): String = when (this) {
    Navigation.NAV_DOWNLOAD -> DownloadFragment.TAG
    Navigation.NAV_MODULES -> ModulesFragment.TAG
    Navigation.NAV_HOME -> StatusInstallerFragment.TAG
    Navigation.NAV_LOGS -> LogsFragment.TAG
    Navigation.NAV_SETTINGS -> SettingsFragment.TAG
    Navigation.NAV_SUPPORT -> SupportFragment.TAG
    Navigation.NAV_ABOUT -> AboutFragment.TAG
    Navigation.FRAG_DEVICE -> DeviceInfoFragment.TAG
    Navigation.FRAG_MODULE_BOOKMARK -> ModulesBookmarkFragment.TAG
    Navigation.FRAG_DOWNLOAD_DESCRIPTION -> DownloadDetailsFragment.TAG
    Navigation.FRAG_DOWNLOAD_VERSION -> DownloadDetailsVersionsFragment.TAG
    Navigation.FRAG_DOWNLOAD_SETTINGS -> DownloadDetailsSettingsFragment.TAG
    else -> ErrorFragment.TAG
}

fun Navigation.getNavPos(): Int = when (this) {
    Navigation.NAV_DOWNLOAD -> setNavPos(0, 2)
    Navigation.NAV_MODULES -> 1
    Navigation.NAV_HOME -> setNavPos(2, 0)
    Navigation.NAV_LOGS -> 3
    Navigation.NAV_SETTINGS -> 4
    Navigation.NAV_SUPPORT -> 5
    Navigation.NAV_ABOUT -> 6
    else -> -1
}
private fun setNavPos(bottom: Int, drawer: Int) = if (Utils().isBottomNav()) bottom else drawer
