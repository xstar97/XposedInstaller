package de.robv.android.xposed.installer.core.logic.mvc

import android.view.View

interface StateViewMvc
{
    fun getRootView() : View

    interface StateDelegate
    {
        fun onShow()
        fun onLoading()
        fun onError()
    }
    fun setDelegate(delegate: StateDelegate)
}