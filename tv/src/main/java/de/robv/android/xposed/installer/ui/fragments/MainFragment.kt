package de.robv.android.xposed.installer.ui.fragments

import android.os.Bundle
import android.support.v17.leanback.widget.*
import android.util.Log
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.NavigationPosition

import de.robv.android.xposed.installer.logic.getTitle
import de.robv.android.xposed.installer.ui.activities.containers.ViewActivity
import de.robv.android.xposed.installer.ui.activities.containers.ViewActivity.Companion.INTENT_ACTIVITY_KEY

import de.robv.android.xposed.installer.ui.fragments.base.BaseNavFragment
import org.jetbrains.anko.startActivity

class MainFragment : BaseNavFragment(), OnItemViewSelectedListener, OnItemViewClickedListener
{
    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        try {
            val name = item as String
            val pos = when(name){
                NavigationPosition.HOME.getTitle(activity!!) -> NavigationPosition.HOME.pos
                NavigationPosition.SETTINGS.getTitle(activity!!) -> NavigationPosition.SETTINGS.pos
                else -> NavigationPosition.ERROR.pos
            }
            activity!!.startActivity<ViewActivity>(INTENT_ACTIVITY_KEY to pos)
            Log.d(XposedApp.TAG, "id: $id")
        }catch (e: Exception){
            Log.d(XposedApp.TAG, e.message)
        }
    }

    override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUIElements()
        loadRows(activity!!)
        onItemViewClickedListener = this
        onItemViewSelectedListener = this
    }
}