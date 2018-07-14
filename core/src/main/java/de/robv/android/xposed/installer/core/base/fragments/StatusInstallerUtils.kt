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
import de.robv.android.xposed.installer.core.repo.zips.ZipRepository
import de.robv.android.xposed.installer.core.repo.zips.Zips
import de.robv.android.xposed.installer.core.util.DownloadsUtil
import de.robv.android.xposed.installer.core.util.FrameworkZips
import de.robv.android.xposed.installer.core.util.RunnableWithParam
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.IOException

@Suppress("UNUSED_CHANGED_VALUE", "UNUSED_ANONYMOUS_PARAMETER")
open class StatusInstallerUtils
{
    companion object {

        val DISABLE_FILE = File(BaseXposedApp().BASE_DIR + "conf/disabled")

        fun checkClassExists(className: String): Boolean {
            return try {
                Class.forName(className)
                true
            } catch (e: ClassNotFoundException) {
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

        //actions
        fun flash(context: Context, install: Intent, flashable: Flashable) {
            //val install = Intent(context, InstallationActivity::class.java)
            install.putExtra(Flashable.KEY, flashable)
            context.startActivity(install)
        }

        fun confirmReboot(context: Context, contentTextId: Int, yesHandler: MaterialDialog.SingleButtonCallback) {
            MaterialDialog.Builder(context)
                    .content(R.string.reboot_confirmation)
                    .positiveText(contentTextId)
                    .negativeText(android.R.string.no)
                    .onPositive(yesHandler)
                    .show()
        }
        @Suppress("LocalVariableName")
        fun showActionDialog(context: Context, install: Intent, title: String, type: FrameworkZips.Type) {
            val ACTION_FLASH = 0
            val ACTION_FLASH_RECOVERY = 1
            val ACTION_SAVE = 2
            val ACTION_DELETE = 3

            val isDownloaded = FrameworkZips.hasLocal(title, type)
            val itemCount = if (isDownloaded) 3 else 2
            val texts = arrayOfNulls<String>(itemCount)
            val ids = IntArray(itemCount)
            var i = 0

            texts[i] = context.getString(type.text_flash)
            ids[i++] = ACTION_FLASH

            texts[i] = context.getString(type.text_flash_recovery)
            ids[i++] = ACTION_FLASH_RECOVERY

            //TODO add save function
            /*
            texts[i] = "Save to...";
            ids[i++] = ACTION_SAVE;
            */

            if (FrameworkZips.hasLocal(title, type)) {
                texts[i] = context.getString(R.string.framework_delete)
                ids[i++] = ACTION_DELETE
            }

            MaterialDialog.Builder(context)
                    .title(title)
                    .items(*texts)
                    .itemsIds(ids)
                    .itemsCallback(MaterialDialog.ListCallback { dialog, itemView, position, text ->
                        val action = itemView.id

                        // Handle delete simple actions.
                        if (action == ACTION_DELETE) {
                            FrameworkZips.delete(context, title, type)
                            LOCAL_ZIP_LOADER.triggerReload(true)
                            return@ListCallback
                        }

                        // Handle actions that need a download first.
                        var runAfterDownload: RunnableWithParam<File>? = null
                        when (action) {
                            ACTION_FLASH -> runAfterDownload = RunnableWithParam { file -> flash(context, install, FlashDirectly(file, type, title, false)) }
                            ACTION_FLASH_RECOVERY -> runAfterDownload = RunnableWithParam { file -> flash(context, install, FlashRecoveryAuto(file, type, title)) }
                            ACTION_SAVE -> runAfterDownload = RunnableWithParam { file -> saveTo(context, file) }
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
        fun download(context: Context, title: String, type: FrameworkZips.Type, callback: RunnableWithParam<File>?) {
            try {
                doAsync {
                    val zip = FrameworkZips.getOnline(title, type)
                    val zipTitle = zip.title
                    Log.d(BaseXposedApp.TAG, "title: $zipTitle")
                    uiThread {
                        DownloadsUtil.Builder(context)
                                .setTitle(zipTitle)
                                .setUrl(zip.url)
                                .setDestinationFromUrl(DownloadsUtil.DOWNLOAD_FRAMEWORK)
                                .setCallback { context, info ->
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

        //TODO allow user to choose download folder
        @Suppress("UNUSED_PARAMETER")
        private fun saveTo(context: Context, file: File) {
            Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    open class MyDataBaseUtil
    {
        val addZip = 0
        val updateZip = 1
        val table0 = "installer"
        val table1 = "uninstaller"

        fun initZipDB(db: ZipRepository) {
            val size0 = db.getAllZips(table0).size
            val size1 = db.getAllZips(table1).size

            synchronized(FrameworkZips::class.java) {
                if (size0 == 0 || size1 == 0) {
                    addZips(addZip, table0, table1, db)
                } else {
                    addZips(updateZip, table0, table1, db)
                }
            }
        }

        private fun addZips(addorUpdate: Int?, table0: String, table1: String, db: ZipRepository) {
            Log.v(BaseXposedApp.TAG, "adding zips...")
            doAsync {
                val allTitles0 = FrameworkZips.getAllTitles(FrameworkZips.Type.INSTALLER)
                for (title0 in allTitles0) {
                    val online = FrameworkZips.getOnline(title0, FrameworkZips.Type.INSTALLER)
                    val local = FrameworkZips.getLocal(title0, FrameworkZips.Type.INSTALLER)

                    val hasLocal = local != null
                    val hasOnline = online != null
                    val zip = if (!hasLocal) online else local

                    val myTitle = zip!!.title
                    val iconStatus = getIconStatus(hasLocal, hasOnline)
                    val zipType = 0

                    if (addorUpdate == addZip) {
                        db.addZip(table0, Zips(myTitle, iconStatus, zipType))
                    } else {
                        db.updateZip(table0, Zips(myTitle, iconStatus, zipType))
                    }
                }
            }

            doAsync {
                val allTitles1 = FrameworkZips.getAllTitles(FrameworkZips.Type.UNINSTALLER)
                for (title1 in allTitles1) {
                    val online = FrameworkZips.getOnline(title1, FrameworkZips.Type.UNINSTALLER)
                    val local = FrameworkZips.getLocal(title1, FrameworkZips.Type.UNINSTALLER)

                    val hasLocal = local != null
                    val hasOnline = online != null
                    val zip = if (!hasLocal) online else local

                    val myTitle = zip!!.title
                    val iconStatus = getIconStatus(hasLocal, hasOnline)
                    val zipType = 1

                    if (addorUpdate == addZip) {
                        db.addZip(table1, Zips(myTitle, iconStatus, zipType))
                    } else {
                        db.updateZip(table1, Zips(myTitle, iconStatus, zipType))
                    }
                }
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
    }

}