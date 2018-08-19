package de.robv.android.xposed.installer.tv.ui.fragments.download

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.support.v17.leanback.widget.*
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.base.fragments.BaseSettings
import de.robv.android.xposed.installer.core.base.fragments.download.BaseDownload
import de.robv.android.xposed.installer.core.mvc.DownloadViewMvc
import de.robv.android.xposed.installer.core.repo.RepoDb
import de.robv.android.xposed.installer.core.repo.RepoDbDefinitions
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.logic.Navigation
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseBrowseSupportFragment
import org.jetbrains.anko.selector

class DownloadFragment : BaseBrowseSupportFragment(), Loader.Listener<RepoLoader>,
        ModuleUtil.ModuleListener, DownloadViewMvc.DownloadDelegate
{
    companion object {
        val TAG: String = DownloadFragment::class.java.simpleName
        fun newInstance() = DownloadFragment()
        private var mSortingOrder: Int = 0
    }

    private var mPref: SharedPreferences? = null
    private var mRepoLoader: RepoLoader? = null
    private var mModuleUtil: ModuleUtil? = null

    private var downloadListener : DownloadViewMvc.DownloadDelegate? = null

    override fun onInstalledModulesReloaded(moduleUtil: ModuleUtil?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onSingleInstalledModuleReloaded(moduleUtil: ModuleUtil?, packageName: String?, module: ModuleUtil.InstalledModule?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onReloadDone(loader: RepoLoader?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onModuleSelected(pkg: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onSearchInit() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onSortingDialogOptionSelected(sort: Int) {

        mSortingOrder = mPref!!.getInt(BaseSettings.prefDownloadSort, mSortingOrder)
        //reloadItems()
        loadRows(activity!!)
    }


    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        try {
            when(item){
                is Navigation -> {
                    val initList = BaseDownload.downloadSortOrder(activity!!).toList()
                    activity!!.selector(activity!!.getString(de.robv.android.xposed.installer.core.R.string.download_sorting_title), initList) { _, i ->
                        when(i){
                            BaseDownload.prefSortStatus, BaseDownload.prefSortUpdate, BaseDownload.prefSortCreate -> {
                                BaseXposedApp.getPreferences().edit().putInt(BaseSettings.prefDownloadSort, i).apply()
                                downloadListener?.onSortingDialogOptionSelected(i)
                            }
                        }
                    }
                }
                else -> {
                    try {
                        val cursor = item as Cursor//mAdapter!!//.getItem(position) as Cursor
                        val packageName = cursor.getString(RepoDbDefinitions.OverviewColumnsIndexes.PKGNAME)
                        Log.v(XposedApp.TAG, "pkg: $packageName")
                        downloadListener?.onModuleSelected(packageName)
                    }catch (e: Exception){
                        Log.e(XposedApp.TAG, e.message)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(XposedApp.TAG, e.message)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPref = XposedApp.getPreferences()
        mSortingOrder = mPref!!.getInt(BaseSettings.prefDownloadSort,
                RepoDb.SORT_STATUS)
        mRepoLoader = RepoLoader.getInstance()
        mModuleUtil = ModuleUtil.getInstance()

        setupUIElements(activity!!.getString(R.string.app_name))

        mRepoLoader!!.addListener(this)
        mModuleUtil!!.addListener(this)
        loadRows(activity!!)
        downloadListener = this
        onItemViewClickedListener = this
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRepoLoader!!.removeListener(this)
        mModuleUtil!!.removeListener(this)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun loadRows(context: Context) {
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val mGridPresenter = GridCursorPresenter()
        val gridRowAdapter = ArrayObjectAdapter(mGridPresenter)
        val settingsPos: Int?
        when {
            BaseDownload.mSortingOrder == RepoDb.SORT_STATUS -> {
                val headers= BaseDownload().sectionHeadersStatus(activity!!)
                settingsPos = headers.size + 1
                for (h in 0 until headers.size) {
                    mRowsAdapter!!.add(ListRow(HeaderItem(h.toLong(), headers[h]), gridRowAdapter))
                }
            }
            BaseDownload.mSortingOrder == RepoDb.SORT_UPDATED -> {
                val headers= BaseDownload().sectionHeadersDate(activity!!)
                settingsPos = headers.size + 1
                for (h in 0 until headers.size) {
                    mRowsAdapter!!.add(ListRow(HeaderItem(h.toLong(), headers[h]), gridRowAdapter))
                }
            }
            else -> {
                val headers= BaseDownload().sectionHeadersDate(activity!!)
                settingsPos = headers.size + 1
                for (h in 0 until headers.size) {
                    mRowsAdapter!!.add(ListRow(HeaderItem(h.toLong(), headers[h]), gridRowAdapter))
                }
            }
        }

        val mNavPresenter = GridNavPresenter()
        val navRowAdapter = ArrayObjectAdapter(mNavPresenter)
        navRowAdapter.add(0, Navigation.FRAG_DOWNLOAD_SETTINGS)
        mRowsAdapter!!.add(ListRow(HeaderItem(settingsPos.toLong(), "other"), navRowAdapter))
        adapter = mRowsAdapter
    }
}