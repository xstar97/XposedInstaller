package de.robv.android.xposed.installer.core.logic.base.fragments.download

import android.content.Context
import android.widget.Toast
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.logic.base.BaseXposedApp
import de.robv.android.xposed.installer.core.repo.Module
import de.robv.android.xposed.installer.core.repo.ModuleVersion
import de.robv.android.xposed.installer.core.util.DownloadsUtil
import de.robv.android.xposed.installer.core.util.HashUtil
import java.io.File

open class BaseDownloadDetailsVersions
{
    companion object {
        var module: Module? = null
    }

    open class DownloadModuleCallback(private val moduleVersion: ModuleVersion) : DownloadsUtil.DownloadFinishedCallback {

        override fun onDownloadFinished(context: Context,
                                        info: DownloadsUtil.DownloadInfo) {
            val localFile = File(info.localFilename)
            if (!localFile.isFile)
                return

            if (moduleVersion.md5sum != null && !moduleVersion.md5sum.isEmpty()) {
                try {
                    val actualMd5Sum = HashUtil.md5(localFile)
                    if (moduleVersion.md5sum != actualMd5Sum) {
                        Toast.makeText(context, context.getString(R.string.download_md5sum_incorrect, actualMd5Sum, moduleVersion.md5sum), Toast.LENGTH_LONG).show()
                        DownloadsUtil.removeById(context, info.id)
                        return
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, context.getString(R.string.download_could_not_read_file, e.message), Toast.LENGTH_LONG).show()
                    DownloadsUtil.removeById(context, info.id)
                    return
                }

            }

            val pm = context.packageManager
            val packageInfo = pm.getPackageArchiveInfo(info.localFilename, 0)

            if (packageInfo == null) {
                Toast.makeText(context, R.string.download_no_valid_apk, Toast.LENGTH_LONG).show()
                DownloadsUtil.removeById(context, info.id)
                return
            }

            if (packageInfo.packageName != moduleVersion.module.packageName) {
                Toast.makeText(context, context.getString(R.string.download_incorrect_package_name, packageInfo.packageName, moduleVersion.module.packageName), Toast.LENGTH_LONG).show()
                DownloadsUtil.removeById(context, info.id)
                return
            }

            BaseXposedApp.installApk(context, info)
        }
    }
}