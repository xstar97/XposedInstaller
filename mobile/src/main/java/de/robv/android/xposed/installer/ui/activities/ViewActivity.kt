package de.robv.android.xposed.installer.ui.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.View
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.logic.ThemeUtil
import de.robv.android.xposed.installer.ui.fragments.AboutFragment
import de.robv.android.xposed.installer.ui.fragments.SupportFragment

/**
 * Universal activity...
 */
class ViewActivity: XposedBaseActivity()
{
    val FRAGMENT_ABOUT = 0
    val FRAGMENT_SUPPORT = 1

    private val stuff = intent.extras!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.setTheme(this)
        setContentView(R.layout.activity_container)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { finish() }

        val ab = supportActionBar
        if (ab != null) {
            ab.title = getString(getMyTitle())
            ab.setDisplayHomeAsUpEnabled(true)
        }
        setFloating(toolbar, getMyTitle())

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, getFragment()).commit()
        }
    }

    private fun getMyTitle(): Int{
        return if (getInt() == FRAGMENT_ABOUT)
            R.string.nav_item_about
        else
            R.string.nav_item_support
    }

    private fun getFragment(): Fragment{
        return if (getInt() == FRAGMENT_ABOUT)
            AboutFragment()
        else
            SupportFragment()
    }

    private fun getInt(): Int{
        return stuff.get("intFrag") as Int
    }
}