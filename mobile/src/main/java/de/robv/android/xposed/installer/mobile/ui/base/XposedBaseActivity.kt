package de.robv.android.xposed.installer.mobile.ui.base

import android.os.Build
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import de.robv.android.xposed.installer.R.bool.isTablet
import de.robv.android.xposed.installer.R.color.colorPrimaryDark
import de.robv.android.xposed.installer.R.drawable.ic_close

import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.mobile.logic.ThemeUtil

abstract class XposedBaseActivity : AppCompatActivity()
{
    var mTheme = -1

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        ThemeUtil.setTheme(this)
    }

    override fun onResume() {
        super.onResume()

        ThemeUtil.reloadTheme(this)
    }

    fun setFloating(toolbar: Toolbar, @StringRes details: Int) {
        val isTablet = resources.getBoolean(isTablet)
        if (isTablet) {
            val params = window.attributes
            params.height = resources.getDimensionPixelSize(R.dimen.floating_height)
            params.width = resources.getDimensionPixelSize(R.dimen.floating_width)
            params.alpha = 1.0f
            params.dimAmount = 0.6f
            params.flags = params.flags or 2
            window.attributes = params

            if (details != 0) {
                toolbar.setTitle(details)
            }
            toolbar.setNavigationIcon(ic_close)
            setFinishOnTouchOutside(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val color = ContextCompat.getColor(this, colorPrimaryDark)
            window.statusBarColor = color
        }
    }
}