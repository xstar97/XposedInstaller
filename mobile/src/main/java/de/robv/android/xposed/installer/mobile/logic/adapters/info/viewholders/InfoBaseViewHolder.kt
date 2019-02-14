package de.robv.android.xposed.installer.mobile.logic.adapters.info.viewholders

import androidx.core.content.ContextCompat
import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.delegates.InfoDelegate
import de.robv.android.xposed.installer.core.logic.models.*
import kotlinx.android.synthetic.main.list_item_tab.view.*

class InfoBaseViewHolder(view: View, private val delegate: InfoDelegate) : BaseViewHolder(view) {

    private lateinit var infoItem: InfoModel

    override fun bindData(data: Any) {
        if(data is InfoModel) {
            infoItem = data
            drawItem()
        }
    }

    private fun drawItem() {
        itemView.run {
            val colorWarning = ContextCompat.getColor(context, R.color.warning)

            when{
                infoItem.key == resources.getString(R.string.verified_boot_active) ->{
                    list_item_tab_key.setTextColor(colorWarning)
                }
                infoItem.desciption == resources.getString(R.string.verified_boot_explanation) ->{
                    list_item_tab_description.setTextColor(colorWarning)
                }
                infoItem.key.isEmpty() -> list_item_tab_key.visibility = View.GONE
                infoItem.desciption.isEmpty() -> list_item_tab_description.visibility = View.GONE
            }
            list_item_tab_icon.setImageDrawable(infoItem.icon)
            list_item_tab_key.text = infoItem.key
            list_item_tab_description.text = infoItem.desciption
        }
    }

    override fun onClick(v: View?) {
        delegate.onItemClick(this.infoItem)
    }

    override fun onLongClick(v: View?) = false
}