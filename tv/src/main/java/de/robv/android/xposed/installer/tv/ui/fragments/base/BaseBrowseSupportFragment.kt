package de.robv.android.xposed.installer.tv.ui.fragments.base

import android.database.Cursor
import android.graphics.Color
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.models.NavModel
import de.robv.android.xposed.installer.core.models.ZipModel
import de.robv.android.xposed.installer.core.repo.RepoDbDefinitions

open class BaseBrowseSupportFragment: BrowseSupportFragment(), OnItemViewSelectedListener, OnItemViewClickedListener
{
    companion object {
        val baseTag: String = BaseBrowseSupportFragment::class.java.simpleName
        fun newInstance() = BaseBrowseSupportFragment()

        const val GRID_ITEM_WIDTH = 300
        const val GRID_ITEM_HEIGHT = 200
        var mRowsAdapter: ArrayObjectAdapter? = null
    }
    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setupUIElements(myTitle: String){
        title = myTitle
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(activity!!, R.color.fastlane_background)
    }

    inner class GridNavPresenter : Presenter() {

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
    inner class GridFrameworkPresenter : Presenter() {

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
            item as ZipModel
            (viewHolder.view as TextView).text = item.key//title:/
        }

        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {

        }
    }
    inner class GridCursorPresenter : Presenter() {

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
            item as Cursor
            (viewHolder.view as TextView).text = item.getString(RepoDbDefinitions.OverviewColumnsIndexes.TITLE)
        }

        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {

        }
    }
}