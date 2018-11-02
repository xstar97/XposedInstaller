package de.robv.android.xposed.installer.tv.ui.module

import android.os.Bundle
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseModules
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.ui.base.BaseGuidedFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.onUiThread

class ModulesFragment: BaseGuidedFragment(), ModuleUtil.ModuleListener
{
    companion object {
        val TAG: String = ModulesFragment::class.java.simpleName
        fun newInstance() = ModulesFragment()
    }

    override fun onInstalledModulesReloaded(moduleUtil: ModuleUtil?) {
        doAsync {
            onUiThread {
                reloadModules()
            }
        }
    }

    override fun onSingleInstalledModuleReloaded(moduleUtil: ModuleUtil?, packageName: String?, module: ModuleUtil.InstalledModule?) {
        doAsync {
            onUiThread {
                reloadModules()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseModules().initModuleUtil(activity!!)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        BaseModules.mModuleUtil!!.addListener(this)
    }
    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.nav_item_modules),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        try {
            doAsync {
                val list = ModuleUtil.getInstance().modules.values
                val actionList = getActionsFromModuleList(activity!!, list)
                Log.v(XposedApp.TAG, "list: ${list.size}")
                onUiThread {
                    if (list.isNotEmpty())
                    actions.addAll(actionList)
                    }
            }
        }catch (npe: NullPointerException){
            error {"npe: ${npe.message}"}
        }catch (e: Exception){
            error {"e: ${e.message}"}
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        //val pos = action!!.id.toInt()
        val packageName = action?.title.toString()
        BaseModules().launchModule(activity!!, packageName)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        BaseModules.mModuleUtil!!.removeListener(this)
    }

    private fun reloadModules(){

    }

}