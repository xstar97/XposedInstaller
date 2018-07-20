package de.robv.android.xposed.installer.ui.fragments

import android.os.Bundle
import android.support.v17.leanback.app.HeadersSupportFragment
import android.support.v17.leanback.widget.Row
import android.support.v17.leanback.widget.RowHeaderPresenter
import android.util.Log
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.*
import de.robv.android.xposed.installer.ui.activities.containers.ViewActivity
import de.robv.android.xposed.installer.ui.activities.containers.ViewActivity.Companion.INTENT_ACTIVITY_KEY
import de.robv.android.xposed.installer.ui.fragments.base.BaseNavFragment
import org.jetbrains.anko.startActivity

class MainFragment : BaseNavFragment(), HeadersSupportFragment.OnHeaderViewSelectedListener, HeadersSupportFragment.OnHeaderClickedListener {

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    override fun onHeaderClicked(viewHolder: RowHeaderPresenter.ViewHolder?, row: Row?) {
        val item = row!!.headerItem
        val id = item.id.toString().toInt()
        try {
            val nav = NavigationPosition.values()
            val pos = nav[id].pos
            activity!!.startActivity<ViewActivity>(INTENT_ACTIVITY_KEY to pos)
        }catch (e: Exception){
            Log.e(XposedApp.TAG, e.message)
        }
    }

    override fun onHeaderSelected(viewHolder: RowHeaderPresenter.ViewHolder?, row: Row?) {
        //Toast.makeText(activity, "selected: ${row!!.headerItem.name}", Toast.LENGTH_LONG).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUIElements(this, this)
        loadRows(activity!!)
    }
}