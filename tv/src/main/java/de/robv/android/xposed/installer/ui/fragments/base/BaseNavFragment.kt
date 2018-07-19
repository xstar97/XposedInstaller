package de.robv.android.xposed.installer.ui.fragments.base

import android.content.Context
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.app.HeadersFragment
import android.support.v17.leanback.app.HeadersSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.CardPresenter
import de.robv.android.xposed.installer.logic.adapters.nav.NavList

open class BaseNavFragment: BrowseSupportFragment()
{
    companion object {
        val baseTag: String = BaseNavFragment::class.java.simpleName
        fun newInstance() = BaseNavFragment()

        var listRowAdapter: ArrayObjectAdapter? = null
        var rowsAdapter: ArrayObjectAdapter? = null
        var cardPresenter: CardPresenter? = null
    }

    fun setupUIElements(delegateOnSelected: HeadersSupportFragment.OnHeaderViewSelectedListener, delegateOnClicked: HeadersSupportFragment.OnHeaderClickedListener) {
        title = getString(R.string.app_name)
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(activity!!, R.color.fastlane_background)
        headersSupportFragment.setOnHeaderViewSelectedListener(delegateOnSelected)
        headersSupportFragment.setOnHeaderClickedListener(delegateOnClicked)
    }

    fun loadRows(context: Context){
        try { //val list = MovieList.list
            val list = NavList.navCategories(context)
            val listRow = ListRowPresenter()
            rowsAdapter = ArrayObjectAdapter(listRow)
            cardPresenter = CardPresenter()
            for (i in 0 until list.size) {
                listRowAdapter = ArrayObjectAdapter(cardPresenter)
                val header = HeaderItem(i.toLong(), list[i])
                rowsAdapter!!.add(ListRow(header, listRowAdapter))
            }
            adapter = rowsAdapter
        }catch(e: Exception){
            Log.d(XposedApp.TAG, e.message)
        }
    }
}