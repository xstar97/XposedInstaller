package de.robv.android.xposed.installer.mobile.logic.adapters.download

import android.content.Context
import android.database.Cursor
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.repo.RepoDb
import de.robv.android.xposed.installer.core.repo.RepoDbDefinitions
import de.robv.android.xposed.installer.mobile.logic.ThemeUtil
import kotlinx.android.synthetic.main.list_item_download.view.*
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter
import java.text.DateFormat
import java.util.*

class DownloadsAdapter(private val mContext: Context, sortingOrder: Int) : CursorAdapter(mContext, null, 0), StickyListHeadersAdapter
{
    private val mDateFormatter = DateFormat.getDateInstance(DateFormat.SHORT)
    private val mInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val sectionHeadersStatus: Array<String>
    private val sectionHeadersDate: Array<String>
    private var  mSortingOrder = 0

    init {
        mSortingOrder = sortingOrder
        val res = mContext.resources
        sectionHeadersStatus = arrayOf(res.getString(R.string.download_section_framework), res.getString(R.string.download_section_update_available), res.getString(R.string.download_section_installed), res.getString(R.string.download_section_not_installed))
        sectionHeadersDate = arrayOf(res.getString(R.string.download_section_24h), res.getString(R.string.download_section_7d), res.getString(R.string.download_section_30d), res.getString(R.string.download_section_older))
    }

    @Suppress("NAME_SHADOWING")
    override fun getHeaderView(position: Int, convertView: View?, parent: ViewGroup): View {
        var myConvertView = convertView
         if (myConvertView == null) {
            myConvertView = mInflater.inflate(R.layout.list_item_header, parent, false)
        }

        val section = getHeaderId(position)

        val tv = myConvertView!!.findViewById<View>(R.id.list_item_header) as TextView
        tv.text = if (mSortingOrder == RepoDb.SORT_STATUS)
            sectionHeadersStatus[section.toInt()]
        else
            sectionHeadersDate[section.toInt()]
        return myConvertView
    }

    override fun getHeaderId(position: Int): Long {
        val cursor = getItem(position) as Cursor
        val created = cursor.getLong(RepoDbDefinitions.OverviewColumnsIndexes.CREATED)
        val updated = cursor.getLong(RepoDbDefinitions.OverviewColumnsIndexes.UPDATED)
        val isFramework = cursor.getInt(RepoDbDefinitions.OverviewColumnsIndexes.IS_FRAMEWORK) > 0
        val isInstalled = cursor.getInt(RepoDbDefinitions.OverviewColumnsIndexes.IS_INSTALLED) > 0
        val hasUpdate = cursor.getInt(RepoDbDefinitions.OverviewColumnsIndexes.HAS_UPDATE) > 0

        if (mSortingOrder != RepoDb.SORT_STATUS) {
            val timestamp = if (mSortingOrder == RepoDb.SORT_UPDATED) updated else created
            val age = System.currentTimeMillis() - timestamp
            val mSecsPerDay = 24 * 60 * 60 * 1000L
            if (age < mSecsPerDay)
                return 0
            if (age < 7 * mSecsPerDay)
                return 1
            return if (age < 30 * mSecsPerDay) 2 else 3
        } else {
            if (isFramework)
                return 0

            return when {
                hasUpdate -> 1
                isInstalled -> 2
                else -> 3
            }
        }
    }

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
       return mInflater.inflate(R.layout.list_item_download, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val title = cursor.getString(RepoDbDefinitions.OverviewColumnsIndexes.TITLE)
        val summary = cursor.getString(RepoDbDefinitions.OverviewColumnsIndexes.SUMMARY)
        val installedVersion = cursor.getString(RepoDbDefinitions.OverviewColumnsIndexes.INSTALLED_VERSION)
        val latestVersion = cursor.getString(RepoDbDefinitions.OverviewColumnsIndexes.LATEST_VERSION)
        val created = cursor.getLong(RepoDbDefinitions.OverviewColumnsIndexes.CREATED)
        val updated = cursor.getLong(RepoDbDefinitions.OverviewColumnsIndexes.UPDATED)
        val isInstalled = cursor.getInt(RepoDbDefinitions.OverviewColumnsIndexes.IS_INSTALLED) > 0
        val hasUpdate = cursor.getInt(RepoDbDefinitions.OverviewColumnsIndexes.HAS_UPDATE) > 0

        val txtTitle = view.list_item_download_title//view.findViewById<TextView>(R.id.list_item_download_title)
        txtTitle.text = title

        val txtSummary = view.list_item_download_summary//view.findViewById<TextView>(R.id.list_item_download_summary)
        txtSummary.text = summary

        val txtStatus = view.list_item_download_status//view.findViewById<TextView>(R.id.list_item_download_status)
        val timeStamp = view.list_item_download_time
        when {
            hasUpdate -> {
                txtStatus.text = mContext.getString(
                        R.string.download_status_update_available,
                        installedVersion, latestVersion)

                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.download_status_update_available))
                txtStatus.visibility = View.VISIBLE
            }
            isInstalled -> {
                txtStatus.text = mContext.getString(
                        R.string.download_status_installed, installedVersion)
                txtStatus.setTextColor(ThemeUtil.getThemeColor(mContext, R.attr.download_status_installed))
                txtStatus.visibility = View.VISIBLE
            }
            else -> txtStatus.visibility = View.GONE
        }

        val creationDate = mDateFormatter.format(Date(created))
        val updateDate = mDateFormatter.format(Date(updated))
        //view.findViewById<TextView>(R.id.list_item_download_time)
        timeStamp.text = context.getString(R.string.download_timestamps, creationDate, updateDate)
    }
}