package de.robv.android.xposed.installer.mobile.ui.anko

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import android.view.View
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.delegates.LogsDelegate
import de.robv.android.xposed.installer.mobile.ui.fragments.LogsFragment
import de.robv.android.xposed.installer.mobile.logic.Utils
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.sdk15.coroutines.onClick

class LogsViewUI(context: Context, delegate: LogsDelegate) : AnkoComponent<LogsFragment>, PopupMenu.OnMenuItemClickListener {

    private var mContext: Context? = null
    private var mDelegate: LogsDelegate? = null

    init {
        mContext = context
        mDelegate = delegate
    }
    override fun createView(ui: AnkoContext<LogsFragment>): View = with(ui) {

        relativeLayout {
            id = Ids.container

            scrollView {
                id = Ids.svLog

                horizontalScrollView {
                    id = Ids.hsvLog
                    textView {
                        id = Ids.txtLog
                        padding = dip(8)
                        setTextIsSelectable(true)
                    }.lparams(width = wrapContent, height = wrapContent)

                }.lparams(width = matchParent, height = wrapContent)

            }.lparams(width = matchParent, height = wrapContent)

            floatingActionButton {
                isClickable = true
                isFocusable = true
                id = Ids.fabLogActions
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_info))
                onClick {
                    Utils().launchMenu(mContext!!, find(Ids.fabLogActions), R.menu.menu_logs, this@LogsViewUI).show()
                }
            }.lparams(width = wrapContent, height = wrapContent) {
                alignParentRight()
                alignParentBottom()
                alignParentEnd()
                margin = dip(16)
            }

        }
    }
    object Ids {
        const val container = 1
        const val fabLogActions = 2
        const val hsvLog = 3
        const val svLog = 4
        const val txtLog = 5
    }
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val id = item!!.itemId
        return when (id) {
            R.id.send_email -> {
                mDelegate?.onSendEmail()!!
                true
            }
            R.id.send_github -> {
                mDelegate?.onSendGitHub()!!
                true
            }
            R.id.menu_save -> {
                mDelegate?.onSaveLog()!!
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
                mDelegate?.onOtherOption(id)!!
                true
            }
        }
    }
}