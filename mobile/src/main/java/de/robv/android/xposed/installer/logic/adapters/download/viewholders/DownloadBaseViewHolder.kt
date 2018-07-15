package de.robv.android.xposed.installer.logic.adapters.download.viewholders

import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import de.robv.android.xposed.installer.logic.adapters.download.DownloadModel
import kotlinx.android.synthetic.main.list_item_download.view.*

class DownloadBaseViewHolder(view: View, private val delegate: Delegate) : BaseViewHolder(view) {

    private lateinit var infoItem: DownloadModel

    interface Delegate {
        fun onItemClick(infoItem: DownloadModel)
    }

    override fun bindData(data: Any) {
        if(data is DownloadModel) {
            infoItem = data
            drawItem()
        }
    }

    private fun drawItem() {
        itemView.run {
            text1.text = infoItem.title
            text2.text = infoItem.description
            downloadStatus.text = infoItem.downloadStatus
            timestamps.text = infoItem.timeStamp
        }
    }

    override fun onClick(v: View?) {
        delegate.onItemClick(this.infoItem)
    }

    override fun onLongClick(v: View?) = false
}