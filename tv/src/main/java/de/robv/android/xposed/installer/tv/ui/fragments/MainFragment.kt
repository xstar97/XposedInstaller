package de.robv.android.xposed.installer.tv.ui.fragments

import android.os.Bundle
import android.support.v17.leanback.widget.*
import android.util.Log
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.core.models.NavModel
import de.robv.android.xposed.installer.tv.logic.NavigationPosition
import de.robv.android.xposed.installer.tv.ui.activities.containers.ViewActivity
import de.robv.android.xposed.installer.tv.ui.activities.containers.ViewActivity.Companion.INTENT_ACTIVITY_KEY

import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseNavFragment
import org.jetbrains.anko.startActivity

class MainFragment : BaseNavFragment(), OnItemViewSelectedListener, OnItemViewClickedListener
{
    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        try {
            val nav = item as NavModel
            val pos = nav.pos
            activity!!.startActivity<ViewActivity>(INTENT_ACTIVITY_KEY to pos)
            Log.d(XposedApp.TAG, "pos: ${nav.pos}\nnav: ${nav.title}")
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