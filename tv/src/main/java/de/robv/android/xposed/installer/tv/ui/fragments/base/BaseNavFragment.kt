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
import de.robv.android.xposed.installer.tv.logic.getTitle

import android.support.v17.leanback.widget.*
import android.support.v17.leanback.widget.Presenter
import de.robv.android.xposed.installer.core.models.NavModel

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
            gridRowAdapter0.add(NavModel(nav.pos, nav.title))
        }

        for (nav in list.second){
            gridRowAdapter1.add(NavModel(nav.pos,nav.title))
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
            //view.setBackgroundColor(resources.getColor(R.color.default_background))
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
        //this implementation allows the list to start from index: 0 to end of the list, but give the nav items their current positions
        //from NavigationPosition...
        val list = NavigationPosition.values()
        val first = ArrayList<NavModel>()
        for (f in 0 until 4) {
            first.add(NavModel(f, list[f].getTitle(context)))
        }
        val second = ArrayList<NavModel>()
        for (s in 4 until list.size) {
            second.add(NavModel(s, list[s].getTitle(context)))
        }
        return Pair(first, second)
    }
}