package de.robv.android.xposed.installer.logic.adapters.download

import android.content.res.Resources
import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseAdapter
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import com.skydoves.baserecyclerviewadapter.SectionRow
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.logic.adapters.download.viewholders.DownloadBaseViewHolder
import de.robv.android.xposed.installer.logic.adapters.download.viewholders.DownloadBaseViewHolderHeader
import android.content.Context

class DownloadBaseAdapter(private val context: Context, private val delegate: DownloadBaseViewHolder.Delegate): BaseAdapter() {

    private var myContext = context

    val SECTION_FRAMEWORK = 0
    val SECTION_UPDATE = 1
    val SECTION_NOT_INSTALLED = 2

    val SECTION_DATE_24_HOURS = 3
    val SECTION_DATE_7_DAYS = 4
    val SECTION_DATE_30_DAYS = 5
    val SECTION_DATE_OLDEST = 6


    init {
        this.myContext = context
        for(i in 0..4) {
            addSection(ArrayList<DownloadModel>())
        }
    }

    fun addItems(section: Int, items: ArrayList<DownloadModel>) {
        addItemOnSection(section, setSectionName(section))
        addItemsOnSection(section, items)
        notifyDataSetChanged()
    }

    override fun layout(sectionRow: SectionRow): Int {
        return when(sectionRow.row()) {
            0 -> R.layout.list_item_download_header
            else -> R.layout.list_item_download
        }
    }

    override fun viewHolder(layout: Int, view: View): BaseViewHolder {
        when(layout) {
            R.layout.list_item_download_header -> return DownloadBaseViewHolderHeader(view)
            R.layout.list_item_download -> return DownloadBaseViewHolder(view, delegate)
        }
        throw Resources.NotFoundException("no layout founded")
    }

    private fun setSectionName(section: Int): String{
        return when (section) {
            SECTION_FRAMEWORK -> context.getString(R.string.download_section_framework)
            SECTION_UPDATE -> context.getString(R.string.download_section_update_available)
            SECTION_NOT_INSTALLED -> context.getString(R.string.download_section_not_installed)
            SECTION_DATE_24_HOURS -> context.getString(R.string.download_section_24h)
            SECTION_DATE_7_DAYS -> context.getString(R.string.download_section_7d)
            SECTION_DATE_30_DAYS -> context.getString(R.string.download_section_30d)
            SECTION_DATE_OLDEST -> context.getString(R.string.download_section_older)
            else -> "section: $section"
        }
    }
}