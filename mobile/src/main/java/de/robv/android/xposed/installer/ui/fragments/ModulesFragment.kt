package de.robv.android.xposed.installer.ui.fragments

import android.Manifest
import android.support.v4.app.ListFragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.util.TypedValue
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.PrintWriter
import java.text.Collator
import java.util.ArrayList
import java.util.Locale

import de.robv.android.xposed.installer.core.repo.Module
import de.robv.android.xposed.installer.core.repo.ModuleVersion
import de.robv.android.xposed.installer.core.repo.ReleaseType
import de.robv.android.xposed.installer.core.repo.RepoDb
import de.robv.android.xposed.installer.core.repo.RepoDb.RowNotFoundException
import de.robv.android.xposed.installer.core.util.DownloadsUtil
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.ModuleUtil.InstalledModule
import de.robv.android.xposed.installer.core.util.NavUtil
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.logic.ThemeUtil
import de.robv.android.xposed.installer.ui.activities.DownloadDetailsActivity
import de.robv.android.xposed.installer.ui.activities.ModulesBookmark

import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.core.base.BaseXposedApp.WRITE_EXTERNAL_PERMISSION
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.DISABLE_FILE
import de.robv.android.xposed.installer.core.util.ModuleUtil.SETTINGS_CATEGORY
import de.robv.android.xposed.installer.core.base.fragments.utils.ModulesUtil

@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNUSED_VARIABLE", "PropertyName", "MemberVisibilityCanBePrivate")
class ModulesFragment : ListFragment(), ModuleUtil.ModuleListener
{
    companion object {
        val TAG: String = ModulesFragment::class.java.simpleName
        fun newInstance() = ModulesFragment()
    }

    val PLAY_STORE_PACKAGE = "com.android.vending"
    val PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=%s"
    var PLAY_STORE_LABEL: String? = null

    val XPOSED_REPO_LINK = "http://repo.xposed.info/module/%s"
    val NOT_ACTIVE_NOTE_TAG = "NOT_ACTIVE_NOTE"
    var installedXposedVersion: Int = 0
    var mModuleUtil: ModuleUtil? = null
    var mPm: PackageManager? = null

    private var mAdapter: ModuleAdapter? = null
    private val reloadModules = Runnable {
        mAdapter!!.setNotifyOnChange(false)
        mAdapter!!.clear()
        mAdapter!!.addAll(mModuleUtil!!.modules.values)
        val col = Collator.getInstance(Locale.getDefault())
        mAdapter!!.sort { lhs, rhs -> col.compare(lhs.appName, rhs.appName) }
        mAdapter!!.notifyDataSetChanged()
    }
    private var mClickedMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModuleUtil = ModuleUtil.getInstance()
        mPm = activity!!.packageManager
        if (PLAY_STORE_LABEL == null) {
            try {
                val ai = mPm!!.getApplicationInfo(PLAY_STORE_PACKAGE,
                        0)
                PLAY_STORE_LABEL = mPm!!.getApplicationLabel(ai).toString()
            } catch (ignored: NameNotFoundException) {
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        installedXposedVersion = XposedApp.getInstalledXposedVersion()
        if (installedXposedVersion < 0 || XposedApp.getActiveXposedVersion() < 0 || DISABLE_FILE.exists()) {
            val notActiveNote = activity!!.layoutInflater.inflate(R.layout.view_xposed_not_active_note, listView, false)
            if (installedXposedVersion < 0) {
                (notActiveNote.findViewById<View>(android.R.id.title) as TextView).setText(R.string.framework_not_installed)
            }
            notActiveNote.tag = NOT_ACTIVE_NOTE_TAG
            listView.addHeaderView(notActiveNote)
        }

        mAdapter = ModuleAdapter(activity!!)
        reloadModules.run()
        listAdapter = mAdapter
        setEmptyText(activity!!.getString(R.string.no_xposed_modules_found))
        registerForContextMenu(listView)
        mModuleUtil!!.addListener(this)

        //val actionBar = (activity as WelcomeActivity).supportActionBar

        val metrics = resources.displayMetrics
        val sixDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, metrics).toInt()
        val eightDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, metrics).toInt()
        //assert(actionBar != null)
        //val toolBarDp = if (actionBar!!.height == 0) 196 else actionBar.height

        listView.divider = null
        listView.dividerHeight = sixDp
        //listView.setPadding(eightDp, toolBarDp + eightDp, eightDp, eightDp)
        listView.clipToPadding = false

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // TODO maybe enable again after checking the implementation
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
        if (item!!.itemId == R.id.bookmarks) {
            startActivity(Intent(activity, ModulesBookmark::class.java))
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
            R.id.export_enabled_modules -> {
                if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                    return false
                }

                if (ModuleUtil.getInstance().enabledModules.isEmpty()) {
                    Toast.makeText(activity, getString(R.string.no_enabled_modules), Toast.LENGTH_SHORT).show()
                    return false
                }

                ModulesUtil().onMenuEnabledModules(activity, targetDir, listModules, enabledModulesPath)
                /*try {
                    if (!targetDir.exists())
                        targetDir.mkdir()

                    val `in` = FileInputStream(listModules)
                    val out = FileOutputStream(enabledModulesPath)

                    val buffer = ByteArray(1024)
                    var len: Int
                    while ((len = `in`.read(buffer)) > 0) {
                        out.write(buffer, 0, len)
                    }
                    `in`.close()
                    out.close()
                } catch (e: IOException) {
                    Toast.makeText(activity, resources.getString(R.string.logs_save_failed) + "\n" + e.message, Toast.LENGTH_LONG).show()
                    return false
                }*/

                Toast.makeText(activity, enabledModulesPath.toString(), Toast.LENGTH_LONG).show()
                return true
            }
            R.id.export_installed_modules -> {
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
            R.id.import_installed_modules -> return importModules(installedModulesPath)
            R.id.import_enabled_modules -> return importModules(enabledModulesPath)
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

    private fun importModules(path: File): Boolean {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(activity, R.string.sdcard_not_writable, Toast.LENGTH_LONG).show()
            return false
        }
        var ips: InputStream? = null
        val repoLoader = RepoLoader.getInstance()
        val list = ArrayList<Module>()
        if (!path.exists()) {
            Toast.makeText(activity, getString(R.string.no_backup_found),
                    Toast.LENGTH_LONG).show()
            return false
        }
        try {
            @Suppress("UNUSED_VALUE")
            ips = FileInputStream(path)
        } catch (e: FileNotFoundException) {
            Log.e(XposedApp.TAG, "Could not open $path", e)
        }

        if (path.length() == 0L) {
            Toast.makeText(activity, R.string.file_is_empty, Toast.LENGTH_LONG).show()
            return false
        }

        //TODO fix this
        /*try {
            assert(ips != null)
            val ipsr = InputStreamReader(ips!!)
            val br = BufferedReader(ipsr)
            var line: String
            while ((line = br.readLine()) != null) {
                val m = repoLoader.getModule(line)

                if (m == null) {
                    Toast.makeText(activity, getString(R.string.download_details_not_found,
                            line), Toast.LENGTH_SHORT).show()
                } else {
                    list.add(m)
                }
            }
            br.close()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show()
        }*/

        for (m in list) {
            var mv: ModuleVersion? = null
            for (i in m.versions.indices) {
                val mvTemp = m.versions[i]

                if (mvTemp.relType == ReleaseType.STABLE) {
                    mv = mvTemp
                    break
                }
            }

            if (mv != null) {
                DownloadsUtil.addModule(activity, m.name, mv.downloadLink, DownloadDetailsVersionsFragment.DownloadModuleCallback(mv))
            }
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mModuleUtil!!.removeListener(this)
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

        if (packageName == NOT_ACTIVE_NOTE_TAG) {
            //TODO add this to new 'WelcomeActivity.kt'
            //(activity as WelcomeActivity).switchFragment(0)
            return
        }

        val launchIntent = getSettingsIntent(packageName)
        if (launchIntent != null)
            startActivity(launchIntent)
        else
            Toast.makeText(activity,
                    activity!!.getString(R.string.module_no_ui),
                    Toast.LENGTH_LONG).show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenuInfo) {
        val installedModule = getItemFromContextMenuInfo(menuInfo) ?: return

        menu.setHeaderTitle(installedModule.appName)
        activity!!.menuInflater.inflate(R.menu.context_menu_modules, menu)

        if (getSettingsIntent(installedModule.packageName) == null)
            menu.removeItem(R.id.menu_launch)

        try {
            val support = RepoDb
                    .getModuleSupport(installedModule.packageName)
            if (NavUtil.parseURL(support) == null)
                menu.removeItem(R.id.menu_support)
        } catch (e: RowNotFoundException) {
            menu.removeItem(R.id.menu_download_updates)
            menu.removeItem(R.id.menu_support)
        }

        val installer = mPm!!.getInstallerPackageName(installedModule.packageName)
        if (PLAY_STORE_LABEL != null && PLAY_STORE_PACKAGE == installer)
            menu.findItem(R.id.menu_play_store).title = PLAY_STORE_LABEL
        else
            menu.removeItem(R.id.menu_play_store)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val module = getItemFromContextMenuInfo(item.menuInfo) ?: return false

        when (item.itemId) {
            R.id.menu_launch -> {
                startActivity(getSettingsIntent(module.packageName))
                return true
            }

            R.id.menu_download_updates -> {
                val detailsIntent = Intent(activity, DownloadDetailsActivity::class.java)
                detailsIntent.data = Uri.fromParts("package", module.packageName, null)
                startActivity(detailsIntent)
                return true
            }

            R.id.menu_support -> {
                NavUtil.startURL(activity, Uri.parse(RepoDb.getModuleSupport(module.packageName)))
                return true
            }

            R.id.menu_play_store -> {
                val i = Intent(android.content.Intent.ACTION_VIEW)
                i.data = Uri.parse(String.format(PLAY_STORE_LINK, module.packageName))
                i.setPackage(PLAY_STORE_PACKAGE)
                try {
                    startActivity(i)
                } catch (e: ActivityNotFoundException) {
                    i.setPackage(null)
                    startActivity(i)
                }

                return true
            }

            R.id.menu_app_info -> {
                startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", module.packageName, null)))
                return true
            }

            R.id.menu_uninstall -> {
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

    private fun getSettingsIntent(packageName: String): Intent? {
        // taken from
        // ApplicationPackageManager.getLaunchIntentForPackage(String)
        // first looks for an Xposed-specific category, falls back to
        // getLaunchIntentForPackage
        val pm = activity!!.packageManager

        val intentToResolve = Intent(Intent.ACTION_MAIN)
        intentToResolve.addCategory(SETTINGS_CATEGORY)
        intentToResolve.setPackage(packageName)
        val ris = pm.queryIntentActivities(intentToResolve, 0)

        if (ris == null || ris.size <= 0) {
            return pm.getLaunchIntentForPackage(packageName)
        }

        val intent = Intent(intentToResolve)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClassName(ris[0].activityInfo.packageName, ris[0].activityInfo.name)
        return intent
    }

    private inner class ModuleAdapter(context: Context) : ArrayAdapter<InstalledModule>(context, R.layout.list_item_module, R.id.title) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)

            if (convertView == null) {
                // The reusable view was created for the first time, set up the
                // listener on the checkbox
                (view.findViewById<View>(R.id.checkbox) as CheckBox).setOnCheckedChangeListener { buttonView, isChecked ->
                    val packageName = buttonView.tag as String
                    val changed = mModuleUtil!!.isModuleEnabled(packageName) xor isChecked
                    if (changed) {
                        mModuleUtil!!.setModuleEnabled(packageName, isChecked)
                        mModuleUtil!!.updateModulesList(true)
                    }
                }
            }

            val item = getItem(position)

            val version = view.findViewById<View>(R.id.version_name) as TextView
            version.text = item!!.versionName

            // Store the package name in some views' tag for later access
            view.findViewById<View>(R.id.checkbox).tag = item.packageName
            view.tag = item.packageName

            (view.findViewById<View>(R.id.icon) as ImageView).setImageDrawable(item.icon)

            val descriptionText = view.findViewById<View>(R.id.description) as TextView
            if (!item.description.isEmpty()) {
                descriptionText.text = item.description
                descriptionText.setTextColor(ThemeUtil.getThemeColor(context, android.R.attr.textColorSecondary))
            } else {
                descriptionText.text = getString(R.string.module_empty_description)
                @Suppress("DEPRECATION")
                descriptionText.setTextColor(resources.getColor(R.color.warning))
            }

            val checkbox = view.findViewById<View>(R.id.checkbox) as CheckBox
            checkbox.isChecked = mModuleUtil!!.isModuleEnabled(item.packageName)
            val warningText = view.findViewById<View>(R.id.warning) as TextView

            if (item.minVersion == 0) {
                checkbox.isEnabled = false
                warningText.text = getString(R.string.no_min_version_specified)
                warningText.visibility = View.VISIBLE
            } else if (installedXposedVersion != 0 && item.minVersion > installedXposedVersion) {
                checkbox.isEnabled = false
                warningText.text = String.format(getString(R.string.warning_xposed_min_version), item.minVersion)
                warningText.visibility = View.VISIBLE
            } else if (item.minVersion < ModuleUtil.MIN_MODULE_VERSION) {
                checkbox.isEnabled = false
                warningText.text = String.format(getString(R.string.warning_min_version_too_low), item.minVersion, ModuleUtil.MIN_MODULE_VERSION)
                warningText.visibility = View.VISIBLE
            } else if (item.isInstalledOnExternalStorage) {
                checkbox.isEnabled = false
                warningText.text = getString(R.string.warning_installed_on_external_storage)
                warningText.visibility = View.VISIBLE
            } else if (installedXposedVersion == 0) {
                checkbox.isEnabled = false
                warningText.text = getString(R.string.framework_not_installed)
                warningText.visibility = View.VISIBLE
            } else {
                checkbox.isEnabled = true
                warningText.visibility = View.GONE
            }
            return view
        }
    }

}
