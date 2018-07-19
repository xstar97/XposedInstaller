package de.robv.android.xposed.installer.ui.activities

import android.app.ListFragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import de.robv.android.xposed.installer.R

import java.util.ArrayList

import de.robv.android.xposed.installer.core.repo.Module
import de.robv.android.xposed.installer.core.util.DownloadsUtil
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.logic.ThemeUtil
import de.robv.android.xposed.installer.ui.fragments.Download.DownloadDetailsVersionsFragment

class ModulesBookmark : XposedBaseActivity() {

    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        ThemeUtil.setTheme(this)
        setContentView(R.layout.activity_container)

        mRepoLoader = RepoLoader.getInstance()

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { finish() }

        val ab = supportActionBar
        if (ab != null) {
            ab.setTitle(R.string.bookmarks)
            ab.setDisplayHomeAsUpEnabled(true)
        }

        setFloating(toolbar, 0)

        container = findViewById(R.id.container)

        if (savedInstanceBundle == null) {
            fragmentManager.beginTransaction().add(R.id.container, ModulesBookmarkFragment()).commit()
        }
    }

    class ModulesBookmarkFragment : ListFragment(), AdapterView.OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

        private val mBookmarkedModules = ArrayList<Module>()
        private var mAdapter: BookmarkModuleAdapter? = null
        private var mBookmarksPref: SharedPreferences? = null
        private var changed: Boolean = false
        private var mClickedMenuItem: MenuItem? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            mBookmarksPref = activity.getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
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

            mAdapter = BookmarkModuleAdapter(activity)
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
            activity.menuInflater.inflate(R.menu.context_menu_modules_bookmark, menu)
        }

        override fun onContextItemSelected(item: MenuItem): Boolean {
            val module = getItemFromContextMenuInfo(
                    item.menuInfo) ?: return false

            val pkg = module.packageName
            val mv = DownloadsUtil.getStableVersion(module) ?: return false

            mClickedMenuItem = item

            when (item.itemId) {
                R.id.install_bookmark -> DownloadsUtil.addModule(activity, module.name, mv.downloadLink, DownloadDetailsVersionsFragment.DownloadModuleCallback(mv))
                R.id.install_remove_bookmark -> DownloadsUtil.addModule(activity, module.name, mv.downloadLink, object : DownloadDetailsVersionsFragment.DownloadModuleCallback(mv) {
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

            Snackbar.make(container!!, R.string.bookmark_removed, Snackbar.LENGTH_SHORT).setAction(R.string.undo) {
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

    private class BookmarkModuleAdapter(context: Context) : ArrayAdapter<Module>(context, R.layout.list_item_module, R.id.title) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)

            view.findViewById<View>(R.id.checkbox).visibility = View.GONE
            view.findViewById<View>(R.id.version_name).visibility = View.GONE
            view.findViewById<View>(R.id.icon).visibility = View.GONE

            val item = getItem(position)

            (view.findViewById<View>(R.id.title) as TextView).text = item!!.name
            (view.findViewById<View>(R.id.description) as TextView).text = item.summary

            return view
        }
    }

    companion object {

        private var mRepoLoader: RepoLoader? = null
        private var container: View? = null
    }
}
