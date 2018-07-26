package de.robv.android.xposed.installer.mobile.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.afollestad.materialdialogs.MaterialDialog
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.mobile.XposedApp

import java.io.File
import java.io.IOException

import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.DISABLE_FILE
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.LOCAL_ZIP_LOADER
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.ONLINE_ZIP_LOADER
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.checkClassExists
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.confirmReboot
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.getCanonicalFile
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.getPathWithCanonicalPath
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.showActionDialog
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.showOptimizedAppDialog
import de.robv.android.xposed.installer.core.models.ZipModel
import de.robv.android.xposed.installer.core.util.*
import de.robv.android.xposed.installer.core.util.FrameworkZips.LocalZipLoader
import de.robv.android.xposed.installer.core.util.FrameworkZips.OnlineZipLoader
import de.robv.android.xposed.installer.mobile.logic.adapters.zip.ZipSpinnerAdapter
import de.robv.android.xposed.installer.mobile.ui.activities.InstallationActivity
import kotlinx.android.synthetic.main.fragment_status_installer.*

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.onUiThread

@Suppress("DEPRECATION", "PrivatePropertyName", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName")
class StatusInstallerFragment : Fragment(), View.OnClickListener
{

    override fun onClick(v: View?) {
        val id = v!!.id

        if (id == R.id.button_zip_spinner0){
            val zip = zip_spinner0.selectedItem as ZipModel
            val key = zip.key
            val type = FrameworkZips.Type.INSTALLER

            showActionDialog(activity!!, Intent(context, InstallationActivity::class.java), key, type)
        }
        else if (id == R.id.button_zip_spinner1){
            val zip = zip_spinner1.selectedItem as ZipModel
            val key = zip.key
            val type = FrameworkZips.Type.UNINSTALLER

            showActionDialog(activity!!, Intent(context, InstallationActivity::class.java), key, type)
            }
        }

    companion object {
        val TAG: String = StatusInstallerFragment::class.java.simpleName
        fun newInstance() = StatusInstallerFragment()
    }

    private val PREF_VALUE_HIDE_WARN = "hide_install_warning"

    private val mOnlineZipListener = Loader.Listener<OnlineZipLoader> { refreshZipViewsOnUiThread() }
    private val mLocalZipListener = Loader.Listener<LocalZipLoader> { refreshZipViewsOnUiThread() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_status_installer, container, false)
    }
    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        refreshZips(v)
        button_zip_spinner0.setOnClickListener(this)
        button_zip_spinner1.setOnClickListener(this)

        // Available ZIPs
        val refreshLayout = v.findViewById<View>(R.id.swiperefreshlayout) as SwipeRefreshLayout
        try {
            refreshLayout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        }catch (e: Exception){
            Log.w(XposedApp.TAG, e.message)
        }

        ONLINE_ZIP_LOADER.setSwipeRefreshLayout(refreshLayout)
        ONLINE_ZIP_LOADER.addListener(mOnlineZipListener)
        ONLINE_ZIP_LOADER.triggerFirstLoadIfNecessary()

        LOCAL_ZIP_LOADER.addListener(mLocalZipListener)
        LOCAL_ZIP_LOADER.triggerFirstLoadIfNecessary()

        // Disable switch
        val disableSwitch = v.findViewById<View>(R.id.disableSwitch) as SwitchCompat

        try {
            disableSwitch.isChecked = !DISABLE_FILE.exists()
            disableSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (DISABLE_FILE.exists()) {
                DISABLE_FILE.delete()
                Snackbar.make(disableSwitch, R.string.xposed_on_next_reboot, Snackbar.LENGTH_LONG).show()
            } else {
                DISABLE_FILE.createNewFile()
                Snackbar.make(disableSwitch, R.string.xposed_off_next_reboot, Snackbar.LENGTH_LONG).show()
            }
            }
        } catch (e: Exception){
        Log.w(XposedApp.TAG, e.message)
        } catch (e: IOException) {
            Log.e(BaseXposedApp.TAG, "Could not create $DISABLE_FILE", e)
        }

        // Device info
        setSheetFragment()

        // Known issues
        refreshKnownIssue(v)

        // Display warning dialog to new users
        showDialog()
    }

    override fun onResume() {
        super.onResume()
        refreshInstallStatus()
    }

    private fun showDialog(){
        if (!BaseXposedApp.getPreferences().getBoolean(PREF_VALUE_HIDE_WARN, false)) {
            MaterialDialog.Builder(activity!!)
                    .title(R.string.install_warning_title)
                    .content(R.string.install_warning)
                    .positiveText(android.R.string.ok)
                    .onPositive { dialog, which ->
                        if (dialog.isPromptCheckBoxChecked) {
                            BaseXposedApp.getPreferences().edit().putBoolean(PREF_VALUE_HIDE_WARN, true).apply()
                        }
                    }
                    .checkBoxPromptRes(R.string.dont_show_again, false, null)
                    .cancelable(false)
                    .show()
        }
    }

    private fun setSheetFragment(){
        childFragmentManager.beginTransaction().replace(R.id.app_sheet_content, DeviceInfoFragment()).commit()
    }

    private fun refreshInstallStatus() {
        val v = view!!
        val txtInstallError= v.findViewById<View>(R.id.framework_install_errors) as TextView
        val txtInstallContainer= v.findViewById<View>(R.id.status_container) as View
        val txtInstallIcon= v.findViewById<View>(R.id.status_icon) as ImageView
        val disableWrapper= v.findViewById<View>(R.id.disableView) as View

        // TODO This should probably compare the full version string, not just the number part.
        val active = BaseXposedApp.getActiveXposedVersion()
        val installed = BaseXposedApp.getInstalledXposedVersion()
        when {
            installed < 0 -> {
                txtInstallError.setText(R.string.framework_not_installed)
                txtInstallError.setTextColor(resources.getColor(R.color.warning))
                txtInstallContainer.setBackgroundColor(resources.getColor(R.color.warning))
                txtInstallIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_error))
                disableWrapper.visibility = View.GONE
            }
            installed != active -> {
                txtInstallError.text = getString(R.string.framework_not_active, BaseXposedApp.getXposedProp().version)
                txtInstallError.setTextColor(resources.getColor(R.color.amber_500))
                txtInstallContainer.setBackgroundColor(resources.getColor(R.color.amber_500))
                txtInstallIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_warning))
            }
            else -> {
                txtInstallError.text = getString(R.string.framework_active, BaseXposedApp.getXposedProp().version)
                txtInstallError.setTextColor(resources.getColor(R.color.darker_green))
                txtInstallContainer.setBackgroundColor(resources.getColor(R.color.darker_green))
                txtInstallIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_check_circle))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ONLINE_ZIP_LOADER.removeListener(mOnlineZipListener)
        ONLINE_ZIP_LOADER.setSwipeRefreshLayout(null)
        LOCAL_ZIP_LOADER.removeListener(mLocalZipListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_installer, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val i = item!!.itemId
        if (i == R.id.reboot || i == R.id.soft_reboot || i == R.id.reboot_recovery) {
            val mode = RootUtil.RebootMode.fromId(item.itemId)
            confirmReboot(activity!!, mode.titleRes, MaterialDialog.SingleButtonCallback { dialog, which -> RootUtil.reboot(mode, activity!!) })
            return true
        } else if (i == R.id.dexopt_now) {
            showOptimizedAppDialog(activity!!)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    //TODO add method to core module instead
    @SuppressLint("StringFormatInvalid")
    private fun refreshKnownIssue(v: View) {
        val issueName: String?
        val issueLink: String?
        val appInfo = activity!!.applicationInfo
        val baseDir = File(BaseXposedApp().BASE_DIR)
        val baseDirCanonical = getCanonicalFile(baseDir)
        val baseDirActual = File(if (Build.VERSION.SDK_INT >= 24) appInfo.deviceProtectedDataDir else appInfo.dataDir)
        val baseDirActualCanonical = getCanonicalFile(baseDirActual)
        val prop = BaseXposedApp.getXposedProp()
        val missingFeatures = prop?.missingInstallerFeatures

        if (missingFeatures != null && !missingFeatures.isEmpty()) {
            InstallZipUtil.reportMissingFeatures(missingFeatures)
            issueName = getString(R.string.installer_needs_update, getString(R.string.app_name))
            issueLink = getString(R.string.about_support)
        } else if (File("/system/framework/core.jar.jex").exists()) {
            issueName = "Aliyun OS"
            issueLink = "https://forum.xda-developers.com/showpost.php?p=52289793&postcount=5"
        } else if (Build.VERSION.SDK_INT < 24 && (File("/data/miui/DexspyInstaller.jar").exists() || checkClassExists("miui.dexspy.DexspyInstaller"))) {
            issueName = "MIUI/Dexspy"
            issueLink = "https://forum.xda-developers.com/showpost.php?p=52291098&postcount=6"
        } else if (Build.VERSION.SDK_INT < 24 && File("/system/framework/twframework.jar").exists()) {
            issueName = "Samsung TouchWiz ROM"
            issueLink = "https://forum.xda-developers.com/showthread.php?t=3034811"
        } else if (baseDirCanonical != baseDirActualCanonical) {
            Log.e(BaseXposedApp.TAG, "Base directory: " + getPathWithCanonicalPath(baseDir, baseDirCanonical))
            Log.e(BaseXposedApp.TAG, "Expected: " + getPathWithCanonicalPath(baseDirActual, baseDirActualCanonical))
            issueName = getString(R.string.known_issue_wrong_base_directory, getPathWithCanonicalPath(baseDirActual, baseDirActualCanonical))
            issueLink = "https://github.com/rovo89/XposedInstaller/issues/395"
        } else if (!baseDir.exists()) {
            issueName = getString(R.string.known_issue_missing_base_directory)
            issueLink = "https://github.com/rovo89/XposedInstaller/issues/393"
        } else {
            issueName = null
            issueLink = null
        }

        val txtKnownIssue = v.findViewById<View>(R.id.framework_known_issue) as TextView
        if (issueName != null) {
            txtKnownIssue.text = getString(R.string.install_known_issue, issueName)
            txtKnownIssue.visibility = View.VISIBLE
            txtKnownIssue.setOnClickListener { NavUtil.startURL(activity, issueLink) }
        } else {
            txtKnownIssue.visibility = View.GONE
        }
    }

    private fun refreshZips(v: View){

        doAsync {
            val myZips0 = BaseStatusInstaller.getZips().first
            val myZips1 = BaseStatusInstaller.getZips().second

            onUiThread {
                val tvError = v.findViewById(R.id.zips_load_error) as TextView
                Log.d(XposedApp.TAG, "size 0: ${myZips0.size}\nsize 1: ${myZips1.size}")
                when {
                    !FrameworkZips.hasLoadedOnlineZips() -> {
                        tvError.setText(R.string.framework_zip_load_failed)
                        tvError.visibility = View.VISIBLE
                    }
                    myZips0.size == 0 || myZips1.size == 0 -> {
                        tvError.setText(R.string.framework_no_zips)
                        tvError.visibility = View.VISIBLE
                    }
                    else -> {
                        tvError.visibility = View.GONE
                        populateSpinner(myZips0, myZips1)
                    }
                }
            }
        }
    }

    private fun populateSpinner(zip0: ArrayList<ZipModel>, zip1: ArrayList<ZipModel>){
        doAsync {

            val adapter0 = ZipSpinnerAdapter(activity!!, zip0)
            val adapter1 = ZipSpinnerAdapter(activity!!, zip1)

            onUiThread {
                zip_spinner0.adapter = adapter0
                zip_spinner1.adapter = adapter1
            }
        }
    }
    private fun refreshZipViewsOnUiThread() {
        BaseXposedApp.runOnUiThread { refreshZips(view!!) }
    }
}