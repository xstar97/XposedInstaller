package de.robv.android.xposed.installer.ui.fragments

import android.support.v4.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.CursorAdapter
import android.widget.FilterQueryProvider
import android.widget.TextView

import com.afollestad.materialdialogs.MaterialDialog
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp

import java.text.DateFormat
import java.util.Date

import de.robv.android.xposed.installer.core.repo.RepoDb
import de.robv.android.xposed.installer.core.repo.RepoDbDefinitions.OverviewColumnsIndexes
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.ModuleUtil.InstalledModule
import de.robv.android.xposed.installer.core.util.ModuleUtil.ModuleListener
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.logic.ThemeUtil
import de.robv.android.xposed.installer.ui.activities.DownloadDetailsActivity
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter
import se.emilsjolander.stickylistheaders.StickyListHeadersListView

@Suppress("NAME_SHADOWING")
class DownloadFragment : Fragment(), Loader.Listener<RepoLoader>, ModuleListener
{
    companion object {
        val TAG: String = DownloadFragment::class.java.simpleName
        fun newInstance() = DownloadFragment()
    }
    private var mPref: SharedPreferences? = null
    private var mAdapter: DownloadsAdapter? = null
    private var mFilterText: String? = null
    private var mRepoLoader: RepoLoader? = null
    private var mModuleUtil: ModuleUtil? = null
    private var mSortingOrder: Int = 0
    private var mSearchView: SearchView? = null
    private var mListView: StickyListHeadersListView? = null
    private var mRefreshHint: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPref = XposedApp.getPreferences()
        mRepoLoader = RepoLoader.getInstance()
        mModuleUtil = ModuleUtil.getInstance()
        mAdapter = DownloadsAdapter(activity!!)
        mAdapter!!.filterQueryProvider = FilterQueryProvider { constraint -> RepoDb.queryModuleOverview(mSortingOrder, constraint) }
        mSortingOrder = mPref!!.getInt("download_sorting_order",
                RepoDb.SORT_STATUS)

        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (mAdapter != null && mListView != null) {
            mListView!!.adapter = mAdapter
        }
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.view_downloader, container, false)

        mRefreshHint = v.findViewById(R.id.refresh_hint)
        val refreshLayout = v.findViewById<View>(R.id.swiperefreshlayout) as SwipeRefreshLayout
        @Suppress("DEPRECATION")
        refreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        mRepoLoader!!.addListener(this)
        mRepoLoader!!.setSwipeRefreshLayout(refreshLayout)
        mModuleUtil!!.addListener(this)

        mListView = v.findViewById<View>(R.id.listModules) as StickyListHeadersListView
        if (Build.VERSION.SDK_INT >= 26) {
            mListView!!.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }
        mListView!!.adapter = mAdapter
        mListView!!.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (view.getChildAt(0) != null) {
                    refreshLayout.isEnabled = view.firstVisiblePosition == 0 && view.getChildAt(0).top == 0
                }
            }
        })
        reloadItems()

        mListView!!.setOnItemClickListener { parent, view, position, id ->
            val cursor = mAdapter!!.getItem(position) as Cursor
            val packageName = cursor.getString(OverviewColumnsIndexes.PKGNAME)

            val detailsIntent = Intent(activity, DownloadDetailsActivity::class.java)
            detailsIntent.data = Uri.fromParts("package", packageName, null)
            startActivity(detailsIntent)
        }
        mListView!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            // Expand the search view when the SEARCH key is triggered
            if (keyCode == KeyEvent.KEYCODE_SEARCH && event.action == KeyEvent.ACTION_UP && event.flags and KeyEvent.FLAG_CANCELED == 0) {
                if (mSearchView != null)
                    mSearchView!!.isIconified = false
                return@OnKeyListener true
            }
            false
        })

        setHasOptionsMenu(true)

        return v
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRepoLoader!!.removeListener(this)
        mRepoLoader!!.setSwipeRefreshLayout(null)
        mModuleUtil!!.removeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_download, menu)

        // Setup search button
        val searchItem = menu.findItem(R.id.menu_search)
        mSearchView = searchItem.actionView as SearchView
        mSearchView!!.setIconifiedByDefault(true)
        mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                setFilter(query)
                mSearchView!!.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                setFilter(newText)
                return true
            }
        })
        @Suppress("DEPRECATION")
        MenuItemCompat.setOnActionExpandListener(searchItem, object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                setFilter(null)
                return true // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true // Return true to expand action view
            }
        })
    }

    private fun setFilter(filterText: String?) {
        mFilterText = filterText
        reloadItems()
        mRefreshHint!!.visibility = if (TextUtils.isEmpty(filterText)) View.VISIBLE else View.GONE
    }

    private fun reloadItems() {
        mAdapter!!.filter.filter(mFilterText)
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort -> {
                MaterialDialog.Builder(activity!!)
                        .title(R.string.download_sorting_title)
                        .items(R.array.download_sort_order)
                        .itemsCallbackSingleChoice(mSortingOrder
                        ) { materialDialog, view, i, charSequence ->
                            mSortingOrder = i
                            mPref!!.edit().putInt("download_sorting_order", mSortingOrder).apply()
                            reloadItems()
                            materialDialog.dismiss()
                            true
                        }
                        .show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onReloadDone(loader: RepoLoader) {
        reloadItems()
    }

    override fun onSingleInstalledModuleReloaded(moduleUtil: ModuleUtil, packageName: String, module: InstalledModule) {
        reloadItems()
    }

    override fun onInstalledModulesReloaded(moduleUtil: ModuleUtil) {
        reloadItems()
    }

    private inner class DownloadsAdapter(private val mContext: Context) : CursorAdapter(mContext, null, 0), StickyListHeadersAdapter {
        private val mDateFormatter = DateFormat.getDateInstance(DateFormat.SHORT)
        private val mInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        private val sectionHeadersStatus: Array<String>
        private val sectionHeadersDate: Array<String>

        init {

            val res = mContext.resources
            sectionHeadersStatus = arrayOf(res.getString(R.string.download_section_framework), res.getString(R.string.download_section_update_available), res.getString(R.string.download_section_installed), res.getString(R.string.download_section_not_installed))
            sectionHeadersDate = arrayOf(res.getString(R.string.download_section_24h), res.getString(R.string.download_section_7d), res.getString(R.string.download_section_30d), res.getString(R.string.download_section_older))
        }

        override fun getHeaderView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_sticky_header_download, parent, false)
            }

            val section = getHeaderId(position)

            val tv = convertView!!.findViewById<View>(android.R.id.title) as TextView
            tv.text = if (mSortingOrder == RepoDb.SORT_STATUS)
                sectionHeadersStatus[section.toInt()]
            else
                sectionHeadersDate[section.toInt()]
            return convertView
        }

        override fun getHeaderId(position: Int): Long {
            val cursor = getItem(position) as Cursor
            val created = cursor.getLong(OverviewColumnsIndexes.CREATED)
            val updated = cursor.getLong(OverviewColumnsIndexes.UPDATED)
            val isFramework = cursor.getInt(OverviewColumnsIndexes.IS_FRAMEWORK) > 0
            val isInstalled = cursor.getInt(OverviewColumnsIndexes.IS_INSTALLED) > 0
            val hasUpdate = cursor.getInt(OverviewColumnsIndexes.HAS_UPDATE) > 0

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
            val title = cursor.getString(OverviewColumnsIndexes.TITLE)
            val summary = cursor.getString(OverviewColumnsIndexes.SUMMARY)
            val installedVersion = cursor.getString(OverviewColumnsIndexes.INSTALLED_VERSION)
            val latestVersion = cursor.getString(OverviewColumnsIndexes.LATEST_VERSION)
            val created = cursor.getLong(OverviewColumnsIndexes.CREATED)
            val updated = cursor.getLong(OverviewColumnsIndexes.UPDATED)
            val isInstalled = cursor.getInt(OverviewColumnsIndexes.IS_INSTALLED) > 0
            val hasUpdate = cursor.getInt(OverviewColumnsIndexes.HAS_UPDATE) > 0

            val txtTitle = view.findViewById<View>(android.R.id.text1) as TextView
            txtTitle.text = title

            val txtSummary = view.findViewById<View>(android.R.id.text2) as TextView
            txtSummary.text = summary

            val txtStatus = view.findViewById<View>(R.id.downloadStatus) as TextView
            when {
                hasUpdate -> {
                    txtStatus.text = mContext.getString(
                            R.string.download_status_update_available,
                            installedVersion, latestVersion)
                    @Suppress("DEPRECATION")
                    txtStatus.setTextColor(resources.getColor(R.color.download_status_update_available))
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
            (view.findViewById<View>(R.id.timestamps) as TextView).text = getString(R.string.download_timestamps, creationDate, updateDate)
        }
    }
}
