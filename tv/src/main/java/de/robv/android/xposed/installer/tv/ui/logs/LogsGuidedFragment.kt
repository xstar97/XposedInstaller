package de.robv.android.xposed.installer.tv.ui.logs

import android.os.Bundle
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseLogs
import de.robv.android.xposed.installer.tv.ui.base.BaseGuidedFragment
import org.jetbrains.anko.alert

class LogsGuidedFragment: BaseGuidedFragment()
{
    companion object {
        val TAG: String = LogsGuidedFragment::class.java.simpleName
        fun newInstance() = LogsGuidedFragment()
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.nav_item_logs),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        try {
            val list = getActionsFromInfoList(activity!!, BaseLogs().getLogsList(activity!!))
            for (actionLogs in list) {
                actions.add(actionLogs)
            }
        }catch (npe: NullPointerException){
            error {"npe: ${npe.message}"}
        }catch (e: Exception){
            error {"e: ${e.message}"}
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        val pos = action!!.id.toInt()
        when (pos) {
            BaseLogs.actionView -> {
                activity!!.alert {
                    titleResource = R.string.nav_item_logs
                    message = BaseLogs().getLogs(activity!!)
                }.show()
            }
            BaseLogs.actionSave -> {
                BaseLogs().save(activity!!)
            }
            BaseLogs.actionSendEmail -> {
                BaseLogs().sendEmail(activity!!)
            }
            BaseLogs.actionSendGithub -> {
                BaseLogs().sendGitHubReport(activity!!)
            }
            BaseLogs.actionClear -> {
                BaseLogs().clear(activity!!)
            }
        }
    }
}