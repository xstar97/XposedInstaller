package de.robv.android.xposed.installer.logic.adapters.nav

import android.content.Context
import de.robv.android.xposed.installer.R

object NavList
{
    fun navCategories(context: Context) = arrayOf(
            context.getString(R.string.nav_item_install),
            context.getString(R.string.nav_item_modules),
            context.getString(R.string.nav_item_download),
            context.getString(R.string.nav_item_logs),
            context.getString(R.string.nav_item_support),
            context.getString(R.string.nav_item_about),
            context.getString(R.string.nav_item_settings)
    )
}