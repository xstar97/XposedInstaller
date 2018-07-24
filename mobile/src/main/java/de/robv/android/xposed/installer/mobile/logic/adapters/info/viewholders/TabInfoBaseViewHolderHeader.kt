package de.robv.android.xposed.installer.mobile.logic.adapters.info.viewholders

import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import kotlinx.android.synthetic.main.list_item_tab_header.view.*

class TabInfoBaseViewHolderHeader(view: View): BaseViewHolder(view) {

    override fun bindData(data: Any) {
        if(data is String) {
            itemView.list_item_tab_header.text = data
        }
    }

    override fun onClick(v: View?) {
    }

    override fun onLongClick(v: View?) = false
}