package de.robv.android.xposed.installer.mobile.ui.fragments

import android.support.v4.app.Fragment
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.mobile.XposedApp

import de.robv.android.xposed.installer.core.base.BaseXposedApp.WRITE_EXTERNAL_PERMISSION
import de.robv.android.xposed.installer.core.base.fragments.BaseLogs
import kotlinx.android.synthetic.main.fragment_logs.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.onUiThread

class LogsFragment : Fragment()
{
    companion object {
        val TAG: String = LogsFragment::class.java.simpleName
        fun newInstance() = LogsFragment()
    }
    private var mClickedMenuItem: MenuItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_logs, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        reloadErrorLog()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_logs, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        mClickedMenuItem = item
        when (item!!.itemId) {
            R.id.menu_scroll_top -> scrollTop()
            R.id.menu_scroll_down -> scrollDown()
            R.id.menu_refresh -> {
                reloadErrorLog()
                return true
            }
            R.id.send_email -> {
                BaseLogs().sendEmail(activity!!)
                return true
            }
            R.id.send_github -> {
                BaseLogs().sendGitHubReport(activity!!)
                return true
            }
            R.id.menu_save -> {
                BaseLogs().save(activity!!)
                return true
            }
            R.id.menu_clear -> {
                if(BaseLogs().clear(activity!!)) {
                    txtLog!!.setText(R.string.log_is_empty)
                    reloadErrorLog()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun scrollTop() {
        svLog?.post { svLog?.scrollTo(0, 0) }
        hsvLog?.post { hsvLog?.scrollTo(0, 0) }
    }

    private fun scrollDown() {
        svLog?.post { svLog?.scrollTo(0, txtLog!!.height) }
        hsvLog?.post { hsvLog?.scrollTo(0, 0) }
    }

    private fun reloadErrorLog() {
        val mProgressDialog = MaterialDialog.Builder(activity!!).content(de.robv.android.xposed.installer.core.R.string.loading).progress(true, 0).show()
        var logs: String?
        doAsync {
            logs = BaseLogs().getLogs(activity!!)
            Log.d(XposedApp.TAG, "log: $logs")
            onUiThread {
                mProgressDialog.dismiss()
                txtLog!!.text = logs
            }
        }
        svLog?.post { svLog?.scrollTo(0, txtLog!!.height) }
        hsvLog?.post { hsvLog?.scrollTo(0, 0) }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
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

}
