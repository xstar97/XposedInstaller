package de.robv.android.xposed.installer.tv.ui.base

import android.content.Context
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedAction.CHECKBOX_CHECK_SET_ID
import android.util.Log
import de.robv.android.xposed.installer.core.logic.models.InfoModel
import de.robv.android.xposed.installer.core.logic.models.ZipModel
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.tv.XposedApp

open class BaseGuidedFragment : GuidedStepSupportFragment() {
    companion object {
        val TAG: String = BaseGuidedFragment::class.java.simpleName
        fun newInstance() = BaseGuidedFragment()
    }

    fun getActionsFromList(context: Context, list: Array<String>): ArrayList<GuidedAction>{
        val actions = ArrayList<GuidedAction>()
        for ((pos, title) in list.withIndex()) {
            actions.add(GuidedAction.Builder(context)
                    .id(pos.toLong())
                    .title(title)
                    .build())
        }
        return actions
    }
    fun getActionsFromInfoList(context: Context, list: ArrayList<InfoModel>): ArrayList<GuidedAction>{
        val actions = ArrayList<GuidedAction>()
        for (info in list) {
            val pos = info.pos.toLong()
            val icon = info.icon
            val title = info.key
            val description = info.desciption

            if (icon != null) {
                actions.add(GuidedAction.Builder(context)
                        .id(pos)
                        .icon(icon)
                        .title(title)
                        .description(description)
                        .build())
            }else{
                actions.add(GuidedAction.Builder(context)
                        .id(pos)
                        .title(title)
                        .description(description)
                        .build())
            }
        }
        return actions
    }
    fun getActionsFromZipList(context: Context, list: ArrayList<ZipModel>): ArrayList<GuidedAction>{
        val actions = ArrayList<GuidedAction>()
        for ((position, zip) in list.withIndex()) {
            val pos = position.toLong()
            val icon = zip.icon
            val title = zip.key
            Log.v(XposedApp.TAG, "\ngetActionsFromZipList: \npos: $pos\nicon: $icon\ntitle: $title")
            actions.add(GuidedAction.Builder(context)
                    .id(pos)
                    .icon(icon)
                    .title(title)
                    .build())
        }
        return actions
    }
    fun getActionsFromModuleList(context: Context, list: MutableCollection<ModuleUtil.InstalledModule>): ArrayList<GuidedAction> {
        val actions = ArrayList<GuidedAction>()
        for ((position, module) in list.withIndex()) {
            val pos = position.toLong()
            val icon = module.icon
            val title = module.appName
            val description = module.description
            //val checkState = BaseModules.mModuleUtil!!.isModuleEnabled(module.packageName)
            Log.v(XposedApp.TAG, "\ngetActionsFromModuleList: \npos: $pos\ntitle: $title\ndescription: $description")
            actions.add(GuidedAction.Builder(context)
                    .id(pos)
                    .icon(icon)
                    .title(title)
                    .description(description)
                    .checkSetId(CHECKBOX_CHECK_SET_ID)
                    //.checked(checkState)
                    .build())
        }
        return actions
    }
}