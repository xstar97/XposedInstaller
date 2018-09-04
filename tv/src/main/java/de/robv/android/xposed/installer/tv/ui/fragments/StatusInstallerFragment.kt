package de.robv.android.xposed.installer.tv.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseDevice
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseStatusInstaller
import de.robv.android.xposed.installer.core.logic.models.StatusModel
import de.robv.android.xposed.installer.core.logic.models.ZipModel
import de.robv.android.xposed.installer.core.util.FrameworkZips
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.ui.activities.InstallationActivity
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseGuidedFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.onUiThread

class StatusInstallerFragment: BaseGuidedFragment()
{
    companion object {
        val TAG: String = StatusInstallerFragment::class.java.simpleName
        fun newInstance() = StatusInstallerFragment()
    }

    private val mOnlineZipListener = Loader.Listener<FrameworkZips.OnlineZipLoader> { getZipLists() }
    private val mLocalZipListener = Loader.Listener<FrameworkZips.LocalZipLoader> { getZipLists() }

    private val actionXposed = 0
    private val actionError = 1
    private val actionInstallUpdate = 2
    private val actionUninstall = 3
    private val actionYourDevice = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getZipLists()

        BaseStatusInstaller().mOnlineZipLoader.addListener(mOnlineZipListener)
        BaseStatusInstaller().mOnlineZipLoader.triggerFirstLoadIfNecessary()

        BaseStatusInstaller().mLocalZipLoader.addListener(mLocalZipListener)
        BaseStatusInstaller().mLocalZipLoader.triggerFirstLoadIfNecessary()
    }

    override fun onResume() {
        super.onResume()
        val xposed = findActionById(actionXposed.toLong())
        if (xposed != null){
            val myDescription = getXposedData().statusMessage!!
            val myIcon = getXposedData().statusIcon!!
            xposed.icon = myIcon
            xposed.description = myDescription
            notifyActionChanged(findActionPositionById(actionError.toLong()))
        }
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.nav_item_install),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        try {
            actions.add(getXposed())
            actions.add(getError())
            actions.add(getInstaller())
            actions.add(getUninstaller())
            actions.add(getDevice())
        }catch (npe: NullPointerException){
            Log.e(TAG,"npe: ${npe.message}")
        }catch (e: Exception){
            Log.e(TAG,"e: ${e.message}")
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        val pos = action!!.id.toInt()
        when (pos) {
            actionInstallUpdate -> {

            }
            actionUninstall -> {

            }
        }
    }
    override fun onSubGuidedActionClicked(action: GuidedAction?): Boolean {
        ///val pos = action!!.id.toInt()

        val title = action?.title.toString()
        val isInstaller = title.contains("version", ignoreCase = true)
        val isUninstaller = title.contains("uninstaller", ignoreCase = true)
        val type = if (isInstaller) FrameworkZips.Type.INSTALLER else FrameworkZips.Type.UNINSTALLER
        return when {
            isInstaller || isUninstaller -> {
                BaseStatusInstaller().showActionDialog(activity!!, Intent(activity!!, InstallationActivity::class.java), title, type)
                 true
            }
            else -> {//this is device info:p

                 false
            }
        }
        //return true
    }

    private fun getXposed(): GuidedAction {
        return GuidedAction.Builder(activity!!)
                .id(actionXposed.toLong())
                .title(activity!!.getString(R.string.app_name))
                .icon(getXposedData().statusIcon!!)
                .description(getXposedData().statusMessage)
                .build()
    }

    private fun getXposedData(): StatusModel{
        return BaseStatusInstaller().getInstallerStatusData(activity!!)
    }

    private fun getError(): GuidedAction{
        return GuidedAction.Builder(activity!!)
                .id(actionError.toLong())
                .description("")
                .enabled(false)
                .build()
    }
    private fun getErrorDescription(myZips0: ArrayList<ZipModel>, myZips1: ArrayList<ZipModel>): String{
        return when {
            !FrameworkZips.hasLoadedOnlineZips() -> activity!!.getString(R.string.framework_zip_load_failed)
            myZips0.size == 0 || myZips1.size == 0 ->  activity!!.getString(R.string.framework_no_zips)
            else -> "nothing to report:)"
        }
    }

    private fun getInstaller(): GuidedAction{
        return GuidedAction.Builder(activity!!)
                .id(actionInstallUpdate.toLong())
                .title(activity!!.getString(R.string.install_update))
                .build()
    }
    private fun getUninstaller(): GuidedAction{
        return GuidedAction.Builder(activity!!)
                .id(actionUninstall.toLong())
                .title(activity!!.getString(R.string.uninstall))
                .build()
    }

    private fun getDevice(): GuidedAction{
        val id = actionYourDevice.toLong()
        val title = activity!!.getString(R.string.framework_device_info)
        val list = BaseDevice().getDeviceList(activity!!)
        return GuidedAction.Builder(activity!!)
                .id(id)
                .title(title)
                .subActions(getActionsFromDeviceList(activity!!, list))
                .build()
    }

    private fun getZipLists(){
        try {
            doAsync {
                val myZips0 = BaseStatusInstaller().getZips(activity!!).first
                val myZips1 = BaseStatusInstaller().getZips(activity!!).second
                Log.v(XposedApp.TAG, "getZipLists: \nzip0: ${myZips0.size}\nzip1: ${myZips1.size}")
                onUiThread {
                    collapseAction(true)
                    val zip0 = findActionById(actionInstallUpdate.toLong())
                    val zip1 = findActionById(actionUninstall.toLong())
                    val error = findActionById(actionError.toLong())
                    if (zip0 != null){
                        zip0.subActions = getActionsFromZipList(activity!!, myZips0)
                        notifyActionChanged(findActionPositionById(actionInstallUpdate.toLong()))
                        Log.v(XposedApp.TAG, "getZipLists: \nzip0: ${myZips0.size}")
                    }

                    if (zip1 != null){
                        zip1.subActions = getActionsFromZipList(activity!!, myZips1)
                        notifyActionChanged(findActionPositionById(actionUninstall.toLong()))
                        Log.v(XposedApp.TAG, "getZipLists: \nzip1: ${myZips1.size}")
                    }

                    if (myZips0.size == 0 || myZips1.size == 0){
                        error.isEnabled = true
                        error.description = getErrorDescription(myZips0, myZips1)
                        notifyActionChanged(findActionPositionById(actionError.toLong()))
                    }else{
                        error.isEnabled = false
                    }
                }
            }
        }catch (e: Exception){
            Log.w(XposedApp.TAG, e.message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BaseStatusInstaller().mOnlineZipLoader.removeListener(mOnlineZipListener)
        BaseStatusInstaller().mLocalZipLoader.removeListener(mLocalZipListener)
    }
    override fun onDestroy() {
        super.onDestroy()
        BaseStatusInstaller().mOnlineZipLoader.removeListener(mOnlineZipListener)
        BaseStatusInstaller().mLocalZipLoader.removeListener(mLocalZipListener)
    }
}