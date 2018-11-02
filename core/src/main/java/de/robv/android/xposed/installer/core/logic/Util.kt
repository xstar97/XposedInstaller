package de.robv.android.xposed.installer.core.logic

import android.app.UiModeManager
import android.content.Context
import android.content.Context.UI_MODE_SERVICE
import android.content.res.Configuration
import android.support.v7.widget.PopupMenu
import android.view.View

open class Util
{
    open fun launchMenu(context: Context, v: View, menu: Int, delegate: PopupMenu.OnMenuItemClickListener): PopupMenu {
        val popUp = PopupMenu(context, v)
        popUp.menuInflater.inflate(menu, popUp.menu)
        popUp.setOnMenuItemClickListener(delegate)
        return popUp
    }
    open fun isDeviceTV(context: Context): Boolean {
        val uiModeManager = context.getSystemService(UI_MODE_SERVICE) as UiModeManager
        return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    }
}