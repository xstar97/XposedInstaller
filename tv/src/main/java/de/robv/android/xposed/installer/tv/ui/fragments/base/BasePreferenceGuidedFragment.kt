package de.robv.android.xposed.installer.tv.ui.fragments.base

import android.content.SharedPreferences
import android.support.v17.leanback.app.GuidedStepSupportFragment
import android.support.v17.leanback.widget.GuidedAction
import android.support.v7.preference.Preference
import de.robv.android.xposed.installer.core.models.InfoModel

open class BasePreferenceGuidedFragment: GuidedStepSupportFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener
{
    override fun onPreferenceClick(preference: Preference?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        val TAG: String = BasePreferenceGuidedFragment::class.java.simpleName

        fun newInstance() = BasePreferenceGuidedFragment()
    }

    fun getActionsFromList(list: ArrayList<InfoModel>): ArrayList<GuidedAction> {
        val action = ArrayList<GuidedAction>()
        for (info in list) {
            val pos = info.pos.toLong()
            val title = info.key
            val description = info.desciption
            action.add(GuidedAction.Builder(activity!!)
                    .id(pos)
                    .title(title)
                    .description(description)
                    .build())
        }
        return action
    }
}