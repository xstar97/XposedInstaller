package de.robv.android.xposed.installer.tv.ui.fragments.download

import android.content.Context
import android.os.Bundle
import android.support.v17.leanback.widget.*
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.download.BaseDownload
import de.robv.android.xposed.installer.core.repo.RepoDb
import de.robv.android.xposed.installer.core.repo.RepoDbDefinitions
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseBrowseSupportFragment

class DownloadFragment : BaseBrowseSupportFragment(), Loader.Listener<RepoLoader>, ModuleUtil.ModuleListener
{
    companion object {
        val TAG: String = DownloadFragment::class.java.simpleName
        fun newInstance() = DownloadFragment()
    }
    override fun onInstalledModulesReloaded(moduleUtil: ModuleUtil?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSingleInstalledModuleReloaded(moduleUtil: ModuleUtil?, packageName: String?, module: ModuleUtil.InstalledModule?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReloadDone(loader: RepoLoader?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        try {

        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }
    }

    override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUIElements(activity!!.getString(R.string.app_name))
        //BaseDownload.mRepoLoader!!.addListener(this)
        //BaseDownload.mModuleUtil!!.addListener(this)

        BaseDownload().initBaseDownload()

        loadRows(activity!!)
        onItemViewClickedListener = this
        onItemViewSelectedListener = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BaseDownload.mRepoLoader!!.removeListener(this)
        BaseDownload.mModuleUtil!!.removeListener(this)
    }

    private fun loadRows(context: Context) {
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val mGridPresenter = GridCursorPresenter()
        val gridRowAdapter = ArrayObjectAdapter(mGridPresenter)
        if(BaseDownload.mSortingOrder == RepoDb.SORT_STATUS){
            val headers= BaseDownload().sectionHeadersStatus(activity!!)

            val dlList =  RepoDb.queryModuleOverview(BaseDownload.mSortingOrder, "")

            //gridRowAdapter.add(dlList)
            for (h in 0 until headers.size) {
                mRowsAdapter!!.add(ListRow(HeaderItem(h.toLong(), headers[h]), gridRowAdapter))
            }
        } else{
            val headers= BaseDownload().sectionHeadersDate(activity!!)
            //gridRowAdapter.addAll()
            for (h in 0 until headers.size) {
                mRowsAdapter!!.add(ListRow(HeaderItem(h.toLong(), headers[h]), gridRowAdapter))
            }
        }
        adapter = mRowsAdapter
    }
}