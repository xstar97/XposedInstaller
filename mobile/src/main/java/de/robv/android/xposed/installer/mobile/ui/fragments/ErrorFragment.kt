package de.robv.android.xposed.installer.mobile.ui.fragments

import android.support.v4.app.Fragment

//TODO add error dialog
class ErrorFragment: Fragment()
{
    companion object {
        val TAG: String = ErrorFragment::class.java.simpleName
        fun newInstance() = ErrorFragment()
    }
}