package de.robv.android.xposed.installer.mobile.ui.fragments.base

import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter
import de.robv.android.xposed.installer.mobile.logic.adapters.info.viewholders.InfoBaseViewHolder
import kotlinx.android.synthetic.main.fragment_view.*

open class BaseViewFragment: Fragment(), InfoBaseViewHolder.Delegate
{
    override fun onItemClick(infoItem: InfoModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun initViews(adapter: InfoBaseAdapter, section: Int, list: ArrayList<InfoModel>){
        fragment_view_recyclerView.adapter = adapter
        fragment_view_recyclerView.layoutManager = LinearLayoutManager(activity!!)
        if(adapter.itemCount == 0)
        adapter.addItems(section, list)
    }
}