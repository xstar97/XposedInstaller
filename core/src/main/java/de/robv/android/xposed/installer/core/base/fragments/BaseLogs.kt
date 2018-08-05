package de.robv.android.xposed.installer.core.base.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat

import android.util.Log
import android.widget.Toast
import com.heinrichreimersoftware.androidissuereporter.IssueReporterLauncher
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.base.fragments.utils.LogsUtils

import org.jetbrains.anko.email
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

open class BaseLogs
{
    companion object
    {
        private val mFileErrorLog = File("${BaseXposedApp().BASE_DIR}/log/error.log")
        private val mFileErrorLogOld = File("${BaseXposedApp().BASE_DIR}/log/error.log.old")
        private val gitHubUser = "rovo89"
        private val gitHubRepo = "XposedInstaller"
    }

   open fun getLogs(context: Context): String{
        return try {
            val logs = LogsUtils().getLogsUtil(mFileErrorLog)
            if (logs.isEmpty())
                context.getString(R.string.log_is_empty)
            else
                logs
        }catch (e: Exception){
            Log.e(BaseXposedApp.TAG, e.message)
            return context.getString(R.string.logs_load_failed)
        }

    }

    open fun clear(context: Context): Boolean {
        return try {
            FileOutputStream(mFileErrorLog).close()
            mFileErrorLogOld.delete()
            Toast.makeText(context, R.string.logs_cleared,
                    Toast.LENGTH_SHORT).show()
            true
        } catch (e: IOException) {
            Toast.makeText(context, context.resources.getString(R.string.logs_clear_failed) + "n" + e.message, Toast.LENGTH_LONG).show()
            false
        }
    }
    open fun save(context: Context): File? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), BaseXposedApp.WRITE_EXTERNAL_PERMISSION)
            return null
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(context, R.string.sdcard_not_writable, Toast.LENGTH_LONG).show()
            return null
        }

        val now = Calendar.getInstance()
        val filename = String.format(
                "xposed_%s_%04d%02d%02d_%02d%02d%02d.log", "error",
                now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1,
                now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), now.get(Calendar.SECOND))

        val dir = context.getExternalFilesDir(null)

        if (!dir!!.exists()) dir.mkdir()

        val targetFile = File(dir, filename)

        return LogsUtils().saveUtil(context, mFileErrorLog, targetFile)
    }

    open fun sendGitHubReport(context: Context){
        try{
            IssueReporterLauncher.forTarget("Xstar97", gitHubRepo)
                    .theme(R.style.Theme_XposedInstaller_Dark)
                    .guestToken("0823ad6425a067409916abc6652b583abc445e61")
                    .guestEmailRequired(true)
                    .minDescriptionLength(20)
                    .putExtraInfo(getLogContent().first, getLogContent().second)
                    .homeAsUpEnabled(true)
                    .launch(context)
        }catch (e: Exception){
            Log.d(BaseXposedApp.TAG, e.message)
        }
    }

    open fun sendEmail(context: Context){
        context.email("", "Logs", getLogContent().second)
    }
    private fun getLogContent(): Pair<String, String>{
        var name = "error.log"
        var log = "Log Is Empty:("
        return try {
            if (mFileErrorLog.exists()) {
                name = mFileErrorLog.name
                log = mFileErrorLog.readText()
            }
            Pair(name, log)
        }catch (e: Exception){
            Log.e(BaseXposedApp.TAG, e.message)
            Pair(name, log)
        }
    }
}