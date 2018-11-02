package de.robv.android.xposed.installer.mobile.ui.module

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ListFragment
import android.util.TypedValue
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.R.id.install_bookmark
import de.robv.android.xposed.installer.R.id.install_remove_bookmark
import de.robv.android.xposed.installer.R.menu.context_menu_modules_bookmark
import de.robv.android.xposed.installer.core.logic.base.fragments.download.BaseDownloadDetailsVersions
import de.robv.android.xposed.installer.core.repo.Module
import de.robv.android.xposed.installer.core.util.DownloadsUtil
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.mobile.logic.adapters.module.BookmarkModuleAdapter
import de.robv.android.xposed.installer.mobile.ui.download.DownloadDetailsActivity
import java.util.ArrayList

class ModulesBookmarkFragment : ListFragment(), AdapterView.OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener
{
    companion object {
        val TAG: String = ModulesBookmarkFragment::class.java.simpleName
        fun newInstance() = ModulesBookmarkFragment()
    }

    private var mRepoLoader: RepoLoader? = null

    private val mBookmarkedModules = ArrayList<Module>()
    private var mAdapter: BookmarkModuleAdapter? = null
    private var mBookmarksPref: SharedPreferences? = null
    private var changed: Boolean = false
    private var mClickedMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRepoLoader = RepoLoader.getInstance()

        mBookmarksPref = activity!!.getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        mBookmarksPref!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()

        if (changed)
            getModules()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBookmarksPref!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listView.divider = null
        listView.dividerHeight = getDp(6f)
        listView.setPadding(getDp(8f), getDp(8f), getDp(8f), getDp(8f))
        listView.onItemClickListener = this
        listView.clipToPadding = false
        registerForContextMenu(listView)
        setEmptyText(getString(R.string.no_bookmark_added))

        mAdapter = BookmarkModuleAdapter(activity!!)
        getModules()
        listAdapter = mAdapter

        setHasOptionsMenu(true)
    }

    private fun getModules() {
        mAdapter!!.clear()
        mBookmarkedModules.clear()
        for (s in mBookmarksPref!!.all.keys) {
            val isBookmarked = mBookmarksPref!!.getBoolean(s, false)

            if (isBookmarked) {
                val m = mRepoLoader!!.getModule(s)
                if (m != null) mBookmarkedModules.add(m)
            }
        }
        mBookmarkedModules.sortWith(Comparator { mod1, mod2 -> mod1.name.compareTo(mod2.name) })
        mAdapter!!.addAll(mBookmarkedModules)
        mAdapter!!.notifyDataSetChanged()
    }

    private fun getDp(value: Float): Int {
        val metrics = resources.displayMetrics

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics).toInt()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val detailsIntent = Intent(activity, DownloadDetailsActivity::class.java)
        detailsIntent.data = Uri.fromParts("package", mBookmarkedModules[position].packageName, null)
        startActivity(detailsIntent)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        changed = true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        val module = getItemFromContextMenuInfo(menuInfo) ?: return

        menu.setHeaderTitle(module.name)
        activity!!.menuInflater.inflate(context_menu_modules_bookmark, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val module = getItemFromContextMenuInfo(
                item.menuInfo) ?: return false

        val pkg = module.packageName
        val mv = DownloadsUtil.getStableVersion(module) ?: return false

        mClickedMenuItem = item

        when (item.itemId) {
            install_bookmark -> DownloadsUtil.addModule(activity, module.name, mv.downloadLink, BaseDownloadDetailsVersions.DownloadModuleCallback(mv))
            install_remove_bookmark -> DownloadsUtil.addModule(activity, module.name, mv.downloadLink, object : BaseDownloadDetailsVersions.DownloadModuleCallback(mv) {
                override fun onDownloadFinished(context: Context, info: DownloadsUtil.DownloadInfo) {
                    super.onDownloadFinished(context, info)
                    remove(pkg)
                }
            })
            R.id.remove -> remove(pkg)
        }

        return false
    }

    private fun remove(pkg: String) {
        mBookmarksPref!!.edit().putBoolean(pkg, false).apply()

               //snackbar(view, R.string.bookmark_removed).setAction()
        Snackbar.make(view!!, R.string.bookmark_removed, Snackbar.LENGTH_SHORT).setAction(R.string.undo) {
            mBookmarksPref!!.edit().putBoolean(pkg, true).apply()

            getModules()
        }.show()

        getModules()
    }

    private fun getItemFromContextMenuInfo(menuInfo: ContextMenu.ContextMenuInfo): Module? {
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position - listView.headerViewsCount
        return if (position >= 0) listAdapter.getItem(position) as Module else null
    }
}
