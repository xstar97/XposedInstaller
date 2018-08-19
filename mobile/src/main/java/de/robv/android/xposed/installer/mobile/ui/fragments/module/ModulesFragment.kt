package de.robv.android.xposed.installer.mobile.ui.fragments.module

import android.Manifest
import android.support.v4.app.ListFragment
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.util.TypedValue
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.Collator
import java.util.Locale

import de.robv.android.xposed.installer.core.repo.RepoDb
import de.robv.android.xposed.installer.core.repo.RepoDb.RowNotFoundException

import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.ModuleUtil.InstalledModule
import de.robv.android.xposed.installer.core.util.NavUtil

import de.robv.android.xposed.installer.mobile.ui.activities.DownloadDetailsActivity

import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.R.id.*
import de.robv.android.xposed.installer.R.layout.view_state_xposed_note
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.core.base.BaseXposedApp.WRITE_EXTERNAL_PERMISSION
import de.robv.android.xposed.installer.core.base.fragments.BaseModules
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.DISABLE_FILE

import de.robv.android.xposed.installer.core.base.fragments.utils.ModulesUtil
import de.robv.android.xposed.installer.mobile.logic.Navigation
import de.robv.android.xposed.installer.mobile.logic.adapters.module.ModuleAdapter
import de.robv.android.xposed.installer.mobile.ui.activities.WelcomeActivity
import de.robv.android.xposed.installer.mobile.ui.activities.containers.ViewActivity
import de.robv.android.xposed.installer.mobile.ui.activities.containers.ViewActivity.Companion.INTENT_NAV_KEY
import org.jetbrains.anko.startActivity

class ModulesFragment : ListFragment(), ModuleUtil.ModuleListener
{
    companion object {
        val TAG: String = ModulesFragment::class.java.simpleName
        fun newInstance() = ModulesFragment()
    }

    private var mAdapter: ModuleAdapter? = null
    private val reloadModules = Runnable {
        mAdapter!!.setNotifyOnChange(false)
        mAdapter!!.clear()
        val list = BaseModules.mModuleUtil!!.modules.values
        mAdapter!!.addAll(list)
        val col = Collator.getInstance(Locale.getDefault())
        mAdapter!!.sort { lhs, rhs -> col.compare(lhs.appName, rhs.appName) }
        mAdapter!!.notifyDataSetChanged()
    }
    private var mClickedMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseModules().initModuleUtil(activity!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        BaseModules.installedXposedVersion = XposedApp.getInstalledXposedVersion()
        when{
            BaseModules.installedXposedVersion < 0 || XposedApp.getActiveXposedVersion() < 0 || DISABLE_FILE.exists()->{
                val notActiveNote = activity!!.layoutInflater!!.inflate(view_state_xposed_note, listView, false)
                if (BaseModules.installedXposedVersion < 0) {
                    notActiveNote.findViewById<TextView>(android.R.id.title).setText(R.string.framework_not_installed)
                }
                notActiveNote.tag = BaseModules.NOT_ACTIVE_NOTE_TAG

                listView.addHeaderView(notActiveNote)
            }
        }

        mAdapter = ModuleAdapter(activity!!)
        reloadModules.run()
        listAdapter = mAdapter
        setEmptyText(activity!!.getString(R.string.no_xposed_modules_found))
        registerForContextMenu(listView)
        BaseModules.mModuleUtil!!.addListener(this)

        val actionBar = (activity as WelcomeActivity).supportActionBar

        val metrics = resources.displayMetrics
        val sixDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, metrics).toInt()
        val eightDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, metrics).toInt()
        //assert(actionBar != null)
        val toolBarDp = if (actionBar!!.height == 0) 196 else actionBar.height

        listView.divider = null
        listView.dividerHeight = sixDp
        listView.setPadding(eightDp, toolBarDp + eightDp, eightDp, eightDp)
        listView.clipToPadding = false

       setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //TODO maybe enable again after checking the implementation
        //inflater.inflate(R.menu.menu_modules, menu);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults)
        if (requestCode == WRITE_EXTERNAL_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mClickedMenuItem != null) {
                    Handler().postDelayed({ onOptionsItemSelected(mClickedMenuItem) }, 500)
                }
            } else {
                Toast.makeText(activity, R.string.permissionNotGranted, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == bookmarks) {
            activity!!.startActivity<ViewActivity>(INTENT_NAV_KEY to Navigation.FRAG_MODULE_BOOKMARK)
            return true
        }

        val backupPath = Environment.getExternalStorageDirectory().toString() + "/XposedInstaller"

        val enabledModulesPath = File(backupPath, "enabled_modules.list")
        val installedModulesPath = File(backupPath, "installed_modules.list")
        val targetDir = File(backupPath)
        val listModules = File(XposedApp().ENABLED_MODULES_LIST_FILE)

        mClickedMenuItem = item

        if (checkPermissions())
            return false

        when (item.itemId) {
            export_enabled_modules -> {
                if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                    return false
                }

                if (ModuleUtil.getInstance().enabledModules.isEmpty()) {
                    Toast.makeText(activity, getString(R.string.no_enabled_modules), Toast.LENGTH_SHORT).show()
                    return false
                }

                ModulesUtil().onMenuEnabledModules(activity, targetDir, listModules, enabledModulesPath)

                Toast.makeText(activity, enabledModulesPath.toString(), Toast.LENGTH_LONG).show()
                return true
            }
            export_installed_modules -> {
                if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                    Toast.makeText(activity, R.string.sdcard_not_writable, Toast.LENGTH_LONG).show()
                    return false
                }
                val installedModules = ModuleUtil.getInstance().modules

                if (installedModules.isEmpty()) {
                    Toast.makeText(activity, getString(R.string.no_installed_modules), Toast.LENGTH_SHORT).show()
                    return false
                }

                try {
                    if (!targetDir.exists())
                        targetDir.mkdir()

                    val fw = FileWriter(installedModulesPath)
                    val bw = BufferedWriter(fw)
                    val fileOut = PrintWriter(bw)

                    val keys = installedModules.keys
                    for (key1 in keys) {
                        val packageName = key1 as String
                        fileOut.println(packageName)
                    }

                    fileOut.close()
                } catch (e: IOException) {
                    Toast.makeText(activity, resources.getString(R.string.logs_save_failed) + "n" + e.message, Toast.LENGTH_LONG).show()
                    return false
                }

                Toast.makeText(activity, installedModulesPath.toString(), Toast.LENGTH_LONG).show()
                return true
            }
            import_installed_modules -> return BaseModules().importModules(activity!!,installedModulesPath)
            import_enabled_modules -> return BaseModules().importModules(activity!!, enabledModulesPath)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_PERMISSION)
            return true
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BaseModules.mModuleUtil!!.removeListener(this)
        listAdapter = null
        mAdapter = null
    }

    override fun onSingleInstalledModuleReloaded(moduleUtil: ModuleUtil, packageName: String, module: InstalledModule) {
        activity!!.runOnUiThread(reloadModules)
    }

    override fun onInstalledModulesReloaded(moduleUtil: ModuleUtil) {
        activity!!.runOnUiThread(reloadModules)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val packageName = v.tag as String

        if (packageName == BaseModules.NOT_ACTIVE_NOTE_TAG) {
            (activity as WelcomeActivity).switchFragment(Navigation.NAV_HOME)
            return
        }

        BaseModules().launchModule(activity!!, packageName)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenuInfo) {
        val installedModule = getItemFromContextMenuInfo(menuInfo) ?: return

        menu.setHeaderTitle(installedModule.appName)
        activity!!.menuInflater.inflate(R.menu.context_menu_modules, menu)

        if (BaseModules.getSettingsIntent(activity!!, installedModule.packageName) == null)
            menu.removeItem(menu_launch)

        try {
            val support = RepoDb
                    .getModuleSupport(installedModule.packageName)
            if (NavUtil.parseURL(support) == null)
                menu.removeItem(menu_support)
        } catch (e: RowNotFoundException) {
            menu.removeItem(menu_download_updates)
            menu.removeItem(menu_support)
        }

        val installer = BaseModules.mPm!!.getInstallerPackageName(installedModule.packageName)
        if (BaseModules.PLAY_STORE_LABEL != null && BaseModules.PLAY_STORE_PACKAGE == installer)
            menu.findItem(menu_play_store).title = BaseModules.PLAY_STORE_LABEL
        else
            menu.removeItem(menu_play_store)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val module = getItemFromContextMenuInfo(item.menuInfo) ?: return false

        when (item.itemId) {
            menu_launch -> {
                startActivity(BaseModules.getSettingsIntent(activity!!, module.packageName))
                return true
            }
            menu_download_updates -> {
                val detailsIntent = Intent(activity, DownloadDetailsActivity::class.java)
                detailsIntent.data = Uri.fromParts("package", module.packageName, null)
                startActivity(detailsIntent)
                return true
            }

            menu_support -> {
                NavUtil.startURL(activity!!, Uri.parse(RepoDb.getModuleSupport(module.packageName)))
                return true
            }

            menu_play_store -> {
                val i = Intent(android.content.Intent.ACTION_VIEW)
                i.data = Uri.parse(String.format(BaseModules.PLAY_STORE_LINK, module.packageName))
                i.setPackage(BaseModules.PLAY_STORE_PACKAGE)
                try {
                    startActivity(i)
                } catch (e: ActivityNotFoundException) {
                    i.setPackage(null)
                    startActivity(i)
                }

                return true
            }

            menu_app_info -> {
                startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", module.packageName, null)))
                return true
            }

            menu_uninstall -> {
                startActivity(Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.fromParts("package", module.packageName, null)))
                return true
            }
        }

        return false
    }

    private fun getItemFromContextMenuInfo(menuInfo: ContextMenuInfo): InstalledModule? {
        val info = menuInfo as AdapterContextMenuInfo
        val position = info.position - listView.headerViewsCount
        return if (position >= 0) listAdapter.getItem(position) as InstalledModule else null
    }
}