package de.robv.android.xposed.installer.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import de.robv.android.xposed.installer.R

import java.io.File

import java.io.FileOutputStream

import java.io.IOException
import java.util.Calendar

import de.robv.android.xposed.installer.core.util.LogsReader

import de.robv.android.xposed.installer.core.base.BaseXposedApp.BASE_PKG
import de.robv.android.xposed.installer.core.base.BaseXposedApp.WRITE_EXTERNAL_PERMISSION
import de.robv.android.xposed.installer.ui.fragments.utils.LogsUtil

//TODO fix from crashing when trying to save log!
class LogsFragment : Fragment(), LogsReader.onAsyncComplete
{
    companion object {
        val TAG: String = LogsFragment::class.java.simpleName
        fun newInstance() = LogsFragment()
    }
    private val mFileErrorLog = File("$BASE_PKG/log/error.log")
    private val mFileErrorLogOld = File(
            "$BASE_PKG/log/error.log.old")
    private var mTxtLog: TextView? = null
    private var mSVLog: ScrollView? = null
    private var mHSVLog: HorizontalScrollView? = null
    private var mClickedMenuItem: MenuItem? = null

    override fun getData(data: String) {
        mTxtLog?.text = data
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_logs, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        reloadErrorLog()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_logs, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        mClickedMenuItem = item
        when (item!!.itemId) {
            R.id.menu_scroll_top -> scrollTop()
            R.id.menu_scroll_down -> scrollDown()
            R.id.menu_refresh -> {
                reloadErrorLog()
                return true
            }
            R.id.menu_send -> {
                try {
                    send()
                } catch (ignored: NullPointerException) {
                }

                return true
            }
            R.id.menu_save -> {
                save()
                return true
            }
            R.id.menu_clear -> {
                clear()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun scrollTop() {
        mSVLog?.post { mSVLog?.scrollTo(0, 0) }
        mHSVLog?.post { mHSVLog?.scrollTo(0, 0) }
    }

    private fun scrollDown() {
        mSVLog?.post { mSVLog?.scrollTo(0, mTxtLog!!.height) }
        mHSVLog?.post { mHSVLog?.scrollTo(0, 0) }
    }

    private fun reloadErrorLog() {
        LogsReader(activity, this).execute(mFileErrorLog)
        mSVLog?.post { mSVLog?.scrollTo(0, mTxtLog!!.height) }
        mHSVLog?.post { mHSVLog?.scrollTo(0, 0) }
    }

    private fun clear() {
        try {
            FileOutputStream(mFileErrorLog).close()
            mFileErrorLogOld.delete()
            mTxtLog?.setText(R.string.log_is_empty)
            Toast.makeText(activity, R.string.logs_cleared,
                    Toast.LENGTH_SHORT).show()
            reloadErrorLog()
        } catch (e: IOException) {
            Toast.makeText(activity, resources.getString(R.string.logs_clear_failed) + "n" + e.message, Toast.LENGTH_LONG).show()
        }

    }

    private fun send() {
        val uri = FileProvider.getUriForFile(activity!!, "$BASE_PKG.fileprovider", mFileErrorLog)
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
        sendIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        sendIntent.type = "application/html"
        startActivity(Intent.createChooser(sendIntent, resources.getString(R.string.menuSend)))
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults)
        if (requestCode == WRITE_EXTERNAL_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mClickedMenuItem != null) {
                    Handler().postDelayed({ onOptionsItemSelected(mClickedMenuItem) }, 500)
                }
            } else {
                Toast.makeText(activity, R.string.permissionNotGranted, Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun save(): File? {
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_PERMISSION)
            return null
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(activity, R.string.sdcard_not_writable, Toast.LENGTH_LONG).show()
            return null
        }

        val now = Calendar.getInstance()
        val filename = String.format(
                "xposed_%s_%04d%02d%02d_%02d%02d%02d.log", "error",
                now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1,
                now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), now.get(Calendar.SECOND))

        val dir = activity!!.getExternalFilesDir(null)

        if (!dir!!.exists()) dir.mkdir()

        val targetFile = File(dir, filename)

        //TODO add portion to util class
         /*try {
            /*FileInputStream in = new FileInputStream(mFileErrorLog);
            FileOutputStream out = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();

            Toast.makeText(getActivity(), targetFile.toString(),
                    Toast.LENGTH_LONG).show();

            return targetFile;*/
        //} catch (IOException e) {
          //  Toast.makeText(getActivity(), getResources().getString(R.string.logs_save_failed) + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
         //   return null;
        //}*/
        return LogsUtil().saveUtil(activity, mFileErrorLog, targetFile)

    }

}
