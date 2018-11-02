package de.robv.android.xposed.installer.mobile.ui.list

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.delegates.InfoDelegate
import de.robv.android.xposed.installer.core.logic.models.InfoModel
import de.robv.android.xposed.installer.core.logic.mvc.BaseViewMvc
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter
import kotlinx.android.synthetic.main.fragment_list.view.*

class BaseViewMvcImp(private val context: Context, layoutInflater: LayoutInflater, private val mySection: Int, private val myList: ArrayList<InfoModel>, private var mDelegate: InfoDelegate) : BaseViewMvc//add listener
{
    private var mRootView = layoutInflater.inflate(R.layout.fragment_list, null)
    private val adapter by lazy { InfoBaseAdapter(context, mDelegate) }
    init{
        val rv = mRootView.fragment_list_rv
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)
        if(adapter.itemCount == 0)
            adapter.addItems(mySection, myList)
    }

    override fun setDelegate(delegate: InfoDelegate) {
        mDelegate = delegate
    }
    override fun getRootView() = this.mRootView!!
}