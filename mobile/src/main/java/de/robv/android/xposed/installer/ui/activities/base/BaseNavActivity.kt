package de.robv.android.xposed.installer.ui.activities.base

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
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.NavigationPosition
import de.robv.android.xposed.installer.logic.Utils
import de.robv.android.xposed.installer.ui.activities.XposedBaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.view_toolbar.*

@SuppressLint("Registered")
open class BaseNavActivity: XposedBaseActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener,
        NavigationView.OnNavigationItemSelectedListener
{
    val NAV_KEY_POSITION = "keyPosition"
    var navPosition: NavigationPosition = getDefaultMenu()

    private var myToggle: ActionBarDrawerToggle? = null

    override fun onNavigationItemReselected(item: MenuItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getDefaultMenu(): NavigationPosition {
        return try {
            val id = Utils().getView()
            when (id) {
                0 -> NavigationPosition.HOME
                1 -> NavigationPosition.MODULES
                2 -> NavigationPosition.DOWNLOAD
                3 -> NavigationPosition.LOGS
                else -> NavigationPosition.HOME
            }
        }catch (e: Exception){
            Log.e(XposedApp.TAG, e.message)
            return NavigationPosition.HOME
        }
    }

    //Bottom nav section
    open fun initBottomNav(selectedListener: BottomNavigationView.OnNavigationItemSelectedListener, reSelectedListener: BottomNavigationView.OnNavigationItemReselectedListener) {
        getBottomNav()!!.setOnNavigationItemSelectedListener(selectedListener)
        getBottomNav()!!.setOnNavigationItemReselectedListener(reSelectedListener)
    }

    fun setBottomNavActive(position: Int) {
        getBottomNav()!!.menu.getItem(position).isChecked = true
    }

    fun getBottomNav(): BottomNavigationView?{
        return bottom_navigation
    }

    //Nav navigation_drawer section
    open fun initDrawerNav(activity: Activity, listener: NavigationView.OnNavigationItemSelectedListener) {
        myToggle = ActionBarDrawerToggle(
                activity, getDrawerLayout(), getToolBar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        getDrawerLayout()!!.addDrawerListener(myToggle!!)
        myToggle!!.syncState()

        getDrawerNav()!!.setNavigationItemSelectedListener(listener)
    }
    fun setDrawerNavActive(position: Int) {
        getDrawerNav()!!.menu.getItem(position).isChecked = true
    }

    open fun closeDrawer() {
        getDrawerLayout()!!.closeDrawer(GravityCompat.START)
    }
    open fun openDrawer() {
        getDrawerLayout()!!.openDrawer(GravityCompat.START)
    }
    open fun isDrawerOpen(): Boolean {
        return getDrawerLayout()!!.isDrawerOpen(GravityCompat.START)
    }
    open fun selectDrawerMenuItem(isChecked: Boolean) {
        for (i in 0 until getDrawerMenuSize()) {
            unSelectDrawerMenuItem(i, isChecked)
        }
    }
    open fun unSelectDrawerMenuItem(i: Int, isChecked: Boolean) {
        getDrawerNav()!!.menu.getItem(i).isChecked = isChecked
    }
    private fun getDrawerMenuSize(): Int {
        return getDrawerNav()!!.menu.size()
    }

    open fun getToolBar(): Toolbar? {
        return toolbar
    }
    open fun getDrawerLayout(): DrawerLayout? {
        return drawer_layout
    }
    open fun getDrawerNav(): NavigationView? {
        return drawer_navigation!!
    }
}