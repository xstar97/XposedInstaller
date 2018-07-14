package de.robv.android.xposed.installer.logic.adapters.info.viewholders

import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoModel
import kotlinx.android.synthetic.main.list_item_tab.view.*

class TabInfoBaseViewHolder(view: View, private val delegate: Delegate) : BaseViewHolder(view) {

    private lateinit var infoItem: TabInfoModel

    interface Delegate {
        fun onItemClick(infoItem: TabInfoModel)
    }

    override fun bindData(data: Any) {
        if(data is TabInfoModel) {
            infoItem = data
            drawItem()
        }
    }

    private fun drawItem() {
        itemView.run {
            list_item_tab_icon.setBackgroundResource(infoItem.icon)
            list_item_tab_key.text = infoItem.key
            list_item_tab_description.text = infoItem.desciption
        }
    }

    override fun onClick(v: View?) {
        delegate.onItemClick(this.infoItem)
    }

    override fun onLongClick(v: View?) = false
}