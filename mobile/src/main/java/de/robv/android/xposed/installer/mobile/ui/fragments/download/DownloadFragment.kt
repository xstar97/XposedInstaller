package de.robv.android.xposed.installer.mobile.ui.fragments.download

import android.support.v4.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FilterQueryProvider

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseSettings
import de.robv.android.xposed.installer.core.logic.mvc.DownloadViewMvc

import de.robv.android.xposed.installer.core.repo.RepoDb
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.ModuleUtil.InstalledModule
import de.robv.android.xposed.installer.core.util.ModuleUtil.ModuleListener
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.adapters.download.DownloadsAdapter
import de.robv.android.xposed.installer.mobile.mvc.DownloadViewMvcImp
import de.robv.android.xposed.installer.mobile.ui.activities.DownloadDetailsActivity
import kotlinx.android.synthetic.main.fragment_download.view.*
import kotlinx.android.synthetic.main.view_state.view.*
import se.emilsjolander.stickylistheaders.StickyListHeadersListView

//TODO replace list view with recyclerView and base adapter
class DownloadFragment : Fragment(), DownloadViewMvc.DownloadDelegate, Loader.Listener<RepoLoader>, ModuleListener
{
    companion object {
        val TAG: String = DownloadFragment::class.java.simpleName
        fun newInstance() = DownloadFragment()
    }

    private lateinit var mDownloadView: DownloadViewMvc
    private lateinit var mDownloadViewImp: DownloadViewMvcImp

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
        mSortingOrder = mPref!!.getInt(BaseSettings.prefDownloadSort,
                RepoDb.SORT_STATUS)
        mRepoLoader = RepoLoader.getInstance()
        mModuleUtil = ModuleUtil.getInstance()
        mAdapter = DownloadsAdapter(activity!!, mSortingOrder)
        mAdapter!!.filterQueryProvider = FilterQueryProvider { constraint -> RepoDb.queryModuleOverview(mSortingOrder, constraint) }
        //setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDownloadViewImp = DownloadViewMvcImp(activity!!, mAdapter!!, layoutInflater)
        mDownloadView = mDownloadViewImp
        mDownloadViewImp.setDelegate(this)
        return mDownloadView.getRootView()
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        mRefreshHint = mDownloadViewImp.getRootView().refresh_hint//mDownloadViewImp.getRootView().findViewById(R.id.refresh_hint)
        val mRefreshHintIcon = mDownloadViewImp.getRootView().view_state_icon
        mRefreshHintIcon.setImageResource(R.drawable.ic_menu_refresh)
        val mRefreshHintTitle = mDownloadViewImp.getRootView().view_state_title
        mRefreshHintTitle.text = activity!!.getString(R.string.update_download_list)
        val refreshLayout = mDownloadViewImp.getRootView().swiperefreshlayout//mDownloadViewImp.getRootView().findViewById(R.id.swiperefreshlayout) as SwipeRefreshLayout
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(activity!!,R.color.colorPrimary))
        mRepoLoader!!.addListener(this)
        mRepoLoader!!.setSwipeRefreshLayout(refreshLayout)
        mModuleUtil!!.addListener(this)

        mListView = mDownloadViewImp.getRootView().listModules//mDownloadViewImp.getRootView().findViewById<View>(R.id.listModules) as StickyListHeadersListView
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

        mListView!!.setOnKeyListener(View.OnKeyListener { vv, keyCode, event ->
            // Expand the search v when the SEARCH key is triggered
            if (keyCode == KeyEvent.KEYCODE_SEARCH && event.action == KeyEvent.ACTION_UP && event.flags and KeyEvent.FLAG_CANCELED == 0) {
                if (mSearchView != null)
                mSearchView!!.isIconified = false
                return@OnKeyListener true
            }
            false
        })

        //setHasOptionsMenu(true)
    }

    override fun onSortingDialogOptionSelected(sort: Int) {
        mSortingOrder = mPref!!.getInt(BaseSettings.prefDownloadSort, mSortingOrder)
        reloadItems()
    }
    override fun onSearchInit() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onModuleSelected(pkg: String?) {
        Log.v(XposedApp.TAG, "myPKG: $pkg")
        val detailsIntent = Intent(activity, DownloadDetailsActivity::class.java)
        detailsIntent.data = Uri.fromParts("package", pkg, null)
        startActivity(detailsIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRepoLoader!!.removeListener(this)
        mRepoLoader!!.setSwipeRefreshLayout(null)
        mModuleUtil!!.removeListener(this)
    }
/*
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
    }*/

    private fun setFilter(filterText: String?) {
        mFilterText = filterText
        reloadItems()
        mRefreshHint = mDownloadViewImp.getRootView().refresh_state//mDownloadViewImp.getRootView().findViewById(R.id.refresh_hint)
        mRefreshHint!!.visibility = if (TextUtils.isEmpty(filterText)) View.VISIBLE else View.GONE
    }

    private fun reloadItems() {
        mAdapter!!.filter.filter(mFilterText)
    }

    /*
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort -> {
                //BaseDownload().showSortingDialog(activity!!)
                /*
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
                        .show()*/
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    override fun onReloadDone(loader: RepoLoader) {
        reloadItems()
    }

    override fun onSingleInstalledModuleReloaded(moduleUtil: ModuleUtil, packageName: String, module: InstalledModule) {
        reloadItems()
    }

    override fun onInstalledModulesReloaded(moduleUtil: ModuleUtil) {
        reloadItems()
    }
}