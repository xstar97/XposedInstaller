package de.robv.android.xposed.installer.mobile.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseSettings
import de.robv.android.xposed.installer.core.logic.delegates.NavigationDelegate
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.view_toolbar.*

@SuppressLint("Registered")
open class BaseNavActivity: XposedBaseActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener,
        NavigationView.OnNavigationItemSelectedListener, NavigationDelegate
{
    companion object {
        const val navKeyPosition = "keyPosition"
    }
    private var navigationDelegate: NavigationDelegate? = null

    fun setDelegate(delegate: NavigationDelegate): NavigationDelegate?{
        this.navigationDelegate = delegate
        return this
    }
    override fun onNavSelected(nav: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onNavReSelected(nav: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        val nav = findNavPosById(item.itemId)
        navigationDelegate?.onNavReSelected(nav)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val nav = findNavPosById(item.itemId)
        navigationDelegate?.onNavSelected(nav)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return if (Utils().isBottomNav()) {
            menuInflater.inflate(R.menu.menu_fragments, menu)
            true
        }else{
            false
        }
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        when (id) {
            R.id.nav_item_support, R.id.nav_item_about ->{
                val nav = findNavPosById(id)
                val context = if (XposedApp.getPreferences().getString(BaseSettings.prefSubView, "0")!!.toInt() == 2) this else myManager
                Utils().launchView(context, nav)
                return true
            }
        }
        return true
    }


    var navPosition: Navigation = getDefaultMenu()
    fun getDefaultMenu(): Navigation {
        return try {
            val id = Utils().getView()
            when (id) {
                0 -> Navigation.NAV_HOME
                1 -> Navigation.NAV_MODULES
                2 -> Navigation.NAV_DOWNLOAD
                3 -> Navigation.NAV_LOGS
                else -> Navigation.FRAG_ERROR
            }
        }catch (e: Exception){
            Log.e(XposedApp.TAG, e.message)
            return Navigation.FRAG_ERROR
        }
    }

    val myManager = supportFragmentManager

    fun restoreSaveInstanceState(savedInstanceState: Bundle?) {
        // Restore the current navigation position.
        savedInstanceState?.also {
            val id = it.getInt(navKeyPosition, getDefaultMenu().id)
            navPosition = findNavPosById(id)
        }

    }
    fun initFragment(savedInstanceState: Bundle?) {
        savedInstanceState ?: switchFragment(navPosition)
    }

    fun initNav(selectedListener: BottomNavigationView.OnNavigationItemSelectedListener?, reSelectedListener: BottomNavigationView.OnNavigationItemReselectedListener?, drawerDelegate: NavigationView.OnNavigationItemSelectedListener?) {
        if (Utils().isBottomNav()) {
            bottom_navigation.setOnNavigationItemSelectedListener(selectedListener)
            bottom_navigation.setOnNavigationItemReselectedListener(reSelectedListener)
        } else{
            val myToggle = ActionBarDrawerToggle(
                    this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawer_layout.addDrawerListener(myToggle)
            myToggle.syncState()

            drawer_navigation.setNavigationItemSelectedListener(drawerDelegate)
        }
    }
    fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }
    fun openDrawer() {
        drawer_layout.openDrawer(GravityCompat.START)
    }
    fun isDrawerOpen(): Boolean {
        return drawer_layout.isDrawerOpen(GravityCompat.START)
    }
    fun selectDrawerMenuItems(isChecked: Boolean) {
        for (i in 0 until drawerMenuSize()) {
            selectDrawerMenuItem(i, isChecked)
        }
    }
    fun selectDrawerMenuItem(i: Int, isChecked: Boolean) {
        drawer_navigation.menu.getItem(i).isChecked = isChecked
    }
    private fun drawerMenuSize(): Int {
        return drawer_navigation.menu.size()
    }

    //BaseNavigation
    fun buildNav() {
        if (Utils().isBottomNav()) {
            drawer_navigation.visibility = View.GONE
            bottom_navigation.visibility = View.VISIBLE
            initNav(this, this, null)
        }
        else{
            bottom_navigation.visibility = View.GONE
            drawer_navigation.visibility = View.VISIBLE
            initNav(null, null, this)
        }
    }

    fun switchFragment(navPosition: Navigation): Boolean {
        val fragment = myManager?.findFragment(navPosition)
        if (fragment!!.isAdded) return false
        detachFragment()
        attachFragment(fragment, navPosition.getTag())
        myManager?.executePendingTransactions()
        setNavActive(navPosition.getNavPos())
        closeDrawer()
        return true
    }
    fun setNavActive(position: Int){
        if (Utils().isBottomNav())
            bottom_navigation.menu.getItem(position).isChecked = true
        else
            drawer_navigation.menu.getItem(position).isChecked = true
    }

    private fun detachFragment() {
        myManager?.findFragmentById(R.id.content)?.also {
            myManager.beginTransaction().detach(it).commit()
        }
    }
    private fun attachFragment(fragment: Fragment, tag: String) {
        if (fragment.isDetached) {
            myManager?.beginTransaction()?.attach(fragment)?.commit()
        } else {
            myManager?.beginTransaction()?.add(R.id.content, fragment, tag)?.commit()
        }
        // Set a transition animation for this transaction.
        myManager!!.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }

    fun android.support.v4.app.FragmentManager.findFragment(position: Navigation): Fragment? {
        return findFragmentByTag(position.getTag()) ?: position.createFragment()
    }
}