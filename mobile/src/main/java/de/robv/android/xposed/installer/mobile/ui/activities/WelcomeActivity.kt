package de.robv.android.xposed.installer.mobile.ui.activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.MenuItem
import android.view.View
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.mobile.logic.*
import de.robv.android.xposed.installer.mobile.ui.activities.base.BaseNavActivity
import de.robv.android.xposed.installer.mobile.ui.fragments.download.DownloadDetailsFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.view_toolbar.*

class WelcomeActivity: BaseNavActivity(),
        ModuleUtil.ModuleListener,
        Loader.Listener<RepoLoader>,
        View.OnClickListener,
        PopupMenu.OnMenuItemClickListener
{
    private var mRepoLoader: RepoLoader? = null

    override fun onReloadDone(loader: RepoLoader?) {
        notifyDataSetChanged()
    }

    override fun onInstalledModulesReloaded(moduleUtil: ModuleUtil?) {
        notifyDataSetChanged()
    }

    override fun onSingleInstalledModuleReloaded(moduleUtil: ModuleUtil?, packageName: String?, module: ModuleUtil.InstalledModule?) {
        notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        val id = v!!.id
        when(id){
            R.id.fabMenu ->{
                showFabMenu(v)
            }
            R.id.card_view_status ->{

            }
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navPosition = findNavPosById(item.itemId)
        return switchFragment(navPosition)
    }
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        try {
            val nav = findNavPosById(item!!.itemId)
            Utils().launchSheet(supportFragmentManager, nav)
        }catch (e: Exception){
            Log.e(XposedApp.TAG, e.message)
        }
        return true
    }
    override fun onNavigationItemReselected(item: MenuItem) {
        //TODO notify user to stop spamming!
    }

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        ThemeUtil.setTheme(this)
        restoreSaveInstanceState(savedInstanceBundle)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fabMenu.setOnClickListener(this@WelcomeActivity)

        setNav()
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
        drawerLayout()!!.setDrawerLockMode(lockOrNot)
        initVisFab()
        navActive(navPosition)
    }
    private fun restoreSaveInstanceState(savedInstanceState: Bundle?) {
        // Restore the current navigation position.
        savedInstanceState?.also {
           val id = it.getInt(navKeyPosition, getDefaultMenu().id)
            navPosition = findNavPosById(id)
        }

    }
    private fun initFragment(savedInstanceState: Bundle?) {
        savedInstanceState ?: switchFragment(navPosition)
    }

    private fun initVisFab(){
        fabMenu.hide()
        if (Utils().isBottomNav()) {
            if (navPosition != NavigationPosition.SETTINGS) fabMenu.show()
        }
        else{
            fabMenu.hide()
        }
    }
    private fun showFabMenu(v: View){
        Utils().launchMenu(this, v, R.menu.menu_popup, this).show()
    }

    private fun setNav() {
        if (Utils().isBottomNav()) {
            drawerNav()!!.visibility = View.GONE
            bottomNav()!!.visibility = View.VISIBLE
            initBottomNav(this, this)
        }
        else{
            bottomNav()!!.visibility = View.GONE
            drawerNav()!!.visibility = View.VISIBLE
            initDrawerNav(this,this)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun switchFragment(navPosition: NavigationPosition): Boolean {
        val fragment = supportFragmentManager.findFragment(navPosition)
        if (fragment!!.isAdded) return false
        detachFragment()
        attachFragment(fragment, navPosition.getTag())
        supportFragmentManager.executePendingTransactions()
        navActive(navPosition)
        initVisFab()
        closeDrawer()
        return true
    }

    private fun navActive(navPosition: NavigationPosition){
        val pos = navPosition.getPos()
        if (Utils().isBottomNav()){
            bottomNavActive(pos)
        } else{
            drawerNavActive(pos)
        }
    }

    private fun detachFragment() {
        supportFragmentManager.findFragmentById(R.id.content)?.also {
            supportFragmentManager.beginTransaction().detach(it).commit()
        }
    }
    private fun attachFragment(fragment: Fragment, tag: String) {
        if (fragment.isDetached) {
            supportFragmentManager.beginTransaction().attach(fragment).commit()
        } else {
            supportFragmentManager.beginTransaction().add(R.id.content, fragment, tag).commit()
        }
        // Set a transition animation for this transaction.
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }

    private fun android.support.v4.app.FragmentManager.findFragment(position: NavigationPosition): Fragment? {
        return findFragmentByTag(position.getTag()) ?: position.createFragment()
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

                Snackbar.make(parentLayout, R.string.modules_updates_available, Snackbar.LENGTH_LONG).setAction(getString(R.string.view)) { switchFragment(NavigationPosition.MODULES) }.show()
            }
        }catch (e: Exception){
            Log.d(XposedApp.TAG, e.message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            ModuleUtil.getInstance().removeListener(this)
            mRepoLoader?.removeListener(this)
        }catch (e: Exception){
            Log.d(XposedApp.TAG, e.message)
        }
    }
}