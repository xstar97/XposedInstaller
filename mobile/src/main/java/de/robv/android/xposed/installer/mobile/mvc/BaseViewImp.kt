package de.robv.android.xposed.installer.mobile.mvc

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.models.InfoDelegate
import de.robv.android.xposed.installer.core.mvc.BaseView
import kotlinx.android.synthetic.main.fragment_view.view.*

class BaseViewImp(layoutInflater: LayoutInflater) : BaseView
{
    private var mRootView = layoutInflater.inflate(R.layout.fragment_view, null)
    private var itemDelegate : InfoDelegate? = null
    init{
        mRootView.fragment_view_recyclerView as RecyclerView
    }
    override fun getRootView() = this.mRootView

    override fun setDelegate(delegate: InfoDelegate){
        itemDelegate = delegate
    }
}