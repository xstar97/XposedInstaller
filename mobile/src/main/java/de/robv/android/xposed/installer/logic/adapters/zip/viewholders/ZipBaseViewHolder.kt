package de.robv.android.xposed.installer.logic.adapters.zip.viewholders

import android.graphics.Color
import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import de.robv.android.xposed.installer.core.util.FrameworkZips
import de.robv.android.xposed.installer.logic.adapters.zip.ZipModel
import kotlinx.android.synthetic.main.list_item_framework_zip.view.*

class ZipBaseViewHolder(view: View, private val delegate: Delegate) : BaseViewHolder(view) {

    private lateinit var infoItem: ZipModel

    interface Delegate {

        fun onItemClick(infoItem: ZipModel)
    }

    override fun bindData(data: Any) {
        if(data is ZipModel) {
            infoItem = data
            drawItem()
        }
    }

    private fun drawItem() {
        itemView.run {
            /*if (isOutDated){
                val gray = Color.parseColor("#A0A0A0")
                list_item_zip_title.setTextColor(gray)
                list_item_zip_status.setColorFilter(gray)
            } else {*/
                list_item_zip_title.text = infoItem.key
                list_item_zip_status.setBackgroundResource(infoItem.icon)
        }
    }

    override fun onClick(v: View?) {
        delegate.onItemClick(this.infoItem)
    }

    override fun onLongClick(v: View?) = false
}