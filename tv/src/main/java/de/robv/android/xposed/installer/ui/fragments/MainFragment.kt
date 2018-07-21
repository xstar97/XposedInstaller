package de.robv.android.xposed.installer.ui.fragments

import android.os.Bundle
import de.robv.android.xposed.installer.ui.fragments.base.BaseNavFragment

class MainFragment : BaseNavFragment()
{
    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUIElements()
        loadRows(activity!!)
    }
}