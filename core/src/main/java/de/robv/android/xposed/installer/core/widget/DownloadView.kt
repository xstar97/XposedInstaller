package de.robv.android.xposed.installer.core.widget

import android.app.DownloadManager
import android.content.Context
import android.support.v4.app.Fragment
import android.util.AttributeSet
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

class DownloadView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private lateinit var btnDownload: Button
    private lateinit var btnDownloadCancel: Button
    private lateinit var btnInstall: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var txtInfo: TextView
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
                btnDownload.visibility = View.GONE
                btnDownloadCancel.visibility = View.GONE
                btnInstall.visibility = View.GONE
                progressBar.visibility = View.GONE
                txtInfo.visibility = View.VISIBLE
                txtInfo.setText(R.string.download_view_no_url)
            }
            mInfo == null -> {
                btnDownload.visibility = View.VISIBLE
                btnDownloadCancel.visibility = View.GONE
                btnInstall.visibility = View.GONE
                progressBar.visibility = View.GONE
                txtInfo.visibility = View.GONE
            }
            else -> when (mInfo!!.status) {
                DownloadManager.STATUS_PENDING, DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_RUNNING -> {
                    btnDownload.visibility = View.GONE
                    btnDownloadCancel.visibility = View.VISIBLE
                    btnInstall.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    txtInfo.visibility = View.VISIBLE
                    if (mInfo!!.totalSize <= 0 || mInfo!!.status != DownloadManager.STATUS_RUNNING) {
                        progressBar.isIndeterminate = true
                        txtInfo.setText(R.string.download_view_waiting)
                    } else {
                        progressBar.isIndeterminate = false
                        progressBar.max = mInfo!!.totalSize
                        progressBar.progress = mInfo!!.bytesDownloaded
                        txtInfo.text = getContext().getString(
                                R.string.download_view_running,
                                mInfo!!.bytesDownloaded / 1024,
                                mInfo!!.totalSize / 1024)
                    }
                }

                DownloadManager.STATUS_FAILED -> {
                    btnDownload.visibility = View.VISIBLE
                    btnDownloadCancel.visibility = View.GONE
                    btnInstall.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    txtInfo.visibility = View.VISIBLE
                    txtInfo.text = getContext().getString(
                            R.string.download_view_failed, mInfo!!.reason)
                }

                DownloadManager.STATUS_SUCCESSFUL -> {
                    btnDownload.visibility = View.GONE
                    btnDownloadCancel.visibility = View.GONE
                    btnInstall.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    txtInfo.visibility = View.VISIBLE
                    txtInfo.setText(R.string.download_view_successful)
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

        btnDownload = findViewById<View>(R.id.btnDownload) as Button
        btnDownloadCancel = findViewById<View>(R.id.btnDownloadCancel) as Button
        btnInstall = findViewById<View>(R.id.btnInstall) as Button

        btnDownload.setOnClickListener {
            mInfo = DownloadsUtil.addModule(getContext(), title, url, downloadFinishedCallback)
            refreshViewFromUiThread()

            if (mInfo != null)
                DownloadMonitor().start()
        }

        btnDownloadCancel.setOnClickListener(OnClickListener {
            if (mInfo == null)
                return@OnClickListener

            DownloadsUtil.removeById(getContext(), mInfo!!.id)
            // UI update will happen automatically by the DownloadMonitor
        })

        btnInstall.setOnClickListener(OnClickListener {
            if (downloadFinishedCallback == null)
                return@OnClickListener

            downloadFinishedCallback!!.onDownloadFinished(getContext(), mInfo)
        })

        progressBar = findViewById<View>(R.id.progress) as ProgressBar
        txtInfo = findViewById<View>(R.id.txtInfo) as TextView

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

    companion object {
        var mClickedUrl: String? = null
    }
}
