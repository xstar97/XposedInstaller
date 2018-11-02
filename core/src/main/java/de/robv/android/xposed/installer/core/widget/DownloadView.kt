package de.robv.android.xposed.installer.core.widget

import android.app.DownloadManager
import android.content.Context
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.util.DownloadsUtil
import de.robv.android.xposed.installer.core.util.DownloadsUtil.DownloadFinishedCallback
import de.robv.android.xposed.installer.core.util.DownloadsUtil.DownloadInfo
import kotlinx.android.synthetic.main.download_view.view.*

class DownloadView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs)
{
    private lateinit var mDownloadBtn: Button
    private lateinit var mDownloadBtnCancel: Button
    private lateinit var mInstallBtn: Button
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mTxtInfo: TextView
    var fragment: Fragment? = null
    private var mInfo: DownloadInfo? = null
    var url: String? = null
        set(url) {
            field = url

            mInfo = if (this.url != null)
                DownloadsUtil.getLatestForUrl(context, this.url)
            else
                null

            refreshView()
        }
    private val refreshViewRunnable = Runnable {
        when {
            url == null -> {
                mDownloadBtn.visibility = View.GONE
                mDownloadBtnCancel.visibility = View.GONE
                mInstallBtn.visibility = View.GONE
                mProgressBar.visibility = View.GONE
                mTxtInfo.visibility = View.VISIBLE
                //mTxtInfo.setText(R.string.download_view_no_url)
                mTxtInfo.text = context.getString(R.string.download_view_no_url)
            }
            mInfo == null -> {
                mDownloadBtn.visibility = View.VISIBLE
                mDownloadBtnCancel.visibility = View.GONE
                mInstallBtn.visibility = View.GONE
                mProgressBar.visibility = View.GONE
                mTxtInfo.visibility = View.GONE
            }
            else -> when (mInfo!!.status) {
                DownloadManager.STATUS_PENDING, DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_RUNNING -> {
                    mDownloadBtn.visibility = View.GONE
                    mDownloadBtnCancel.visibility = View.VISIBLE
                    mInstallBtn.visibility = View.GONE
                    mProgressBar.visibility = View.VISIBLE
                    mTxtInfo.visibility = View.VISIBLE
                    if (mInfo!!.totalSize <= 0 || mInfo!!.status != DownloadManager.STATUS_RUNNING) {
                        mProgressBar.isIndeterminate = true
                        mTxtInfo.setText(R.string.download_view_waiting)
                    } else {
                        mProgressBar.isIndeterminate = false
                        mProgressBar.max = mInfo!!.totalSize
                        mProgressBar.progress = mInfo!!.bytesDownloaded
                        mTxtInfo.text = getContext().getString(
                                R.string.download_view_running,
                                mInfo!!.bytesDownloaded / 1024,
                                mInfo!!.totalSize / 1024)
                    }
                }

                DownloadManager.STATUS_FAILED -> {
                    mDownloadBtn.visibility = View.VISIBLE
                    mDownloadBtnCancel.visibility = View.GONE
                    mInstallBtn.visibility = View.GONE
                    mProgressBar.visibility = View.GONE
                    mTxtInfo.visibility = View.VISIBLE
                    mTxtInfo.text = getContext().getString(
                            R.string.download_view_failed, mInfo!!.reason)
                }

                DownloadManager.STATUS_SUCCESSFUL -> {
                    mDownloadBtn.visibility = View.GONE
                    mDownloadBtnCancel.visibility = View.GONE
                    mInstallBtn.visibility = View.VISIBLE
                    mProgressBar.visibility = View.GONE
                    mTxtInfo.visibility = View.VISIBLE
                    mTxtInfo.setText(R.string.download_view_successful)
                }
            }
        }
    }
    var title: String? = null
    var downloadFinishedCallback: DownloadFinishedCallback? = null

    init {
        isFocusable = false
        orientation = LinearLayout.VERTICAL

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.download_view, this, true)

        mDownloadBtn = btnDownload//findViewById<View>(R.id.btnDownload) as Button
        mDownloadBtnCancel = btnDownloadCancel//findViewById<View>(R.id.btnDownloadCancel) as Button
        mInstallBtn = btnInstall//findViewById<View>(R.id.btnInstall) as Button

        mDownloadBtn.setOnClickListener {
            mInfo = DownloadsUtil.addModule(getContext(), title, url, downloadFinishedCallback)
            refreshViewFromUiThread()

            if (mInfo != null)
                DownloadMonitor().start()
        }

        mDownloadBtnCancel.setOnClickListener(OnClickListener {
            if (mInfo == null)
                return@OnClickListener

            DownloadsUtil.removeById(getContext(), mInfo!!.id)
            // UI update will happen automatically by the DownloadMonitor
        })

        mInstallBtn.setOnClickListener(OnClickListener {
            if (downloadFinishedCallback == null)
                return@OnClickListener
            Log.d("test", "show menu")
            downloadFinishedCallback!!.onDownloadFinished(getContext(), mInfo)
        })

        mProgressBar = progress//findViewById<View>(R.id.progress) as ProgressBar
        mTxtInfo = txtInfo//findViewById<View>(R.id.txtInfo) as TextView

        refreshViewFromUiThread()
    }

    private fun refreshViewFromUiThread() {
        refreshViewRunnable.run()
    }

    private fun refreshView() {
        post(refreshViewRunnable)
    }

    private inner class DownloadMonitor : Thread("DownloadMonitor") {

        override fun run() {
            while (true) {
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    return
                }

                try {
                    mInfo = DownloadsUtil.getById(context, mInfo!!.id)
                } catch (ignored: NullPointerException) {
                }

                refreshView()
                if (mInfo == null)
                    return

                if (mInfo!!.status != DownloadManager.STATUS_PENDING
                        && mInfo!!.status != DownloadManager.STATUS_PAUSED
                        && mInfo!!.status != DownloadManager.STATUS_RUNNING)
                    return
            }
        }
    }
}
