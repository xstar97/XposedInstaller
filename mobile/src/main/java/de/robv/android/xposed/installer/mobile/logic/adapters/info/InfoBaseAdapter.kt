package de.robv.android.xposed.installer.mobile.logic.adapters.info

import android.content.Context
import android.content.res.Resources
import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseAdapter
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import com.skydoves.baserecyclerviewadapter.SectionRow
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.delegates.InfoDelegate
import de.robv.android.xposed.installer.core.logic.models.*
import de.robv.android.xposed.installer.mobile.logic.adapters.info.viewholders.InfoBaseViewHolder
import de.robv.android.xposed.installer.mobile.logic.adapters.info.viewholders.InfoBaseViewHolderHeader

open class InfoBaseAdapter(private val context: Context?, private val delegate: InfoDelegate?): BaseAdapter() {

   // private var myContext = context
    companion object {
        const val mSectionAbout = 0
        const val mSectionSupport = 1
        const val mSectionDevice = 2
    }
    init {
        //this.myContext = context
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
            0 -> R.layout.list_item_header
            else -> R.layout.list_item_tab
        }
    }

    override fun viewHolder(layout: Int, view: View): BaseViewHolder {
        when(layout) {
            R.layout.list_item_header -> return InfoBaseViewHolderHeader(view)
            R.layout.list_item_tab -> return InfoBaseViewHolder(view, delegate!!)
        }
        throw Resources.NotFoundException("no layout founded")
    }

    private fun setSectionName(section: Int): String{
        return when (section) {
            mSectionAbout -> context!!.getString(R.string.nav_item_about)
            mSectionSupport -> context!!.getString(R.string.nav_item_support)
            mSectionDevice -> context!!.getString(R.string.framework_device_info)
            else -> "section: $section"
        }
    }
}