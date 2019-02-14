package de.robv.android.xposed.installer.tv.ui.main

import android.content.Context
import android.os.Bundle
import androidx.leanback.widget.*
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.logic.Navigation
import de.robv.android.xposed.installer.tv.logic.presenters.CardPresenter
import de.robv.android.xposed.installer.tv.ui.base.ViewActivity
import de.robv.android.xposed.installer.tv.ui.base.ViewActivity.Companion.INTENT_NAV_KEY
import de.robv.android.xposed.installer.tv.ui.base.BaseBrowseSupportFragment
import org.jetbrains.anko.startActivity

class WelcomeFragment : BaseBrowseSupportFragment()
{
    companion object {
        val TAG: String = WelcomeFragment::class.java.simpleName
        fun newInstance() = WelcomeFragment()
    }

    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        try {
            val nav = item as Navigation
            val pos = nav.pos
            activity!!.startActivity<ViewActivity>(INTENT_NAV_KEY to nav)
            Log.v(XposedApp.TAG, "nav -> \npos: $pos")
        }catch (e: Exception){
            Log.d(TAG, e.message)
        }
    }

    override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUIElements(activity!!.getString(R.string.app_name))

        loadHeaderRows(activity!!)
        onItemViewClickedListener = this
        onItemViewSelectedListener = this
    }

    @Suppress("UNUSED_PARAMETER")
    private fun loadHeaderRows(context: Context){
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val mCardPresenter = CardPresenter(activity!!)
        val gridRowAdapter0 = ArrayObjectAdapter(mCardPresenter)
        val gridRowAdapter1 = ArrayObjectAdapter(mCardPresenter)

        loadChildRows(gridRowAdapter0, gridRowAdapter1)

        mRowsAdapter!!.add(ListRow(HeaderItem(0, "NAV"), gridRowAdapter0))
        mRowsAdapter!!.add(ListRow(HeaderItem(1, "OTHER"), gridRowAdapter1))
        adapter = mRowsAdapter
    }
    private fun loadChildRows(gridRowAdapter0: ArrayObjectAdapter, gridRowAdapter1: ArrayObjectAdapter){
        val initList= navList()
        for (nav0 in initList.first){
            gridRowAdapter0.add(nav0)
        }

        for (nav1 in initList.second){
            gridRowAdapter1.add(nav1)
        }
    }

    private fun navList(): Pair<Array<Navigation>, Array<Navigation>>{
        val initList = Navigation.values()
        Log.v(XposedApp.TAG, "initSize -> ${initList.size}")
        val first = initList.copyOfRange(Navigation.NAV_HOME.pos, Navigation.NAV_SUPPORT.pos)
        Log.v(XposedApp.TAG, "firstSize -> ${first.size}")
        val second = initList.copyOfRange(Navigation.NAV_SUPPORT.pos, Navigation.NAV_SETTINGS.pos + 1)
        Log.v(XposedApp.TAG, "secondSize -> ${second.size}")
        return Pair(first, second)
    }

}