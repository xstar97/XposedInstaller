package de.robv.android.xposed.installer.ui.fragments.base

import android.content.Context
import android.graphics.Color
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v4.content.ContextCompat
import de.robv.android.xposed.installer.R
import android.widget.TextView
import android.view.Gravity
import android.view.ViewGroup

import de.robv.android.xposed.installer.logic.NavigationPosition
import de.robv.android.xposed.installer.logic.getTitle

import android.support.v17.leanback.widget.*
import android.support.v17.leanback.widget.Presenter

open class BaseNavFragment: BrowseSupportFragment()
{
    companion object {
        val baseTag: String = BaseNavFragment::class.java.simpleName
        fun newInstance() = BaseNavFragment()

        const val GRID_ITEM_WIDTH = 300
        const val GRID_ITEM_HEIGHT = 200
        var mRowsAdapter: ArrayObjectAdapter? = null
    }

    fun setupUIElements(){
        title = getString(R.string.app_name)
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(activity!!, R.color.fastlane_background)
    }
    fun loadRows(context: Context){
        val list = NavigationPosition.values()
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val mGridPresenter = GridItemPresenter()
        val gridRowAdapter0 = ArrayObjectAdapter(mGridPresenter)
        val gridRowAdapter1 = ArrayObjectAdapter(mGridPresenter)
        for (i in 0 until 4) {
            gridRowAdapter0.add(list[i].getTitle(context))
        }
        //-1 to remove error fragment from list!
        for (i in 4 until list.size-1){
            gridRowAdapter1.add(list[i].getTitle(context))
        }
        mRowsAdapter!!.add(ListRow(HeaderItem(0, "NAV"), gridRowAdapter0))
        mRowsAdapter!!.add(ListRow(HeaderItem(1, "OTHER"), gridRowAdapter1))
        adapter = mRowsAdapter
    }

    inner class GridItemPresenter : Presenter() {

        override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.setBackgroundColor(resources.getColor(R.color.default_background))
            view.setTextColor(Color.WHITE)
            view.gravity = Gravity.CENTER
            return Presenter.ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {

        }
    }
}