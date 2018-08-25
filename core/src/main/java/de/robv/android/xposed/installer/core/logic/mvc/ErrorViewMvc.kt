package de.robv.android.xposed.installer.core.logic.mvc

import android.view.View

interface ErrorViewMvc
{
    fun getRootView() : View

    interface StateDelegate
    {
        fun onErrorNavigate()
    }
    fun setDelegate(delegate: ErrorViewMvc.StateDelegate)
}