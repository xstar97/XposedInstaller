package de.robv.android.xposed.installer.core.logic.base.fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import de.robv.android.xposed.installer.core.logic.base.BaseXposedApp
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.installation.FlashDirectly
import de.robv.android.xposed.installer.core.installation.FlashRecoveryAuto
import de.robv.android.xposed.installer.core.installation.Flashable
import de.robv.android.xposed.installer.core.logic.models.ActionModel
import de.robv.android.xposed.installer.core.logic.models.StatusModel
import de.robv.android.xposed.installer.core.logic.models.ZipModel
import de.robv.android.xposed.installer.core.util.*
import org.jetbrains.anko.*
import java.io.File
import java.io.IOException

open class BaseStatusInstaller
{
    companion object {

        val DISABLE_FILE = File(BaseXposedApp().BASE_DIR + "conf/disabled")

        const val actionFlash = 0
        const val actionFlashRecovery = 1
        const val actionSave = 2
        const val actionDelete = 3

        private val zipList0 = ArrayList<ZipModel>()
        private val zipList1 = ArrayList<ZipModel>()

        private fun getIconStatus(hasLocal: Boolean, hasOnline: Boolean): Int =
                if (!hasLocal) {
                    R.drawable.ic_cloud
                } else if (hasOnline) {
                    R.drawable.ic_cloud_download
                } else {
                    R.drawable.ic_cloud_off
                }

    }

    open val mOnlineZipLoader = FrameworkZips.OnlineZipLoader.getInstance()!!
    open val mLocalZipLoader = FrameworkZips.LocalZipLoader.getInstance()!!

    open fun checkClassExists(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        }catch (e: ClassNotFoundException) {
            false
        }
    }

    open fun getPathWithCanonicalPath(file: File, canonical: File): String {
        return if (file == canonical) {
            file.absolutePath
        } else {
            file.absolutePath + " \u2192 " + canonical.absolutePath
        }
    }
    open fun getCanonicalFile(file: File): File {
        return try {
            file.canonicalFile
        } catch (e: IOException) {
            Log.e(BaseXposedApp.TAG, "Failed to get canonical file for " + file.absolutePath, e)
            file
        }

    }

    private fun getActions(context: Context, title: String, type: FrameworkZips.Type): ArrayList<ActionModel> {
        val isDownloaded = FrameworkZips.hasLocal(title, type)

        val actionFlashTitle = context.getString(type.text_flash)
        val actionFlashId = actionFlash

        val actionFlashRecoveryTitle = context.getString(type.text_flash_recovery)
        val actionFlashRecoveryId = actionFlashRecovery

        //TODO add save function

        val actionSaveTitle = "Save to..."
        val actionSaveId = actionSave


        val actionDeleteTitle = context.getString(R.string.framework_delete)
        val actionDeleteId = actionDelete

        val list  = ArrayList<ActionModel>()
        return when (isDownloaded) {
            true -> {
                list.add(ActionModel(actionFlashId, actionFlashTitle))
                list.add(ActionModel(actionFlashRecoveryId, actionFlashRecoveryTitle))
                list.add(ActionModel(actionDeleteId, actionDeleteTitle))
                list
            }
            else ->{
                list.add(ActionModel(actionFlashId, actionFlashTitle))
                list.add(ActionModel(actionFlashRecoveryId, actionFlashRecoveryTitle))
                //list.add(ActionModel(actionSaveId, actionSaveTitle))
                list
            }
        }
    }
    open fun showActionDialog(context: Context, install: Intent, title: String, type: FrameworkZips.Type){
        Log.v(BaseXposedApp.TAG, "showActionDialog...")
        try {
            val list = getActions(context, title, type)
            val listActions  = ArrayList<String>()

            for (a in list){
                listActions.add(a.key)
            }
            context.selector(title, listActions) { _, i ->
                val action = list[i].pos

                val runAfterDownload: RunnableWithParam<File>?

                val local = FrameworkZips.getLocal(title, type)
                when(action){
                    BaseStatusInstaller.actionFlash ->{
                        runAfterDownload = RunnableWithParam { file -> flash(context, install, FlashDirectly(file, type, title, false))}
                        if (local != null) { runAfterDownload.run(local.path) } else { download(context, title, type, runAfterDownload)}
                    }
                    BaseStatusInstaller.actionFlashRecovery ->{
                        runAfterDownload = RunnableWithParam { file -> flash(context, install, FlashRecoveryAuto(file, type, title))}
                        if (local != null) { runAfterDownload.run(local.path) } else { download(context, title, type, runAfterDownload)}
                    }
                    BaseStatusInstaller.actionSave ->{
                        runAfterDownload = RunnableWithParam { file -> saveTo(context, file)}
                        if (local != null) { runAfterDownload.run(local.path) } else { download(context, title, type, runAfterDownload)}
                    }
                    BaseStatusInstaller.actionDelete ->{
                        FrameworkZips.delete(context, title, type)
                        mLocalZipLoader.triggerReload(true)
                    }
                }
            }
        }catch (e: Exception){
            Log.e(BaseXposedApp.TAG, e.message)
        }catch (npe : KotlinNullPointerException){
            Log.e(BaseXposedApp.TAG, npe.message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.DONUT)
    open fun getKnownIssueData(context: Context):Pair<String, String>{
        val issueName: String?
        val issueLink: String?
        val appInfo = context.applicationInfo
        val baseDir = File(BaseXposedApp().BASE_DIR)
        val baseDirCanonical = BaseStatusInstaller().getCanonicalFile(baseDir)
        val baseDirActual = File(if (Build.VERSION.SDK_INT >= 24) appInfo.deviceProtectedDataDir else appInfo.dataDir)
        val baseDirActualCanonical = BaseStatusInstaller().getCanonicalFile(baseDirActual)
        val prop = BaseXposedApp.getXposedProp()
        val missingFeatures = prop?.missingInstallerFeatures

        return when{
            missingFeatures != null && !missingFeatures.isEmpty() ->{
                InstallZipUtil.reportMissingFeatures(missingFeatures)
                issueName = context.getString(R.string.installer_needs_update, context.getString(R.string.app_name))
                issueLink = context.getString(R.string.about_support)
                Pair(issueName, issueLink)
            }
            File("/system/framework/core.jar.jex").exists() ->{
                issueName = "Aliyun OS"
                issueLink = "https://forum.xda-developers.com/showpost.php?p=52289793&postcount=5"
                Pair(issueName, issueLink)
            }
            Build.VERSION.SDK_INT < 24 && (File("/data/miui/DexspyInstaller.jar").exists() || BaseStatusInstaller().checkClassExists("miui.dexspy.DexspyInstaller")) ->{
                issueName = "MIUI/Dexspy"
                issueLink = "https://forum.xda-developers.com/showpost.php?p=52291098&postcount=6"
                Pair(issueName, issueLink)
            }
            Build.VERSION.SDK_INT < 24 && File("/system/framework/twframework.jar").exists() ->{
                issueName = "Samsung TouchWiz ROM"
                issueLink = "https://forum.xda-developers.com/showthread.php?t=3034811"
                Pair(issueName, issueLink)
            }
            baseDirCanonical != baseDirActualCanonical ->{
                Log.e(BaseXposedApp.TAG, "Base directory: " + BaseStatusInstaller().getPathWithCanonicalPath(baseDir, baseDirCanonical))
                Log.e(BaseXposedApp.TAG, "Expected: " + BaseStatusInstaller().getPathWithCanonicalPath(baseDirActual, baseDirActualCanonical))
                issueName = context.getString(R.string.known_issue_wrong_base_directory, BaseStatusInstaller().getPathWithCanonicalPath(baseDirActual, baseDirActualCanonical))
                issueLink = "https://github.com/rovo89/XposedInstaller/issues/395"
                Pair(issueName, issueLink)
            }
            !baseDir.exists() ->{
                issueName = context.getString(R.string.known_issue_missing_base_directory)
                issueLink = "https://github.com/rovo89/XposedInstaller/issues/393"
                Pair(issueName, issueLink)
            }
            else ->{
                Pair("","")
            }
        }
    }
    open fun getInstallerStatusData(context: Context): StatusModel{
        val active = BaseXposedApp.getActiveXposedVersion()
        val installed = BaseXposedApp.getInstalledXposedVersion()
        val frameworkInstallErrors: String?
        val frameworkInstallErrorsColor: Int?
        val statusContainer: Int?
        val statusIcon: Drawable?
        return when {
            installed < 0 -> {
                frameworkInstallErrors = context.getString(R.string.framework_not_installed)
                frameworkInstallErrorsColor = ContextCompat.getColor(context,R.color.warning)
                statusContainer = ContextCompat.getColor(context, R.color.warning)
                statusIcon = ContextCompat.getDrawable(context,R.drawable.ic_error)
               val disableView = View.GONE
                StatusModel(frameworkInstallErrors, frameworkInstallErrorsColor, statusContainer, statusIcon, disableView)
            }
            installed != active -> {
                frameworkInstallErrors = context.getString(R.string.framework_not_active, BaseXposedApp.getXposedProp().version)
                frameworkInstallErrorsColor = ContextCompat.getColor(context,R.color.amber_500)
                statusContainer = ContextCompat.getColor(context, R.color.amber_500)
                statusIcon = ContextCompat.getDrawable(context,R.drawable.ic_warning)
                val disableView = -1
                StatusModel(frameworkInstallErrors, frameworkInstallErrorsColor, statusContainer, statusIcon, disableView)
            }
            else -> {
                frameworkInstallErrors = context.getString(R.string.framework_active, BaseXposedApp.getXposedProp().version)
                frameworkInstallErrorsColor = ContextCompat.getColor(context,R.color.darker_green)
                statusContainer = ContextCompat.getColor(context, R.color.darker_green)
                statusIcon = ContextCompat.getDrawable(context,R.drawable.ic_check_circle)
                val disableView = -1
                StatusModel(frameworkInstallErrors, frameworkInstallErrorsColor, statusContainer, statusIcon, disableView)
            }
        }
    }
    //call this method with a doAsync block and any ui updates in a uithread block....
    open fun getZips(context: Context): Pair<ArrayList<ZipModel>, ArrayList<ZipModel>>{
        zipList0.clear()
        zipList1.clear()
        return synchronized(FrameworkZips::class.java) {
            try {
                //Log.d(BaseXposedApp.TAG, "size 0: ${zipList0.size}\nsize 1: ${zipList1.size}")
                val type0 = FrameworkZips.Type.INSTALLER
                val type1 = FrameworkZips.Type.UNINSTALLER
                val allTitles0 = FrameworkZips.getAllTitles(type0)
                val allTitles1 = FrameworkZips.getAllTitles(type1)
                for (title0 in allTitles0) {
                    val online = FrameworkZips.getOnline(title0, type0)
                    val local = FrameworkZips.getLocal(title0, type0)

                    val hasLocal = local != null
                    val hasOnline = online != null
                    val zip = if (!hasLocal) online else local

                    val myTitle = zip!!.title
                    val iconStatus =  ContextCompat.getDrawable(context,getIconStatus(hasLocal, hasOnline))

                    //Log.d(BaseXposedApp.TAG, "title: $myTitle")
                    zipList0.add(ZipModel(myTitle, iconStatus, type0))
                }
                for (title1 in allTitles1) {
                    val online = FrameworkZips.getOnline(title1, type1)
                    val local = FrameworkZips.getLocal(title1, type1)

                    val hasLocal = local != null
                    val hasOnline = online != null
                    val zip = if (!hasLocal) online else local

                    val myTitle = zip!!.title
                    val iconStatus =  ContextCompat.getDrawable(context,getIconStatus(hasLocal, hasOnline))

                    zipList1.add(ZipModel(myTitle, iconStatus!!, type1))
                }
            }catch (e: Exception) {
                Log.d(BaseXposedApp.TAG, "size alltitles0: ${zipList0.size}\nsize alltitles1: ${zipList1.size}")
            }
            Pair(zipList0, zipList1)
        }
    }

    open fun showOptimizedAppDialog(context: Context){
        context.alert{
            titleResource = R.string.dexopt_now
            messageResource = R.string.this_may_take_a_while
            negativeButton(android.R.string.cancel) { dialog ->
                dialog.dismiss()
            }
            positiveButton(android.R.string.ok) { dialog ->
                object : Thread("dexopt") {
                    override fun run() {
                        val rootUtil = RootUtil()
                        if (!rootUtil.startShell()) {
                            dialog.dismiss()
                            NavUtil.showMessage(context, context.getString(R.string.root_failed))
                            return
                        }

                        rootUtil.execute("cmd package bg-dexopt-job", null)

                        dialog.dismiss()
                        BaseXposedApp.runOnUiThread { Toast.makeText(context, R.string.done, Toast.LENGTH_LONG).show() }
                    }
                }.start()
            }
            show()
        }
    }
    open fun showPrefWarnDialog(context: Context?){
    if (!BaseXposedApp.getPreferences().getBoolean(BaseSettings.prefInstallWarn, false)) {
        context!!.alert{
            titleResource = R.string.install_warning_title
            messageResource = R.string.install_warning
            negativeButton(android.R.string.ok) { dialog ->
                dialog.dismiss()
            }
            positiveButton(R.string.dont_show_again) { dialog ->
                BaseXposedApp.getPreferences().edit().putBoolean(BaseSettings.prefInstallWarn, true).apply()
                dialog.dismiss()
            }
            show()
        }
    }
}


    open fun confirmReboot(context: Context, contentTextId: Int, mode: RootUtil.RebootMode){
        context.alert{
            //titleResource = R.string.install_warning_title
            messageResource = R.string.reboot_confirmation
            negativeButton(android.R.string.cancel) { dialog ->
                dialog.dismiss()
            }
            positiveButton(contentTextId) { dialog ->
                RootUtil.reboot(mode, context)
                dialog.dismiss()
            }
            show()
        }
    }

    //actions
    private fun download(context: Context, title: String, type: FrameworkZips.Type, callback: RunnableWithParam<File>?) {
        try {
            doAsync {
                val zip = FrameworkZips.getOnline(title, type)
                val zipTitle = zip.title
                //Log.d(BaseXposedApp.TAG, "title: $zipTitle")
                uiThread {
                    DownloadsUtil.Builder(context)
                            .setTitle(zipTitle)
                            .setUrl(zip.url)
                            .setDestinationFromUrl(DownloadsUtil.DOWNLOAD_FRAMEWORK)
                            .setCallback { _, info ->
                                mLocalZipLoader.triggerReload(true)
                                callback!!.run(File(info.localFilename))
                            }
                            .setMimeType(DownloadsUtil.MIME_TYPES.ZIP)
                            .setDialog(true)
                            .download()
                }
            }
        }catch (e: Exception){
            Log.e(BaseXposedApp.TAG, e.message)
        }
    }
    private fun flash(context: Context, install: Intent, flashable: Flashable) {
        install.putExtra(Flashable.KEY, flashable)
        context.startActivity(install)
    }
    //TODO allow user to choose download folder
    @Suppress("UNUSED_PARAMETER")
    private fun saveTo(context: Context, file: File) {
        Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
    }
}