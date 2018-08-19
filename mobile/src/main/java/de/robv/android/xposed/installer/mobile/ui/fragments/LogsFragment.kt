package de.robv.android.xposed.installer.mobile.ui.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.base.fragments.BaseLogs
import de.robv.android.xposed.installer.core.delegates.LogsDelegate
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.ui.anko.LogsViewUI
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.onUiThread

class LogsFragment: Fragment(), LogsDelegate
{
    companion object {
        val TAG: String = LogsFragment::class.java.simpleName
        fun newInstance() = LogsFragment()
    }

    private lateinit var logViewUI : LogsViewUI

    private lateinit var svLog: ScrollView
    private lateinit var hsvLog: HorizontalScrollView
    private lateinit var container: RelativeLayout
    private lateinit var txtLog: TextView

    private var isSavedMethod: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        logViewUI = LogsViewUI(activity!!, this)
        return this.logViewUI.createView(AnkoContext.create(activity!!, this))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        reloadErrorLog()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults)
        if (requestCode == BaseXposedApp.WRITE_EXTERNAL_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isSavedMethod) {
                    Handler().postDelayed({BaseLogs().save(activity!!)}, 500)
                }
            } else {
                Toast.makeText(activity, R.string.permissionNotGranted, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSendEmail() {
        BaseLogs().sendEmail(activity!!)
    }

    override fun onSendGitHub() {
        BaseLogs().sendGitHubReport(activity!!)
    }

    override fun onSaveLog() {
        BaseLogs().save(activity!!)
        isSavedMethod = true
    }

    override fun onClearLog() {
        if(BaseLogs().clear(activity!!)) {
            txtLog = find(LogsViewUI.Ids.txtLog)
            txtLog.setText(R.string.log_is_empty)
            reloadErrorLog()
        }
    }

    override fun onRefreshLog() {
        reloadErrorLog()
    }

    override fun onOtherOption(id: Int) {
        when (id) {
            R.id.menu_scroll_top -> scrollTop()
            R.id.menu_scroll_down -> scrollDown()
        }
    }

    private fun scrollTop() {
        svLog = find(LogsViewUI.Ids.svLog)
        hsvLog = find(LogsViewUI.Ids.hsvLog)
        svLog.post { svLog.scrollTo(0, 0) }
        hsvLog.post { hsvLog.scrollTo(0, 0) }
    }
    private fun scrollDown() {
        svLog = find(LogsViewUI.Ids.svLog)
        hsvLog = find(LogsViewUI.Ids.hsvLog)
        txtLog = find(LogsViewUI.Ids.txtLog)
        svLog.post { svLog.scrollTo(0, txtLog.height) }
        hsvLog.post { hsvLog.scrollTo(0, 0) }
    }

    private fun reloadErrorLog(){
        svLog = find(LogsViewUI.Ids.svLog)
        hsvLog = find(LogsViewUI.Ids.hsvLog)
        txtLog = find(LogsViewUI.Ids.txtLog)
        container = find(LogsViewUI.Ids.container)
        val mProgressDialog = activity!!.progressDialog(message = "", title = activity!!.getString(R.string.loading))
        var logs: String?
        doAsync {
            logs = BaseLogs().getLogs(activity!!)
            Log.d(XposedApp.TAG, "log: $logs")
            onUiThread {
                mProgressDialog.dismiss()
                 txtLog.text = logs
            }
        }
        svLog.post { svLog.scrollTo(0, txtLog.height) }
        hsvLog.post { hsvLog.scrollTo(0, 0) }
    }
}