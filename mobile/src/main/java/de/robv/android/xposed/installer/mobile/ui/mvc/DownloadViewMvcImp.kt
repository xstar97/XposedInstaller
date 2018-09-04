package de.robv.android.xposed.installer.mobile.ui.mvc

import android.content.Context
import android.database.Cursor
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CursorAdapter
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.R.id.search_view
import de.robv.android.xposed.installer.core.logic.base.BaseXposedApp
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseSettings
import de.robv.android.xposed.installer.core.logic.base.fragments.download.BaseDownload
import de.robv.android.xposed.installer.core.logic.mvc.DownloadViewMvc
import de.robv.android.xposed.installer.core.repo.RepoDbDefinitions
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.Utils
import de.robv.android.xposed.installer.mobile.logic.adapters.download.DownloadsAdapter
import kotlinx.android.synthetic.main.fragment_download.view.*
import org.jetbrains.anko.selector

class DownloadViewMvcImp(context: Context, adapter: DownloadsAdapter, layoutInflater: LayoutInflater):
        DownloadViewMvc, View.OnClickListener, PopupMenu.OnMenuItemClickListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private var mRootView = layoutInflater.inflate(R.layout.fragment_download, null)//R.layout.fragment_download, null)
    private var downloadDelegate : DownloadViewMvc.DownloadDelegate? = null
    private var myContext: Context? = context
    private var mAdapter: CursorAdapter? = adapter
    init{
         mRootView.search_view.setOnQueryTextListener(this)
        mRootView.fabDownloadActions.setOnClickListener(this)
        mRootView.listModules.setOnItemClickListener(this)
    }

    override fun getRootView() = this.mRootView!!

    override fun setDelegate(delegate: DownloadViewMvc.DownloadDelegate){
        downloadDelegate = delegate
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        downloadDelegate?.onQueryFilter(query)
        mRootView.search_view.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        downloadDelegate?.onQueryFilter(newText)
        return true
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val id = item!!.itemId
        return when(id) {
            R.id.menu_search -> {
                mRootView.search_view.visibility = if(mRootView.search_view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                return true
            }
            R.id.menu_sort -> {
                val initList = BaseDownload.downloadSortOrder(myContext!!).toList()
                myContext!!.selector(myContext!!.getString(de.robv.android.xposed.installer.core.R.string.download_sorting_title), initList) { _, i ->
                    when(i){
                        BaseDownload.prefSortStatus, BaseDownload.prefSortUpdate, BaseDownload.prefSortCreate -> {
                            BaseXposedApp.getPreferences().edit().putInt(BaseSettings.prefDownloadSort, i).apply()
                            downloadDelegate?.onSortingDialogOptionSelected(i)
                        }
                    }
                }
                return true
            } else -> false
        }
    }

    override fun onClick(view: View){
        val id = view.id
        when (id) {
            R.id.fabDownloadActions ->{
                Utils().launchMenu(myContext!!, getRootView().fabDownloadActions, R.menu.menu_download, this).show()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        try {
            val cursor = mAdapter!!.getItem(position) as Cursor
            val packageName = cursor.getString(RepoDbDefinitions.OverviewColumnsIndexes.PKGNAME)
            Log.v(XposedApp.TAG, "pkg: $packageName")
            downloadDelegate?.onModuleSelected(packageName)
        }catch (e: Exception){
            Log.e(XposedApp.TAG, e.message)
        }
    }
}
/*

val searchItem = item//menu.findItem(R.id.menu_search)
                mSearchView = searchItem.actionView as SearchView
                mSearchView!!.setIconifiedByDefault(true)
                mSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        downloadDelegate?.onQueryFilter(query)
                        mSearchView!!.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        downloadDelegate?.onQueryFilter(newText)
                        return true
                    }
                })
                @Suppress("DEPRECATION")
                (MenuItemCompat.setOnActionExpandListener(searchItem, object : MenuItemCompat.OnActionExpandListener {
                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        downloadDelegate?.onQueryFilter(null)
                        return true // Return true to collapse action view
                    }

                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        return true // Return true to expand action view
                    }
                }))

 */