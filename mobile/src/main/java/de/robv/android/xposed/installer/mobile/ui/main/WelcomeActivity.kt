package de.robv.android.xposed.installer.mobile.ui.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.DrawerLayout
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.delegates.NavigationDelegate
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.Navigation
import de.robv.android.xposed.installer.mobile.logic.ThemeUtil
import de.robv.android.xposed.installer.mobile.logic.Utils
import de.robv.android.xposed.installer.mobile.logic.getNavPos
import de.robv.android.xposed.installer.mobile.ui.base.BaseNavActivity
import de.robv.android.xposed.installer.mobile.ui.download.DownloadDetailsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.view_toolbar.*

class WelcomeActivity: BaseNavActivity(),
        ModuleUtil.ModuleListener,
        Loader.Listener<RepoLoader>, NavigationDelegate
{
    override fun onNavSelected(nav: Any) {
        navPosition = nav as Navigation//findNavPosById(item.itemId)
        switchFragment(navPosition)
    }

    override fun onNavReSelected(nav: Any) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mRepoLoader: RepoLoader? = null

    override fun onReloadDone(loader: RepoLoader) {
        notifyDataSetChanged()
    }

    override fun onInstalledModulesReloaded(moduleUtil: ModuleUtil?) {
        notifyDataSetChanged()
    }

    override fun onSingleInstalledModuleReloaded(moduleUtil: ModuleUtil?, packageName: String?, module: ModuleUtil.InstalledModule?) {
        notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        ThemeUtil.setTheme(this)
        restoreSaveInstanceState(savedInstanceBundle)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setDelegate(this)

        buildNav()
        initFragment(savedInstanceBundle)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        // Store the current navigation position.
        outState?.putInt(navKeyPosition, navPosition.id)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (isDrawerOpen()){
            closeDrawer()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val lockOrNot = if (Utils().isBottomNav()) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED
        drawer_layout!!.setDrawerLockMode(lockOrNot)
        setNavActive(navPosition.getNavPos())
    }


    private fun notifyDataSetChanged() {
        val parentLayout = content
        val frameworkUpdateVersion = mRepoLoader!!.frameworkUpdateVersion
        val moduleUpdateAvailable = mRepoLoader!!.hasModuleUpdates()

        try {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.content)

            if (currentFragment is DownloadDetailsFragment) {
                if (frameworkUpdateVersion != null) {
                    Snackbar.make(parentLayout, R.string.welcome_framework_update_available.toString() + " " + frameworkUpdateVersion.toString(), Snackbar.LENGTH_LONG).show()
                }
            }

            val snackBar = XposedApp.getPreferences().getBoolean("snack_bar", true)

            if (moduleUpdateAvailable && snackBar) {

                Snackbar.make(parentLayout, R.string.modules_updates_available, Snackbar.LENGTH_LONG).setAction(getString(R.string.view)) { switchFragment(Navigation.NAV_MODULES) }.show()
            }
        }catch (e: Exception){
            Log.d(XposedApp.TAG, e.message)
        }
    }
}