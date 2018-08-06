package de.robv.android.xposed.installer.mobile.ui.activities.base

import android.annotation.SuppressLint
import android.app.Activity
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle

import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.Navigation
import de.robv.android.xposed.installer.mobile.logic.Utils
import de.robv.android.xposed.installer.mobile.ui.activities.XposedBaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.view_toolbar.*

@SuppressLint("Registered")
open class BaseNavActivity: XposedBaseActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener,
        NavigationView.OnNavigationItemSelectedListener
{
    companion object {
        const val navKeyPosition = "keyPosition"
    }
    var navPosition: Navigation = getDefaultMenu()

    private var myToggle: ActionBarDrawerToggle? = null

    override fun onNavigationItemReselected(item: MenuItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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

    //Bottom nav section
    open fun initBottomNav(selectedListener: BottomNavigationView.OnNavigationItemSelectedListener, reSelectedListener: BottomNavigationView.OnNavigationItemReselectedListener) {
        bottomNav()!!.setOnNavigationItemSelectedListener(selectedListener)
        bottomNav()!!.setOnNavigationItemReselectedListener(reSelectedListener)
    }

    fun bottomNavActive(position: Int) {
        bottomNav()!!.menu.getItem(position).isChecked = true
    }

    fun bottomNav(): BottomNavigationView?{
        return bottom_navigation
    }

    //Nav navigation_drawer section
    open fun initDrawerNav(activity: Activity, listener: NavigationView.OnNavigationItemSelectedListener) {
        myToggle = ActionBarDrawerToggle(
                activity, drawerLayout(), toolBar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout()!!.addDrawerListener(myToggle!!)
        myToggle!!.syncState()

        drawerNav()!!.setNavigationItemSelectedListener(listener)
    }
    fun drawerNavActive(position: Int) {
        drawerNav()!!.menu.getItem(position).isChecked = true
    }

    open fun closeDrawer() {
        drawerLayout()!!.closeDrawer(GravityCompat.START)
    }
    open fun openDrawer() {
        drawerLayout()!!.openDrawer(GravityCompat.START)
    }
    open fun isDrawerOpen(): Boolean {
        return drawerLayout()!!.isDrawerOpen(GravityCompat.START)
    }
    open fun selectDrawerMenuItem(isChecked: Boolean) {
        for (i in 0 until drawerMenuSize()) {
            unSelectDrawerMenuItem(i, isChecked)
        }
    }
    open fun unSelectDrawerMenuItem(i: Int, isChecked: Boolean) {
        drawerNav()!!.menu.getItem(i).isChecked = isChecked
    }
    private fun drawerMenuSize(): Int {
        return drawerNav()!!.menu.size()
    }

    open fun toolBar(): Toolbar? {
        return toolbar
    }
    open fun drawerLayout(): DrawerLayout? {
        return drawer_layout
    }
    open fun drawerNav(): NavigationView? {
        return drawer_navigation
    }
}