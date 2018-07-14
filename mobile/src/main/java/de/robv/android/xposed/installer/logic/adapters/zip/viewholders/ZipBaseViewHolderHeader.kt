package de.robv.android.xposed.installer.logic.adapters.zip.viewholders

import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import kotlinx.android.synthetic.main.list_item_framework_zip_header.view.*

class ZipBaseViewHolderHeader(view: View): BaseViewHolder(view) {

    override fun bindData(data: Any) {
        if(data is String) {
            itemView.list_item_zip_header.text = data
        }
    }

    override fun onClick(v: View?) {
    }

    override fun onLongClick(v: View?) = false
}