package de.robv.android.xposed.installer.tv.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v17.leanback.widget.*
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.logic.Navigation
import de.robv.android.xposed.installer.tv.ui.activities.containers.ViewActivity
import de.robv.android.xposed.installer.tv.ui.activities.containers.ViewActivity.Companion.INTENT_NAV_KEY
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseBrowseSupportFragment
import org.jetbrains.anko.startActivity

class WelcomeFragment : BaseBrowseSupportFragment()
{
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

    companion object {
        val TAG: String = WelcomeFragment::class.java.simpleName
        fun newInstance() = WelcomeFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUIElements(activity!!.getString(R.string.app_name))

        loadRows(activity!!)
        onItemViewClickedListener = this
        onItemViewSelectedListener = this
    }

    private fun loadRows(context: Context){
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val mGridPresenter = GridNavPresenter()
        val gridRowAdapter0 = ArrayObjectAdapter(mGridPresenter)
        val gridRowAdapter1 = ArrayObjectAdapter(mGridPresenter)
        val initList= navList()

        for (nav0 in initList.first){
            gridRowAdapter0.add(nav0)
        }

        for (nav1 in initList.second){
            gridRowAdapter1.add(nav1)
        }

        mRowsAdapter!!.add(ListRow(HeaderItem(0, "NAV"), gridRowAdapter0))
        mRowsAdapter!!.add(ListRow(HeaderItem(1, "OTHER"), gridRowAdapter1))
        adapter = mRowsAdapter
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