package de.robv.android.xposed.installer.core.base.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.installation.FlashDirectly
import de.robv.android.xposed.installer.core.installation.FlashRecoveryAuto
import de.robv.android.xposed.installer.core.installation.Flashable
import de.robv.android.xposed.installer.core.repo.zips.ZipModel
import de.robv.android.xposed.installer.core.util.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.IOException

open class BaseStatusInstaller
{
    companion object {

        val DISABLE_FILE = File(BaseXposedApp().BASE_DIR + "conf/disabled")

        fun checkClassExists(className: String): Boolean {
            return try {
                Class.forName(className)
                true
            }catch (e: ClassNotFoundException) {
                false
            }

        }

        @SuppressLint("StaticFieldLeak")
        val ONLINE_ZIP_LOADER = FrameworkZips.OnlineZipLoader.getInstance()!!
        @SuppressLint("StaticFieldLeak")
        val LOCAL_ZIP_LOADER = FrameworkZips.LocalZipLoader.getInstance()!!

        fun getPathWithCanonicalPath(file: File, canonical: File): String {
            return if (file == canonical) {
                file.absolutePath
            } else {
                file.absolutePath + " \u2192 " + canonical.absolutePath
            }
        }
        fun getCanonicalFile(file: File): File {
            return try {
                file.canonicalFile
            } catch (e: IOException) {
                Log.e(BaseXposedApp.TAG, "Failed to get canonical file for " + file.absolutePath, e)
                file
            }

        }

        private val zipList0 = ArrayList<ZipModel>()
        private val zipList1 = ArrayList<ZipModel>()

        //call this method with a doAsync block and any ui updates in a uithread block....
        fun getZips(): Pair<ArrayList<ZipModel>, ArrayList<ZipModel>>{
            zipList0.clear()
            zipList1.clear()
            return synchronized(FrameworkZips::class.java) {
                 try {
                    //Log.d(BaseXposedApp.TAG, "size 0: ${zipList0.size}\nsize 1: ${zipList1.size}")
                    val allTitles0 = FrameworkZips.getAllTitles(FrameworkZips.Type.INSTALLER)
                    val allTitles1 = FrameworkZips.getAllTitles(FrameworkZips.Type.UNINSTALLER)
                    for (title0 in allTitles0) {
                        val online = FrameworkZips.getOnline(title0, FrameworkZips.Type.INSTALLER)
                        val local = FrameworkZips.getLocal(title0, FrameworkZips.Type.INSTALLER)

                        val hasLocal = local != null
                        val hasOnline = online != null
                        val zip = if (!hasLocal) online else local

                        val myTitle = zip!!.title
                        val iconStatus = getIconStatus(hasLocal, hasOnline)
                        val zipType = 0

                        //Log.d(BaseXposedApp.TAG, "title: $myTitle")
                        zipList0.add(ZipModel(myTitle, iconStatus, zipType))
                    }
                    for (title1 in allTitles1) {
                        val online = FrameworkZips.getOnline(title1, FrameworkZips.Type.UNINSTALLER)
                        val local = FrameworkZips.getLocal(title1, FrameworkZips.Type.UNINSTALLER)

                        val hasLocal = local != null
                        val hasOnline = online != null
                        val zip = if (!hasLocal) online else local

                        val myTitle = zip!!.title
                        val iconStatus = getIconStatus(hasLocal, hasOnline)
                        val zipType = 1

                        zipList1.add(ZipModel(myTitle, iconStatus, zipType))
                    }
                }catch (e: Exception) {
                    Log.d(BaseXposedApp.TAG, "size 0: ${zipList0.size}\nsize 1: ${zipList1.size}")
                }
                Pair(zipList0, zipList1)
            }
        }

        private fun getIconStatus(hasLocal: Boolean, hasOnline: Boolean): Int =
                if (!hasLocal) {
                    R.drawable.ic_cloud
                } else if (hasOnline) {
                    R.drawable.ic_cloud_download
                } else {
                    R.drawable.ic_cloud_off
                }

        //actions
        fun showOptimizedAppDialog(context: Context){
            MaterialDialog.Builder(context)
                    .title(R.string.dexopt_now)
                    .content(R.string.this_may_take_a_while)
                    .progress(true, 0)
                    .cancelable(false)
                    .showListener { dialog ->
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
                    }.show()
        }

        fun confirmReboot(context: Context, contentTextId: Int, yesHandler: MaterialDialog.SingleButtonCallback) {
            MaterialDialog.Builder(context)
                    .content(R.string.reboot_confirmation)
                    .positiveText(contentTextId)
                    .negativeText(android.R.string.no)
                    .onPositive(yesHandler)
                    .show()
        }

        @Suppress("UNUSED_ANONYMOUS_PARAMETER", "UNUSED_CHANGED_VALUE")
        fun showActionDialog(context: Context, install: Intent, title: String, type: FrameworkZips.Type) {
            val actionFlash = 0
            val actionFlashRecovery = 1
            val actionSave = 2
            val actionDelete = 3

            val isDownloaded = FrameworkZips.hasLocal(title, type)
            val itemCount = if (isDownloaded) 3 else 2
            val texts = arrayOfNulls<String>(itemCount)
            val ids = IntArray(itemCount)
            var i = 0

            texts[i] = context.getString(type.text_flash)
            ids[i++] = actionFlash

            texts[i] = context.getString(type.text_flash_recovery)
            ids[i++] = actionFlashRecovery

            //TODO add save function
            /*
            texts[i] = "Save to...";
            ids[i++] = ACTION_SAVE;
            */

            if (FrameworkZips.hasLocal(title, type)) {
                texts[i] = context.getString(R.string.framework_delete)
                //TODO rework this method
                ids[i++] = actionDelete
            }

            MaterialDialog.Builder(context)
                    .title(title)
                    .items(*texts)
                    .itemsIds(ids)
                    .itemsCallback(MaterialDialog.ListCallback { dialog, itemView, position, text ->
                        val action = itemView.id

                        // Handle delete simple actions.
                        if (action == actionDelete) {
                            FrameworkZips.delete(context, title, type)
                            LOCAL_ZIP_LOADER.triggerReload(true)
                            return@ListCallback
                        }

                        // Handle actions that need a download first.
                        var runAfterDownload: RunnableWithParam<File>? = null
                        when (action) {
                            actionFlash -> runAfterDownload = RunnableWithParam { file -> flash(context, install, FlashDirectly(file, type, title, false)) }
                            actionFlashRecovery -> runAfterDownload = RunnableWithParam { file -> flash(context, install, FlashRecoveryAuto(file, type, title)) }
                            actionSave -> runAfterDownload = RunnableWithParam { file -> saveTo(context, file) }
                        }

                        val local = FrameworkZips.getLocal(title, type)
                        if (local != null) {
                            runAfterDownload!!.run(local.path)
                        } else {
                            download(context, title, type, runAfterDownload)
                        }
                    })
                    .show()
        }

        @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        fun download(context: Context, title: String, type: FrameworkZips.Type, callback: RunnableWithParam<File>?) {
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
                                .setCallback { delegate, info ->
                                    LOCAL_ZIP_LOADER.triggerReload(true)
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
        fun flash(context: Context, install: Intent, flashable: Flashable) {
            //val install = Intent(context, InstallationActivity::class.java)
            install.putExtra(Flashable.KEY, flashable)
            context.startActivity(install)
        }

        //TODO allow user to choose download folder
        @Suppress("UNUSED_PARAMETER")
        private fun saveTo(context: Context, file: File) {
            Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

}