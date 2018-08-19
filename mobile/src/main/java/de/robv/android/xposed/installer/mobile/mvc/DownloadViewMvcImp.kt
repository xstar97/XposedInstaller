package de.robv.android.xposed.installer.mobile.mvc

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CursorAdapter
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.base.fragments.BaseSettings
import de.robv.android.xposed.installer.core.base.fragments.download.BaseDownload
import de.robv.android.xposed.installer.core.mvc.DownloadViewMvc
import de.robv.android.xposed.installer.core.repo.RepoDbDefinitions
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.Utils
import de.robv.android.xposed.installer.mobile.logic.adapters.download.DownloadsAdapter
import kotlinx.android.synthetic.main.fragment_download.view.*
import org.jetbrains.anko.selector

class DownloadViewMvcImp(context: Context, adapter: DownloadsAdapter, layoutInflater: LayoutInflater):
        DownloadViewMvc, View.OnClickListener, PopupMenu.OnMenuItemClickListener, AdapterView.OnItemClickListener
{
    private var mRootView = layoutInflater.inflate(R.layout.fragment_download, null)
    private var downloadListener : DownloadViewMvc.DownloadDelegate? = null
    private var myContext: Context? = context
    private var mAdapter: CursorAdapter? = adapter
    init{
        mRootView.fabDownloadActions.setOnClickListener(this)
        mRootView.listModules.setOnItemClickListener(this)
    }

    override fun getRootView() = this.mRootView!!

    override fun setDelegate(delegate: DownloadViewMvc.DownloadDelegate){
        downloadListener = delegate
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val id = item!!.itemId
        return when(id) {
            R.id.menu_search -> {
                downloadListener?.onSearchInit()
                return true
            }
            R.id.menu_sort -> {
                val initList = BaseDownload.downloadSortOrder(myContext!!).toList()
                myContext!!.selector(myContext!!.getString(de.robv.android.xposed.installer.core.R.string.download_sorting_title), initList) { _, i ->
                    when(i){
                        BaseDownload.prefSortStatus, BaseDownload.prefSortUpdate, BaseDownload.prefSortCreate -> {
                            BaseXposedApp.getPreferences().edit().putInt(BaseSettings.prefDownloadSort, i).apply()
                            downloadListener?.onSortingDialogOptionSelected(i)
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
            downloadListener?.onModuleSelected(packageName)
        }catch (e: Exception){
            Log.e(XposedApp.TAG, e.message)
        }
    }
}