package de.robv.android.xposed.installer.ui.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.ThemeUtil
import de.robv.android.xposed.installer.ui.fragments.AboutFragment
import de.robv.android.xposed.installer.ui.fragments.SupportFragment
import org.jetbrains.anko.selector
import org.jetbrains.anko.startActivity

/**
 * Universal activity...
 */
class ViewActivity: XposedBaseActivity()
{
    val FRAGMENT_ABOUT = 0
    val FRAGMENT_SUPPORT = 1

    //private val stuff = intent.extras!!

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        ThemeUtil.setTheme(this)
        setContentView(R.layout.activity_container)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { finish() }

        try {
            if (getInt() != -1) {
                val ab = supportActionBar
                if (ab != null) {
                    ab.title = getString(getMyTitle())
                    ab.setDisplayHomeAsUpEnabled(true)
                }
                setFloating(toolbar, getMyTitle())

                if (savedInstanceBundle == null) {
                    supportFragmentManager.beginTransaction().replace(R.id.container, getFragment()).commit()
                }
            }else{
                errorHandling()
            }
        }catch (e: Exception){
            Log.e(XposedApp.TAG, e.message)
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
        val intent = this.intent.extras!!.get("intFrag").toString().toInt()
        Log.d(XposedApp.TAG, "intFrag: $intent")
        return if(intent == 1 || intent == 0)
        intent
        else -1
    }

    private fun errorHandling(){
        val fragments = listOf(getString(R.string.nav_item_support), getString(R.string.nav_item_about))
        selector(getString(R.string.app_name), fragments) { dialogInterface, i ->
            finish()
            val aboutOrSupport = if(fragments[i] == getString(R.string.nav_item_support)) 1 else 0
            this.startActivity<ViewActivity>("intFrag" to aboutOrSupport)
        }
    }
}