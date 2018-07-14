package de.robv.android.xposed.installer.logic.adapters.zip

import android.content.Context
import android.content.res.Resources
import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseAdapter
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import com.skydoves.baserecyclerviewadapter.SectionRow
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.logic.adapters.zip.viewholders.ZipBaseViewHolder
import de.robv.android.xposed.installer.logic.adapters.zip.viewholders.ZipBaseViewHolderHeader

class ZipBaseAdapter(private val context: Context, private val delegate: ZipBaseViewHolder.Delegate): BaseAdapter() {

    private var myContext = context

    val SECTION_INSTALL = 0
    val SECTION_UNINSTALL = 1

    init {
        this.myContext = context
        for(i in 0..2) {
            addSection(ArrayList<Any>())
        }
    }

    fun addZipItems(section: Int, items: ArrayList<ZipModel>) {
        addItemOnSection(section, setSectionName(section))
        addItemsOnSection(section, items)
        notifyDataSetChanged()
    }

    override fun layout(sectionRow: SectionRow): Int {
        return when(sectionRow.row()) {
            0 -> R.layout.list_item_framework_zip_header
            else -> R.layout.list_item_framework_zip
        }
    }

    override fun viewHolder(layout: Int, view: View): BaseViewHolder {
        when(layout) {
            R.layout.list_item_framework_zip_header -> return ZipBaseViewHolderHeader(view)
            R.layout.list_item_framework_zip -> return ZipBaseViewHolder(view, delegate)
        }
        throw Resources.NotFoundException("no layout founded")
    }

    private fun setSectionName(section: Int): String{
        return when (section) {
            SECTION_INSTALL -> context.getString(R.string.install_update)
            SECTION_UNINSTALL -> context.getString(R.string.uninstall)
            else -> "section: $section"
        }
    }
}