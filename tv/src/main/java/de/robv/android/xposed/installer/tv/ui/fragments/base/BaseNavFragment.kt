package de.robv.android.xposed.installer.tv.ui.fragments.base

import android.content.Context
import android.graphics.Color
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v4.content.ContextCompat
import de.robv.android.xposed.installer.R
import android.widget.TextView
import android.view.Gravity
import android.view.ViewGroup

import de.robv.android.xposed.installer.tv.logic.NavigationPosition

import android.support.v17.leanback.widget.*
import android.support.v17.leanback.widget.Presenter
import android.util.Log
import de.robv.android.xposed.installer.core.models.NavModel
import de.robv.android.xposed.installer.tv.XposedApp

open class BaseNavFragment: BrowseSupportFragment()
{
    companion object {
        val baseTag: String = BaseNavFragment::class.java.simpleName
        fun newInstance() = BaseNavFragment()

        private const val GRID_ITEM_WIDTH = 300
        private const val GRID_ITEM_HEIGHT = 200
        private var mRowsAdapter: ArrayObjectAdapter? = null
    }

    fun setupUIElements(){
        title = getString(R.string.app_name)
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(activity!!, R.color.fastlane_background)
    }
    fun loadRows(context: Context){
        //val list = NavigationPosition.values()
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val mGridPresenter = GridItemPresenter()
        val gridRowAdapter0 = ArrayObjectAdapter(mGridPresenter)
        val gridRowAdapter1 = ArrayObjectAdapter(mGridPresenter)
        val list = navList(context)

        for (nav in list.first){
            gridRowAdapter0.add(NavModel(nav.pos, nav.icon, nav.title))
        }

        for (nav in list.second){
            gridRowAdapter1.add(NavModel(nav.pos, nav.icon,nav.title))
        }

        mRowsAdapter!!.add(ListRow(HeaderItem(0, "NAV"), gridRowAdapter0))
        mRowsAdapter!!.add(ListRow(HeaderItem(1, "OTHER"), gridRowAdapter1))
        adapter = mRowsAdapter
    }

    private inner class GridItemPresenter : Presenter() {

        override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.default_background))
            view.setTextColor(Color.WHITE)
            view.gravity = Gravity.CENTER

            return Presenter.ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
            item as NavModel
            (viewHolder.view as TextView).text = item.title
        }

        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {

        }
    }

    private fun navList(context: Context): Pair<ArrayList<NavModel>, ArrayList<NavModel>>{
        val removeErrorFrag = 1
        val nav = NavigationPosition.values()
        val list = nav.copyOfRange(removeErrorFrag, nav.size)

        val first = ArrayList<NavModel>()
        for (f in 0 until NavigationPosition.SUPPORT.pos) {
            val pos = list[f].pos
            val icon = list[f].icon
            val title = list[f].title
            Log.v(XposedApp.TAG, "pos1: $pos | title: ${context.getString(title)}\n")
            //if (pos != -1)
            first.add(NavModel(pos, icon, context.getString(title)))
        }
        val second = ArrayList<NavModel>()
        for (s in NavigationPosition.SUPPORT.pos until list.size) {
            val pos = list[s].pos
            val icon = list[s].icon
            val title = list[s].title
            Log.v(XposedApp.TAG, "pos2: $pos | title: ${context.getString(title)}\n")
           // if (pos != -1)
            second.add(NavModel(pos, icon, context.getString(title)))
        }
        return Pair(first, second)
    }
}