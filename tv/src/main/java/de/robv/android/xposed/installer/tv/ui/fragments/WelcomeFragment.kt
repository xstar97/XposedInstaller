package de.robv.android.xposed.installer.tv.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.models.NavModel
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.logic.NavigationPosition
import de.robv.android.xposed.installer.tv.logic.findNavByPos
import de.robv.android.xposed.installer.tv.ui.activities.containers.ViewActivity
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseBrowseSupportFragment

import org.jetbrains.anko.startActivity

class WelcomeFragment : BaseBrowseSupportFragment()
{
    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        try {
            val model = item as NavModel
            val pos = model.pos
            val title = model.title

            val nav = findNavByPos(pos!!)
            activity!!.startActivity<ViewActivity>(ViewActivity.INTENT_NAV_KEY to nav)
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
    private fun navList(context: Context): Pair<ArrayList<NavModel>, ArrayList<NavModel>>{
        val removeErrorFrag = 1
        val nav = NavigationPosition.values()
        val list = nav.copyOfRange(removeErrorFrag, nav.size)

        val first = ArrayList<NavModel>()
        for (f in 0 until NavigationPosition.SUPPORT.pos) {
            val pos = list[f].pos
            val icon = ContextCompat.getDrawable(activity!!, list[f].icon)
            val title = context.getString(list[f].title)
            Log.v(XposedApp.TAG, "pos1: $pos | title: $title")
            first.add(NavModel(pos, icon, title))
        }
        val second = ArrayList<NavModel>()
        for (s in NavigationPosition.SUPPORT.pos until list.size) {
            val pos = list[s].pos
            val icon = ContextCompat.getDrawable(activity!!, list[s].icon)
            val title = context.getString(list[s].title)
            Log.v(XposedApp.TAG, "pos2: $pos | title: $title")
            // if (pos != -1)
            second.add(NavModel(pos, icon, title))
        }
        return Pair(first, second)
    }
}