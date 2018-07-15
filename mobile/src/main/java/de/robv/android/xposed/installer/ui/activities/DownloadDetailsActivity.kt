@file:Suppress("DEPRECATION")

package de.robv.android.xposed.installer.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import de.robv.android.xposed.installer.ui.fragments.DownloadDetailsVersionsFragment
import de.robv.android.xposed.installer.ui.fragments.ModulesFragment
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.repo.Module
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.ModuleUtil.InstalledModule
import de.robv.android.xposed.installer.core.util.ModuleUtil.ModuleListener
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.logic.ThemeUtil
import de.robv.android.xposed.installer.ui.fragments.DownloadDetailsFragment
import de.robv.android.xposed.installer.ui.fragments.DownloadDetailsSettingsFragment

class DownloadDetailsActivity : XposedBaseActivity(), Loader.Listener<RepoLoader>, ModuleListener
{
    private var mPager: ViewPager? = null
    private var mPackageName: String? = null
    var module: Module? = null
        private set
    var installedModule: InstalledModule? = null
        private set
    private val mItemBookmark: MenuItem? = null

    private val modulePackageName: String?
        get() {
            val uri = intent.data ?: return null

            val scheme = uri.scheme
            if (TextUtils.isEmpty(scheme)) {
                return null
            } else if (scheme == "package") {
                return uri.schemeSpecificPart
            } else if (scheme == "http") {
                val segments = uri.pathSegments
                if (segments.size > 1)
                    return segments[1]
            }
            return null
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtil.setTheme(this)

        mPackageName = modulePackageName
        module = sRepoLoader.getModule(mPackageName)

        installedModule = ModuleUtil.getInstance().getModule(mPackageName)

        super.onCreate(savedInstanceState)
        sRepoLoader.addListener(this)
        sModuleUtil.addListener(this)

        if (module != null) {
            setContentView(R.layout.activity_download_details)

            val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
            setSupportActionBar(toolbar)

            toolbar.setNavigationOnClickListener { finish() }

            val ab = supportActionBar

            if (ab != null) {
                ab.setTitle(R.string.nav_item_download)
                ab.setDisplayHomeAsUpEnabled(true)
            }

            setFloating(toolbar, 0)

            setupTabs()

            val directDownload = intent.getBooleanExtra("direct_download", false)
            // Updates available => start on the versions page
            if (installedModule != null && installedModule!!.isUpdate(sRepoLoader.getLatestVersion(module)) || directDownload)
                mPager!!.currentItem = DOWNLOAD_VERSIONS

            if (Build.VERSION.SDK_INT >= 21)
                findViewById<View>(R.id.fake_elevation).visibility = View.GONE

        } else {
            setContentView(R.layout.activity_download_details_not_found)

            val txtMessage = findViewById<View>(android.R.id.message) as TextView
            txtMessage.text = resources.getString(R.string.download_details_not_found, mPackageName)

            findViewById<View>(R.id.reload).setOnClickListener { v ->
                v.isEnabled = false
                sRepoLoader.triggerReload(true)
            }
        }
    }

    private fun setupTabs() {
        mPager = findViewById<View>(R.id.download_pager) as ViewPager
        mPager!!.adapter = SwipeFragmentPagerAdapter(supportFragmentManager)
        val mTabLayout = findViewById<View>(R.id.sliding_tabs) as TabLayout
        mTabLayout.setupWithViewPager(mPager)
    }

    override fun onDestroy() {
        super.onDestroy()
        sRepoLoader.removeListener(this)
        sModuleUtil.removeListener(this)
    }

    fun gotoPage(page: Int) {
        mPager!!.currentItem = page
    }

    private fun reload() {
        runOnUiThread { recreate() }
    }

    override fun onReloadDone(loader: RepoLoader) {
        reload()
    }

    override fun onInstalledModulesReloaded(moduleUtil: ModuleUtil) {
        reload()
    }

    override fun onSingleInstalledModuleReloaded(moduleUtil: ModuleUtil, packageName: String, module: InstalledModule) {
        if (packageName == mPackageName)
            reload()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_download_details, menu)

        // TODO maybe enable again after checking the implementation
        menu.findItem(R.id.menu_bookmark).isVisible = false
        menu.findItem(R.id.menu_share).isVisible = false

        //mItemBookmark = menu.findItem(R.id.menu_bookmark);
        //setupBookmark(false);
        return true
    }

    private fun setupBookmark(clicked: Boolean) {
        val myPref = getSharedPreferences("bookmarks", Context.MODE_PRIVATE)

        var saved = myPref.getBoolean(module!!.packageName, false)
        val newValue: Boolean

        if (clicked) {
            newValue = !saved
            myPref.edit().putBoolean(module!!.packageName, newValue).apply()

            val msg = if (newValue) R.string.bookmark_added else R.string.bookmark_removed

            Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show()
        }

        saved = myPref.getBoolean(module!!.packageName, false)

        if (saved) {
            mItemBookmark!!.setTitle(R.string.remove_bookmark)
            mItemBookmark.setIcon(R.drawable.ic_bookmark)
        } else {
            mItemBookmark!!.setTitle(R.string.add_bookmark)
            mItemBookmark.setIcon(R.drawable.ic_bookmark_outline)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_bookmark -> setupBookmark(true)
            R.id.menu_refresh -> {
                RepoLoader.getInstance().triggerReload(true)
                return true
            }
            R.id.menu_share -> {
                var text = module!!.name + " - "

                if (isPackageInstalled(mPackageName, this)) {
                    val s = packageManager.getInstallerPackageName(mPackageName)
                    var playStore: Boolean

                    try {
                        playStore = s == ModulesFragment().PLAY_STORE_PACKAGE
                    } catch (e: NullPointerException) {
                        playStore = false
                    }

                    text += if (playStore) {
                        String.format(ModulesFragment().PLAY_STORE_LINK, mPackageName)
                    } else {
                        String.format(ModulesFragment().XPOSED_REPO_LINK, mPackageName)
                    }
                } else {
                    text += String.format(ModulesFragment().XPOSED_REPO_LINK,
                            mPackageName)
                }

                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isPackageInstalled(packagename: String?, context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }

    internal inner class SwipeFragmentPagerAdapter(fm: android.support.v4.app.FragmentManager) : FragmentPagerAdapter(fm) {
        val PAGE_COUNT = 3
        private val tabTitles = arrayOf(getString(R.string.download_details_page_description), getString(R.string.download_details_page_versions), getString(R.string.download_details_page_settings))

        override fun getCount(): Int {
            return PAGE_COUNT
        }


        @Suppress("OverridingDeprecatedMember")
        override fun getItem(position: Int): android.support.v4.app.Fragment? {
            return when (position) {
                DOWNLOAD_DESCRIPTION -> DownloadDetailsFragment()
                DOWNLOAD_VERSIONS -> DownloadDetailsVersionsFragment()
                DOWNLOAD_SETTINGS -> DownloadDetailsSettingsFragment()
                else -> null
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            // Generate title based on item position
            return tabTitles[position]
        }
    }

    companion object {

        val DOWNLOAD_DESCRIPTION = 0
        val DOWNLOAD_VERSIONS = 1
        val DOWNLOAD_SETTINGS = 2
        @SuppressLint("StaticFieldLeak")
        private val sRepoLoader = RepoLoader.getInstance()
        private val sModuleUtil = ModuleUtil.getInstance()
    }
}