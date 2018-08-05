package de.robv.android.xposed.installer.mobile.ui.fragments.download

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast

import java.io.File
import java.text.DateFormat
import java.util.Date

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.download.BaseDownloadDetailsVersions
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.core.repo.Module
import de.robv.android.xposed.installer.core.repo.ModuleVersion
import de.robv.android.xposed.installer.core.repo.ReleaseType
import de.robv.android.xposed.installer.core.repo.RepoParser
import de.robv.android.xposed.installer.core.util.DownloadsUtil
import de.robv.android.xposed.installer.core.util.HashUtil
import de.robv.android.xposed.installer.core.util.ModuleUtil.InstalledModule
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.mobile.logic.ThemeUtil
import de.robv.android.xposed.installer.core.util.chrome.LinkTransformationMethod
import de.robv.android.xposed.installer.core.widget.DownloadView
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
                txtHeader.setOnClickListener { mActivity!!.gotoPage(DownloadDetailsActivity.DOWNLOAD_SETTINGS) }
                listView.addHeaderView(txtHeader)
            }

            sAdapter = VersionsAdapter(mActivity!!, mActivity!!.installedModule)
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

    internal class ViewHolder {
        var txtStatus: TextView? = null
        var txtVersion: TextView? = null
        var txtRelType: TextView? = null
        var txtUploadDate: TextView? = null
        var downloadView: DownloadView? = null
        var txtChangesTitle: TextView? = null
        var txtChanges: TextView? = null
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

    private inner class VersionsAdapter(context: Context, installed: InstalledModule?) : ArrayAdapter<ModuleVersion>(context, R.layout.list_item_version) {
        private val mDateFormatter = DateFormat
                .getDateInstance(DateFormat.SHORT)
        private val mColorRelTypeStable: Int = ThemeUtil.getThemeColor(context, android.R.attr.textColorTertiary)
        @Suppress("DEPRECATION")
        private val mColorRelTypeOthers: Int = resources.getColor(R.color.warning)
        private val mColorInstalled: Int = ThemeUtil.getThemeColor(context, R.attr.download_status_installed)
        @Suppress("DEPRECATION")
        private val mColorUpdateAvailable: Int = resources.getColor(R.color.download_status_update_available)
        private val mTextInstalled: String = getString(R.string.download_section_installed) + ":"
        private val mTextUpdateAvailable: String = getString(R.string.download_section_update_available) + ":"
        private val mInstalledVersionCode: Int = installed?.versionCode ?: -1

        @SuppressLint("InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.list_item_version, null, true)
                val viewHolder = ViewHolder()
                viewHolder.txtStatus = view!!.findViewById<View>(R.id.txtStatus) as TextView
                viewHolder.txtVersion = view.findViewById<View>(R.id.txtVersion) as TextView
                viewHolder.txtRelType = view.findViewById<View>(R.id.txtRelType) as TextView
                viewHolder.txtUploadDate = view.findViewById<View>(R.id.txtUploadDate) as TextView
                viewHolder.downloadView = view.findViewById<View>(R.id.downloadView) as DownloadView
                viewHolder.txtChangesTitle = view.findViewById<View>(R.id.txtChangesTitle) as TextView
                viewHolder.txtChanges = view.findViewById<View>(R.id.txtChanges) as TextView

                viewHolder.downloadView!!.fragment = this@DownloadDetailsVersionsFragment
                view.tag = viewHolder
            }

            val holder = view.tag as ViewHolder
            val item = getItem(position)

            holder.txtVersion!!.text = item!!.name
            holder.txtRelType!!.setText(item.relType.titleId)
            holder.txtRelType!!.setTextColor(if (item.relType == ReleaseType.STABLE)
                mColorRelTypeStable
            else
                mColorRelTypeOthers)

            if (item.uploaded > 0) {
                holder.txtUploadDate!!.text = mDateFormatter.format(Date(item.uploaded))
                holder.txtUploadDate!!.visibility = View.VISIBLE
            } else {
                holder.txtUploadDate!!.visibility = View.GONE
            }

            if (item.code <= 0 || mInstalledVersionCode <= 0
                    || item.code < mInstalledVersionCode) {
                holder.txtStatus!!.visibility = View.GONE
            } else if (item.code == mInstalledVersionCode) {
                holder.txtStatus!!.text = mTextInstalled
                holder.txtStatus!!.setTextColor(mColorInstalled)
                holder.txtStatus!!.visibility = View.VISIBLE
            } else { // item.code > mInstalledVersionCode
                holder.txtStatus!!.text = mTextUpdateAvailable
                holder.txtStatus!!.setTextColor(mColorUpdateAvailable)
                holder.txtStatus!!.visibility = View.VISIBLE
            }

            holder.downloadView!!.url = item.downloadLink
            holder.downloadView!!.title = mActivity!!.module!!.name
            holder.downloadView!!.downloadFinishedCallback = BaseDownloadDetailsVersions.DownloadModuleCallback(item)

            if (item.changelog != null && !item.changelog.isEmpty()) {
                holder.txtChangesTitle!!.visibility = View.VISIBLE
                holder.txtChanges!!.visibility = View.VISIBLE

                if (item.changelogIsHtml) {
                    holder.txtChanges!!.text = RepoParser.parseSimpleHtml(activity, item.changelog, holder.txtChanges)
                    holder.txtChanges!!.transformationMethod = LinkTransformationMethod(activity!!)
                    holder.txtChanges!!.movementMethod = LinkMovementMethod.getInstance()
                } else {
                    holder.txtChanges!!.text = item.changelog
                    holder.txtChanges!!.movementMethod = null
                }

            } else {
                holder.txtChangesTitle!!.visibility = View.GONE
                holder.txtChanges!!.visibility = View.GONE
            }

            return view
        }
    }
}
