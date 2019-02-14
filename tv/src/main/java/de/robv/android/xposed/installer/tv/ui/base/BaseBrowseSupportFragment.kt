package de.robv.android.xposed.installer.tv.ui.base

import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.core.content.ContextCompat
import de.robv.android.xposed.installer.R

open class BaseBrowseSupportFragment: BrowseSupportFragment(), OnItemViewSelectedListener, OnItemViewClickedListener
{
    companion object {
        val baseTag: String = BaseBrowseSupportFragment::class.java.simpleName
        fun newInstance() = BaseBrowseSupportFragment()

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
}