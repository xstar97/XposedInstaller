package de.robv.android.xposed.installer.mobile.ui.fragments.base

import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import de.robv.android.xposed.installer.core.models.InfoDelegate
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.core.mvc.BaseView
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter
import de.robv.android.xposed.installer.mobile.mvc.BaseViewImp
import kotlinx.android.synthetic.main.fragment_view.*

open class BaseViewFragment: Fragment(), InfoDelegate
{
    override fun onItemClick(infoItem: InfoModel) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val adapter by lazy { InfoBaseAdapter(activity!!, this) }
    lateinit var mBaseView : BaseView
    lateinit var mBaseViewImp : BaseViewImp

    fun initView(): View {
        mBaseViewImp = BaseViewImp(layoutInflater)
        mBaseView = mBaseViewImp
        return mBaseView.getRootView()
    }

    open fun initList(section: Int, list: ArrayList<InfoModel>){
        fragment_view_recyclerView.adapter = adapter
        fragment_view_recyclerView.layoutManager = LinearLayoutManager(context)
        if(adapter.itemCount == 0)
            adapter.addItems(section, list)
    }
}