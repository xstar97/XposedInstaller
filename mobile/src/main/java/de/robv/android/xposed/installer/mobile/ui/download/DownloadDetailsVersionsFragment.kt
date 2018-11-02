package de.robv.android.xposed.installer.mobile.ui.download

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.TypedValue
import android.widget.TextView

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.repo.Module
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.mobile.logic.adapters.download.VersionsAdapter

class DownloadDetailsVersionsFragment : ListFragment()
{
    companion object {
        val TAG: String = DownloadDetailsVersionsFragment::class.java.simpleName
        fun newInstance() = DownloadDetailsVersionsFragment()
    }
    private var mActivity: DownloadDetailsActivity? = null
    private var module: Module? = null
    private var sAdapter: VersionsAdapter? = null

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
}
