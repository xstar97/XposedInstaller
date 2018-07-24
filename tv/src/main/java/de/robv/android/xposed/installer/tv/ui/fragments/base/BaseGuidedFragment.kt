package de.robv.android.xposed.installer.tv.ui.fragments.base

import android.support.v17.leanback.app.GuidedStepSupportFragment
import android.support.v17.leanback.widget.GuidedAction
import de.robv.android.xposed.installer.core.models.InfoModel

open class BaseGuidedFragment : GuidedStepSupportFragment() {
    companion object {

        val TAG: String = BaseGuidedFragment::class.java.simpleName

        fun newInstance() = BaseGuidedFragment()
    }
    fun getActionsFromList(list: ArrayList<InfoModel>): ArrayList<GuidedAction>{
        val action = ArrayList<GuidedAction>()
        for (info in list) {
            val pos = info.pos.toLong()
            val icon = info.icon
            val title = info.key
            val description = info.desciption
            action.add(GuidedAction.Builder(activity!!)
                    .id(pos)
                    .icon(icon)
                    .title(title)
                    .description(description)
                    .build())
        }
        return action
    }
}