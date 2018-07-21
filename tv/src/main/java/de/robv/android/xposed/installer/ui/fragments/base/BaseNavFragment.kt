package de.robv.android.xposed.installer.ui.fragments.base

import android.content.Context
import android.graphics.Color
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v4.content.ContextCompat
import de.robv.android.xposed.installer.R
import android.widget.TextView
import android.view.Gravity
import android.view.ViewGroup
import android.support.v17.leanback.widget.Presenter
import android.support.v17.leanback.widget.ListRow
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.HeaderItem
import android.support.v17.leanback.widget.ListRowPresenter

import de.robv.android.xposed.installer.logic.NavigationPosition
import de.robv.android.xposed.installer.logic.getNavCategoriesItems
import de.robv.android.xposed.installer.logic.getTitle


open class BaseNavFragment: BrowseSupportFragment()
{
    companion object {
        val baseTag: String = BaseNavFragment::class.java.simpleName
        fun newInstance() = BaseNavFragment()

        const val GRID_ITEM_WIDTH = 300
        const val GRID_ITEM_HEIGHT = 200
        var mRowsAdapter: ArrayObjectAdapter? = null
    }
    //delegateOnSelected: HeadersSupportFragment.OnHeaderViewSelectedListener, delegateOnClicked: HeadersSupportFragment.OnHeaderClickedListener
    fun setupUIElements() {
        title = getString(R.string.app_name)
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(activity!!, R.color.fastlane_background)
        //headersSupportFragment.setOnHeaderViewSelectedListener(delegateOnSelected)
        //headersSupportFragment.setOnHeaderClickedListener(delegateOnClicked)
    }

    fun loadRows(context: Context){
        val list = NavigationPosition.values()
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        for (i in 0 until list.size) {
            val gridItemPresenterHeader = HeaderItem(i.toLong(), list[i].getTitle(context))
            val item = NavigationPosition.values()[i].getNavCategoriesItems()

            val mGridPresenter = GridItemPresenter()
            val gridRowAdapter = ArrayObjectAdapter(mGridPresenter)
            for(c in 0 until item.size) {
                gridRowAdapter.add(item[c])
            }
            mRowsAdapter!!.add(ListRow(gridItemPresenterHeader, gridRowAdapter))
        }
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