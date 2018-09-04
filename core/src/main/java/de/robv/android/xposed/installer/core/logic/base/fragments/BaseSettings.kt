package de.robv.android.xposed.installer.core.logic.base.fragments

import de.robv.android.xposed.installer.core.logic.base.BaseXposedApp
import java.io.File

open class BaseSettings
{
    companion object {
         val mDisableResourcesFlag = File(BaseXposedApp().BASE_DIR + "conf/disable_resources")

        const val  prefNameModuleSettings ="module_settings"

        const val prefNoGlobal = "no_global"
        const val prefType = "release_type_global"
        const val prefDownload = "download_location"
        const val prefInstallWarn = "hide_install_warning"
        const val prefDownloadSort = "download_sorting_order"
        //only available in mobile only!!!
        const val prefRes = "disable_resources"
        const val prefNav = "default_navigation"
        const val prefTheme = "theme"
        const val prefSubView = "default_sub_view"
    }
}