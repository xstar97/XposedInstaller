package de.robv.android.xposed.installer.mobile.ui.fragments.download

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.TypedValue
import android.widget.TextView

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.repo.Module
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.mobile.logic.adapters.download.VersionsAdapter
import de.robv.android.xposed.installer.mobile.ui.activities.DownloadDetailsActivity

class DownloadDetailsVersionsFragment : ListFragment()
{
    companion object {
        private var sAdapter: VersionsAdapter? = null
        val TAG: String = DownloadDetailsVersionsFragment::class.java.simpleName
        fun newInstance() = DownloadDetailsVersionsFragment()
    }
    private var mActivity: DownloadDetailsActivity? = null
    private var module: Module? = null

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mActivity = activity as DownloadDetailsActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        module = mActivity!!.module
        if (module == null)
            return

        if (module!!.versions.isEmpty()) {
            setEmptyText(getString(R.string.download_no_versions))
            setListShown(true)
        } else {
            val repoLoader = RepoLoader.getInstance()
            if (!repoLoader.isVersionShown(module!!.versions[0])) {
                val txtHeader = TextView(activity)
                txtHeader.setText(R.string.download_test_version_not_shown)
                @Suppress("DEPRECATION")
                txtHeader.setTextColor(resources.getColor(R.color.warning))
                txtHeader.setOnClickListener {
                    mActivity!!.gotoPage(DownloadDetailsActivity.DOWNLOAD_SETTINGS) }
                listView.addHeaderView(txtHeader)
            }

            sAdapter = VersionsAdapter(activity!!, mActivity!!, this ,mActivity!!.installedModule)
            for (version in module!!.versions) {
                if (repoLoader.isVersionShown(version))
                    sAdapter!!.add(version)
            }
            listAdapter = sAdapter
        }

        val metrics = resources.displayMetrics
        val sixDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, metrics).toInt()
        val eightDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, metrics).toInt()
        listView.divider = null
        listView.dividerHeight = sixDp
        listView.setPadding(eightDp, eightDp, eightDp, eightDp)
        listView.clipToPadding = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapter = null
    }

/*
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

            XposedApp.installApk(context, info)
        }
    }*/
}
