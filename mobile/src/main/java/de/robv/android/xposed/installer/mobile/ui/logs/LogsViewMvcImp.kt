package de.robv.android.xposed.installer.mobile.ui.logs

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.MenuItem
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.Util
import de.robv.android.xposed.installer.core.logic.delegates.LogsDelegate
import de.robv.android.xposed.installer.core.logic.mvc.LogsViewMvc
import kotlinx.android.synthetic.main.fragment_logs.view.*

class LogsViewMvcImp(private val context: Context, layoutInflater: LayoutInflater, private var mDelegate: LogsDelegate?) : LogsViewMvc, PopupMenu.OnMenuItemClickListener//add listener
{
    private var mRootView = layoutInflater.inflate(R.layout.fragment_logs, null)
    init{
        mRootView.fabLogsActions.setOnClickListener{
            Util().launchMenu(context, mRootView.fabLogsActions, R.menu.menu_logs, this).show()
        }
    }

    override fun setDelegate(delegate: LogsDelegate) {
        mDelegate = delegate
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val id = item!!.itemId
        return when (id) {
            R.id.send_email -> {
                mDelegate?.onSendEmail()
                true
            }
            R.id.send_github -> {
                mDelegate?.onSendGitHub()
                true
            }
            R.id.menu_save -> {
                mDelegate?.onSaveLog()
                true
            }
            R.id.menu_clear -> {
                mDelegate?.onClearLog()
                true
            }
            R.id.menu_refresh -> {
                mDelegate?.onRefreshLog()
                true
            }
            else -> {
                mDelegate?.onOtherOption(id)
                true
            }
        }
    }

    override fun getRootView() = this.mRootView!!
}