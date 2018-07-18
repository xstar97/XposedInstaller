package de.robv.android.xposed.installer.ui.activities

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
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.logic.*
import de.robv.android.xposed.installer.ui.fragments.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.view_toolbar.*
import de.robv.android.xposed.installer.logic.Utils.Companion.isBottomNav

class WelcomeActivity: BaseNavActivity(), ModuleUtil.ModuleListener, Loader.Listener<RepoLoader>, View.OnClickListener, PopupMenu.OnMenuItemClickListener
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
        if (id == R.id.fabMenu){
            showFabMenu(v)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navPosition = findNavPosById(item.itemId)

        return switchFragment(navPosition)
    }
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val nav = if (item!!.itemId == R.id.nav_item_about) NavigationPosition.ABOUT else NavigationPosition.SUPPORT
        Utils.launchSheet(getFragManager(), nav)
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
        outState?.putInt(NAV_KEY_POSITION, navPosition.id)
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
        val lockOrNot = if (isBottomNav()) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED
        getDrawerLayout()!!.setDrawerLockMode(lockOrNot)
        initVisFab()
        setNavActive(navPosition)
    }
    private fun restoreSaveInstanceState(savedInstanceState: Bundle?) {
        // Restore the current navigation position.
        savedInstanceState?.also {

           val id = it.getInt(NAV_KEY_POSITION, getDefaultMenu().id)
            navPosition = findNavPosById(id)
        }

    }
    private fun initFragment(savedInstanceState: Bundle?) {
        savedInstanceState ?: switchFragment(navPosition)
    }

    private fun initVisFab(){
        fabMenu.hide()
        if (isBottomNav()) {
            if (navPosition != NavigationPosition.SETTINGS) fabMenu.show()
        }
        else{
            fabMenu.hide()
        }
    }
    private fun showFabMenu(v: View){
        Utils.launchMenu(this, v, R.menu.menu_popup, this).show()
    }

    private fun setNav() {
        if (isBottomNav()) {
            getDrawerNav()!!.visibility = View.GONE
            getBottomNav()!!.visibility = View.VISIBLE
            initBottomNav(this, this)
        }
        else{
            getBottomNav()!!.visibility = View.GONE
            getDrawerNav()!!.visibility = View.VISIBLE
            initDrawerNav(this,this)
        }
    }

    fun switchFragment(navPosition: NavigationPosition): Boolean {
        val fragment = getFragManager().findFragment(navPosition)
        if (fragment!!.isAdded) return false
        detachFragment()
        attachFragment(fragment, navPosition.getTag())
        getFragManager().executePendingTransactions()
        setNavActive(navPosition)
        initVisFab()
        //if drawer is 'active' and 'open'...action will close it!
        closeDrawer()
        return true
    }
    private fun setNavActive(navPosition: NavigationPosition){
        if (isBottomNav()){
            setBottomNavActive(navPosition.getPos())
        } else{
            setDrawerNavActive(navPosition.getPos())
        }
    }
    private fun android.support.v4.app.FragmentManager.findFragment(position: NavigationPosition): Fragment? {
        return findFragmentByTag(position.getTag()) ?: position.createFragment()
    }
    private fun detachFragment() {
        getFragManager().findFragmentById(R.id.content)?.also {
            getFragManager().beginTransaction().detach(it).commit()
        }
    }
    private fun attachFragment(fragment: Fragment, tag: String) {
        if (fragment.isDetached) {
            getFragManager().beginTransaction().attach(fragment).commit()
        } else {
            getFragManager().beginTransaction().add(R.id.content, fragment, tag).commit()
        }
        // Set a transition animation for this transaction.
        getFragManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }
    private fun getFragManager(): android.support.v4.app.FragmentManager{
        return supportFragmentManager
    }

    private fun notifyDataSetChanged() {
        val parentLayout = content
        val frameworkUpdateVersion = mRepoLoader!!.frameworkUpdateVersion
        val moduleUpdateAvailable = mRepoLoader!!.hasModuleUpdates()

        try {
            val currentFragment = getFragManager().findFragmentById(R.id.content)

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