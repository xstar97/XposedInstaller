package de.robv.android.xposed.installer.mobile.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.mobile.XposedApp

import java.io.IOException

import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller.Companion.DISABLE_FILE
import de.robv.android.xposed.installer.core.mvc.FrameworkViewMvc
import de.robv.android.xposed.installer.core.models.ZipModel
import de.robv.android.xposed.installer.core.util.*
import de.robv.android.xposed.installer.core.util.FrameworkZips.LocalZipLoader
import de.robv.android.xposed.installer.core.util.FrameworkZips.OnlineZipLoader
import de.robv.android.xposed.installer.core.util.Loader.Listener
import de.robv.android.xposed.installer.mobile.logic.adapters.zip.ZipSpinnerAdapter
import de.robv.android.xposed.installer.mobile.ui.activities.InstallationActivity
import de.robv.android.xposed.installer.mobile.mvc.FrameworkViewMvcImp
import de.robv.android.xposed.installer.mobile.ui.fragments.list.DeviceInfoFragment
import kotlinx.android.synthetic.main.fragment_status_installer.view.*
import kotlinx.android.synthetic.main.view_state.*
import kotlinx.android.synthetic.main.view_state.view.*
import kotlinx.android.synthetic.main.view_status_installer_actions.view.*
import kotlinx.android.synthetic.main.view_status_installer_installed.view.*
import org.jetbrains.anko.*

import org.jetbrains.anko.support.v4.onUiThread

class StatusInstallerFragment : Fragment(), FrameworkViewMvc.FrameworkDelegate
{
    companion object {
        val TAG: String = StatusInstallerFragment::class.java.simpleName
        fun newInstance() = StatusInstallerFragment()
    }

    private lateinit var mFrameworkViewMvc : FrameworkViewMvc

    private val mOnlineZipListener = Listener<OnlineZipLoader> { reloadData() }
    private val mLocalZipListener = Listener<LocalZipLoader> { reloadData() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFrameworkViewMvc = FrameworkViewMvcImp(activity!!, layoutInflater)
        mFrameworkViewMvc.setDelegate(this)
        return mFrameworkViewMvc.getRootView()
    }
    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        reloadData()
        val refreshLayout = mFrameworkViewMvc.getRootView().swiperefreshlayout
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(activity!!, R.color.colorPrimary))

        BaseStatusInstaller().mOnlineZipLoader.setSwipeRefreshLayout(refreshLayout)
        BaseStatusInstaller().mOnlineZipLoader.addListener(mOnlineZipListener)
        BaseStatusInstaller().mOnlineZipLoader.triggerFirstLoadIfNecessary()

        BaseStatusInstaller().mLocalZipLoader.addListener(mLocalZipListener)
        BaseStatusInstaller().mLocalZipLoader.triggerFirstLoadIfNecessary()

        try {
            val disableSwitch = mFrameworkViewMvc.getRootView().disableSwitch
            disableSwitch.isChecked = !DISABLE_FILE.exists()
            disableSwitch.setOnCheckedChangeListener { _, _ ->
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
        BaseStatusInstaller().showPrefWarnDialog(activity!!)

    }

    override fun onFrameworkSelected(zip: ZipModel) {
        val title = zip.key!!
        val type = zip.type
        BaseStatusInstaller().showActionDialog(activity!!, Intent(activity!!, InstallationActivity::class.java), title, type)
    }
    override fun onKnownIssueSelected(uri: String?) {
        NavUtil.startURL(activity!!, uri!!)
    }
    override fun onReboot(id: Int) {
        val mode = RootUtil.RebootMode.fromId(id)
       BaseStatusInstaller().confirmReboot(activity!!, mode.titleRes, mode)
    }
    override fun onOptimizedAppDialog() {
        BaseStatusInstaller().showOptimizedAppDialog(activity!!)
    }

    override fun onResume() {
        super.onResume()
        refreshInstallStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BaseStatusInstaller().mOnlineZipLoader.removeListener(mOnlineZipListener)
        BaseStatusInstaller().mOnlineZipLoader.setSwipeRefreshLayout(null)
        BaseStatusInstaller().mLocalZipLoader.removeListener(mLocalZipListener)
    }


    private fun setSheetFragment(){
        childFragmentManager.beginTransaction().replace(R.id.app_sheet_content, DeviceInfoFragment()).commit()
    }

    private fun refreshInstallStatus() {
        val status = BaseStatusInstaller().getInstallerStatusData(activity!!)
        val disableView = mFrameworkViewMvc.getRootView().disableView

        val frameworkInstallErrors = mFrameworkViewMvc.getRootView().framework_install_errors
        val statusContainer = mFrameworkViewMvc.getRootView().status_container
        val statusIcon = mFrameworkViewMvc.getRootView().status_icon

        frameworkInstallErrors.text = status.errorMes!!
        frameworkInstallErrors.setTextColor(status.errorColor!!)
        statusContainer.setBackgroundColor(status.statusContainerColor!!)
        statusIcon.setImageDrawable(status.statusIcon!!)
        if (status.disableView != -1)
        disableView.visibility = View.GONE
    }

    @SuppressLint("StringFormatInvalid")
    private fun refreshKnownIssue() {
        val frameworkKnownIssue = mFrameworkViewMvc.getRootView().view_state_title
        val frameworkKnownIssueIcon = mFrameworkViewMvc.getRootView().view_state_icon
        frameworkKnownIssueIcon.setImageResource(R.drawable.ic_error)
        val viewState = mFrameworkViewMvc.getRootView().view_state
        val viewInstaller = mFrameworkViewMvc.getRootView().installer_view
        val viewActions = mFrameworkViewMvc.getRootView().view_status_installer_actions
        val initIssues = BaseStatusInstaller().getKnownIssueData(activity!!)
        val issueName = initIssues.first
        val issueLink = initIssues.second
        if (issueName.isNotEmpty()) {
            frameworkKnownIssue.text = String.format(getString(R.string.install_known_issue, issueName))
            frameworkKnownIssue.tag = issueLink
            viewState.visibility = View.VISIBLE
            viewInstaller.visibility = View.GONE
            viewActions.visibility = View.GONE
        } else {
            viewState.visibility = View.GONE
            viewInstaller.visibility = View.VISIBLE
            viewActions.visibility = View.VISIBLE
        }
    }

    private fun reloadData(){
        doAsync {
            val initZip = BaseStatusInstaller().getZips(activity!!)
            val myZips0 = initZip.first
            val myZips1 = initZip.second

            onUiThread {
                val viewState = mFrameworkViewMvc.getRootView().view_state
                val myLayout = mFrameworkViewMvc.getRootView().myLayout
                val errorTitle = view_state_title
                val errorIcon = view_state_icon
                errorIcon.setImageResource(R.drawable.ic_error)
                Log.d(XposedApp.TAG, "size 0: ${myZips0.size}\nsize 1: ${myZips1.size}")
                when {
                    !FrameworkZips.hasLoadedOnlineZips() -> {
                        errorTitle.setText(R.string.framework_zip_load_failed)
                        myLayout.visibility = View.GONE
                        viewState.visibility = View.VISIBLE
                    }
                    myZips0.size == 0 || myZips1.size == 0 -> {
                        errorTitle.setText(R.string.framework_no_zips)
                        myLayout.visibility = View.GONE
                        viewState.visibility = View.VISIBLE
                    }
                    else -> {
                        myLayout.visibility = View.VISIBLE
                        viewState.visibility = View.GONE
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
                mFrameworkViewMvc.getRootView().zip_spinner0.adapter = adapter0
                mFrameworkViewMvc.getRootView().zip_spinner1.adapter = adapter1
            }
        }
    }
}