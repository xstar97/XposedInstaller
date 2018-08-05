package de.robv.android.xposed.installer.mobile.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.afollestad.materialdialogs.MaterialDialog
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.mobile.XposedApp

import java.io.IOException

import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.DISABLE_FILE
import de.robv.android.xposed.installer.core.mvc.FrameworkView
import de.robv.android.xposed.installer.core.models.ZipModel
import de.robv.android.xposed.installer.core.util.*
import de.robv.android.xposed.installer.core.util.FrameworkZips.LocalZipLoader
import de.robv.android.xposed.installer.core.util.FrameworkZips.OnlineZipLoader
import de.robv.android.xposed.installer.mobile.logic.adapters.zip.ZipSpinnerAdapter
import de.robv.android.xposed.installer.mobile.ui.activities.InstallationActivity
import de.robv.android.xposed.installer.mobile.mvc.FrameworkViewImp
import kotlinx.android.synthetic.main.fragment_status_installer.*
import kotlinx.android.synthetic.main.view_status_installer_installed.*

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.onUiThread

class StatusInstallerFragment : Fragment(), FrameworkView.FrameworkDelegate
{
    companion object {
        val TAG: String = StatusInstallerFragment::class.java.simpleName
        fun newInstance() = StatusInstallerFragment()
    }

    override fun onFrameworkSelected(zip: ZipModel) {
        val title = zip.key!!
        val type = zip.type
        BaseStatusInstaller().showActionDialog(activity!!, Intent(activity!!, InstallationActivity::class.java), title, type)
    }

    override fun onKnownIssueSelected(uri: String?) {
        NavUtil.startURL(activity, uri)
    }

    private lateinit var mFrameworkView : FrameworkView
    private val PREF_VALUE_HIDE_WARN = "hide_install_warning"

    private val mOnlineZipListener = Loader.Listener<OnlineZipLoader> { reloadData() }
    private val mLocalZipListener = Loader.Listener<LocalZipLoader> { reloadData() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFrameworkView = FrameworkViewImp(layoutInflater)
        mFrameworkView.setDelegate(this)
        return mFrameworkView.getRootView()
    }
    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        reloadData()

        try {
            swiperefreshlayout.setColorSchemeColors(ContextCompat.getColor(activity!!, R.color.colorPrimary))
        }catch (e: Exception){
            Log.w(XposedApp.TAG, e.message)
        }

        BaseStatusInstaller().ONLINE_ZIP_LOADER.setSwipeRefreshLayout(swiperefreshlayout)
        BaseStatusInstaller().ONLINE_ZIP_LOADER.addListener(mOnlineZipListener)
        BaseStatusInstaller().ONLINE_ZIP_LOADER.triggerFirstLoadIfNecessary()

        BaseStatusInstaller().LOCAL_ZIP_LOADER.addListener(mLocalZipListener)
        BaseStatusInstaller().LOCAL_ZIP_LOADER.triggerFirstLoadIfNecessary()

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
        refreshKnownIssue()

        // Display warning dialog to new users
        showDialog()
    }

    override fun onResume() {
        super.onResume()
        refreshInstallStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BaseStatusInstaller().ONLINE_ZIP_LOADER.removeListener(mOnlineZipListener)
        BaseStatusInstaller().ONLINE_ZIP_LOADER.setSwipeRefreshLayout(null)
        BaseStatusInstaller().LOCAL_ZIP_LOADER.removeListener(mLocalZipListener)
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
            BaseStatusInstaller().confirmReboot(activity!!, mode.titleRes, MaterialDialog.SingleButtonCallback { dialog, which -> RootUtil.reboot(mode, activity!!) })
            return true
        } else if (i == R.id.dexopt_now) {
            BaseStatusInstaller().showOptimizedAppDialog(activity!!)
            return true
        }

        return super.onOptionsItemSelected(item)
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
        val status = BaseStatusInstaller().getInstallerStatusData(activity!!)
        framework_install_errors.text = status.errorMes!!
        framework_install_errors.setTextColor(status.errorColor!!)
        status_container.setBackgroundColor(status.statusContainerColor!!)
        status_icon.setImageDrawable(status.statusIcon!!)
        if (status.disableView != -1)
        disableView.visibility = View.GONE
    }

    @SuppressLint("StringFormatInvalid")
    private fun refreshKnownIssue() {
        val initIssues = BaseStatusInstaller().getKnownIssueData(activity!!)
        val issueName = initIssues.first
        val issueLink = initIssues.second
        if (issueName.isNotEmpty()) {
            framework_known_issue.text = String.format(getString(R.string.install_known_issue, issueName))
            framework_known_issue.tag = issueLink
            framework_known_issue.visibility = View.VISIBLE
        } else {
            framework_known_issue.visibility = View.GONE
        }
    }

    private fun reloadData(){

        doAsync {
            val initZip = BaseStatusInstaller().getZips(activity!!)
            val myZips0 = initZip.first
            val myZips1 = initZip.second

            onUiThread {
                //val tvError = v.findViewById(R.id.zips_load_error) as TextView
                Log.d(XposedApp.TAG, "size 0: ${myZips0.size}\nsize 1: ${myZips1.size}")
                when {
                    !FrameworkZips.hasLoadedOnlineZips() -> {
                        zips_load_error.setText(R.string.framework_zip_load_failed)
                        zips_load_error.visibility = View.VISIBLE
                    }
                    myZips0.size == 0 || myZips1.size == 0 -> {
                        zips_load_error.setText(R.string.framework_no_zips)
                        zips_load_error.visibility = View.VISIBLE
                    }
                    else -> {
                        zips_load_error.visibility = View.GONE
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
    /*
    private fun reloadAdapterData() {
        BaseXposedApp.runOnUiThread { refreshZips() }
    }*/
}