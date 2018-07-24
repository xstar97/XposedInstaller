package de.robv.android.xposed.installer.mobile.logic.adapters.info

import android.content.Context
import android.content.res.Resources
import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseAdapter
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import com.skydoves.baserecyclerviewadapter.SectionRow
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.viewholders.TabInfoBaseViewHolder
import de.robv.android.xposed.installer.mobile.logic.adapters.info.viewholders.TabInfoBaseViewHolderHeader

class TabInfoBaseAdapter(private val context: Context, private val delegate: TabInfoBaseViewHolder.Delegate): BaseAdapter() {

    private var myContext = context

    val SECTION_ABOUT = 0
    val SECTION_SUPPORT = 1
    val SECTION_DEVICE = 2

    init {
        this.myContext = context
        for(i in 0..2) {
            addSection(ArrayList<InfoModel>())
        }
    }

    fun addItems(section: Int, items: ArrayList<InfoModel>) {
        addItemOnSection(section, setSectionName(section))
        addItemsOnSection(section, items)
        notifyDataSetChanged()
    }

    override fun layout(sectionRow: SectionRow): Int {
        return when(sectionRow.row()) {
            0 -> R.layout.list_item_tab_header
            else -> R.layout.list_item_tab
        }
    }

    override fun viewHolder(layout: Int, view: View): BaseViewHolder {
        when(layout) {
            R.layout.list_item_tab_header -> return TabInfoBaseViewHolderHeader(view)
            R.layout.list_item_tab -> return TabInfoBaseViewHolder(view, delegate)
        }
        throw Resources.NotFoundException("no layout founded")
    }

    private fun setSectionName(section: Int): String{
        return when (section) {
            SECTION_ABOUT -> context.getString(R.string.nav_item_about)
            SECTION_SUPPORT -> context.getString(R.string.nav_item_support)
            SECTION_DEVICE -> context.getString(R.string.framework_device_info)
            else -> "section: $section"
        }
    }
}