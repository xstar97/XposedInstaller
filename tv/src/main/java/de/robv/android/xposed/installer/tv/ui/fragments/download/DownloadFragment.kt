package de.robv.android.xposed.installer.tv.ui.fragments.download

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v17.leanback.widget.*
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.download.BaseDownload
import de.robv.android.xposed.installer.core.repo.RepoDb
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.logic.Navigation
import de.robv.android.xposed.installer.tv.ui.activities.containers.ViewActivity
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseBrowseSupportFragment
import org.jetbrains.anko.selector
import org.jetbrains.anko.startActivity

class DownloadFragment : BaseBrowseSupportFragment(), Loader.Listener<RepoLoader>, ModuleUtil.ModuleListener,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    companion object {
        val TAG: String = DownloadFragment::class.java.simpleName
        fun newInstance() = DownloadFragment()
        const val PREF_SORT = "download_sorting_order"
    }
    private val prefSortStatus = 0
    private val prefSortUpdate = 1
    private val prefSortCreate = 2

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
            when(item){
                is Navigation -> {
                    showPrefDialog()
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }
    }

    override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sharedPreferences = XposedApp.getPreferences()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        setupUIElements(activity!!.getString(R.string.app_name))
        //BaseDownload.mRepoLoader!!.addListener(this)
        //BaseDownload.mModuleUtil!!.addListener(this)

        BaseDownload().initBaseDownload()

        loadRows(activity!!)
        onItemViewClickedListener = this
        onItemViewSelectedListener = this
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(XposedApp.TAG, "onSharedPreferenceChanged key: $key")
        when(key){
            DownloadSettingsFragment.PREF_SORT ->{
                activity!!.recreate()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        XposedApp.getPreferences().registerOnSharedPreferenceChangeListener(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        XposedApp.getPreferences().unregisterOnSharedPreferenceChangeListener(this)
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
        var settingsPos: Int?
        if(BaseDownload.mSortingOrder == RepoDb.SORT_STATUS){
            val headers= BaseDownload().sectionHeadersStatus(activity!!)
            settingsPos = headers.size + 1
            for (h in 0 until headers.size) {
                mRowsAdapter!!.add(ListRow(HeaderItem(h.toLong(), headers[h]), gridRowAdapter))
            }
        } else if(BaseDownload.mSortingOrder == RepoDb.SORT_UPDATED){
            val headers= BaseDownload().sectionHeadersDate(activity!!)
            settingsPos = headers.size + 1
            for (h in 0 until headers.size) {
                mRowsAdapter!!.add(ListRow(HeaderItem(h.toLong(), headers[h]), gridRowAdapter))
            }
        } else{
            val headers= BaseDownload().sectionHeadersDate(activity!!)
            settingsPos = headers.size + 1
            for (h in 0 until headers.size) {
                mRowsAdapter!!.add(ListRow(HeaderItem(h.toLong(), headers[h]), gridRowAdapter))
            }
        }

        val mNavPresenter = GridNavPresenter()
        val navRowAdapter = ArrayObjectAdapter(mNavPresenter)
        navRowAdapter.add(0, Navigation.FRAG_DOWNLOAD_SETTINGS)
        mRowsAdapter!!.add(ListRow(HeaderItem(settingsPos.toLong(), activity!!.getString(R.string.nav_item_settings)), navRowAdapter))
        adapter = mRowsAdapter
    }

    private fun downloadSortOrder(): Array<String>{
        return resources.getStringArray(R.array.download_sort_order)
    }
    private fun showPrefDialog(){
        val initList = downloadSortOrder().toList()
        activity!!.selector(activity!!.getString(R.string.download_sorting_title), initList) { dialogInterface, i ->
            when(i){
                prefSortStatus -> {
                    XposedApp.getPreferences().edit().putInt(PREF_SORT, i).apply()
                }
                prefSortUpdate -> {
                    XposedApp.getPreferences().edit().putInt(PREF_SORT, i).apply()
                }
                prefSortCreate -> {
                    XposedApp.getPreferences().edit().putInt(PREF_SORT, i).apply()
                }
            }
        }
    }
}