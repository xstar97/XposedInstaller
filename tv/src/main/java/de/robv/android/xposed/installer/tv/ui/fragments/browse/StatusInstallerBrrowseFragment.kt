package de.robv.android.xposed.installer.tv.ui.fragments.browse

import android.os.Bundle
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.BaseStatusInstaller
import de.robv.android.xposed.installer.core.models.NavModel
import de.robv.android.xposed.installer.core.models.ZipModel
import de.robv.android.xposed.installer.core.util.FrameworkZips
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseBrowseSupportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.onUiThread

class StatusInstallerBrrowseFragment : BaseBrowseSupportFragment()
{
    companion object {
        val TAG: String = StatusInstallerBrrowseFragment::class.java.simpleName
        fun newInstance() = StatusInstallerBrrowseFragment()
        const val installerHeaderItemPos = 0
        const val unInstallerHeaderItemPos = 1
        const val settingsHeaderItemPos = 2
    }

    private val mOnlineZipListener = Loader.Listener<FrameworkZips.OnlineZipLoader> { loadRows() }
    private val mLocalZipListener = Loader.Listener<FrameworkZips.LocalZipLoader> { loadRows() }

    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        try {

        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }
    }

    override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUIElements(activity!!.getString(R.string.nav_item_install))

        loadRows()

        BaseStatusInstaller().ONLINE_ZIP_LOADER.addListener(mOnlineZipListener)
        BaseStatusInstaller().ONLINE_ZIP_LOADER.triggerFirstLoadIfNecessary()

        BaseStatusInstaller().LOCAL_ZIP_LOADER.addListener(mLocalZipListener)
        BaseStatusInstaller().LOCAL_ZIP_LOADER.triggerFirstLoadIfNecessary()
        onItemViewClickedListener = this
        onItemViewSelectedListener = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BaseStatusInstaller().ONLINE_ZIP_LOADER.removeListener(mOnlineZipListener)
        BaseStatusInstaller().LOCAL_ZIP_LOADER.removeListener(mLocalZipListener)
    }
    override fun onDestroy() {
        super.onDestroy()
        BaseStatusInstaller().ONLINE_ZIP_LOADER.removeListener(mOnlineZipListener)
        BaseStatusInstaller().LOCAL_ZIP_LOADER.removeListener(mLocalZipListener)
    }


    private fun loadRows() {
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        mRowsAdapter!!.clear()
        val mZipPresenter = GridFrameworkPresenter()
        val mNavPresenter = GridNavPresenter()
        val zipRowAdapter0 = ArrayObjectAdapter(mZipPresenter)
        val zipRowAdapter1 = ArrayObjectAdapter(mZipPresenter)
        val navRowAdapter = ArrayObjectAdapter(mNavPresenter)
        doAsync {
            val zipList = BaseStatusInstaller().getZips(activity!!)

            for (zip in zipList.first) {
                zipRowAdapter0.add(ZipModel(zip.key, zip.icon, zip.type))
            }

            for (zip in zipList.second) {
                zipRowAdapter1.add(ZipModel(zip.key, zip.icon, zip.type))
            }
            navRowAdapter.add(NavModel(0, ContextCompat.getDrawable(activity!!, R.drawable.ic_info), activity!!.getString(R.string.framework_device_info)))
            navRowAdapter.add(NavModel(1, ContextCompat.getDrawable(activity!!, R.drawable.ic_nav_settings), activity!!.getString(R.string.nav_item_settings)))

            onUiThread {
                mRowsAdapter!!.add(ListRow(HeaderItem(installerHeaderItemPos.toLong(), activity!!.getString(R.string.framework_install)), zipRowAdapter0))
                mRowsAdapter!!.add(ListRow(HeaderItem(unInstallerHeaderItemPos.toLong(), activity!!.getString(R.string.framework_uninstall)), zipRowAdapter1))
                mRowsAdapter!!.add(ListRow(HeaderItem(settingsHeaderItemPos.toLong(), activity!!.getString(R.string.nav_item_settings)), navRowAdapter))
                adapter = mRowsAdapter
            }
        }
    }
    private fun reloadData() {
        doAsync {
            val initZip = BaseStatusInstaller().getZips(activity!!)
            val myZips0 = initZip.first
            val myZips1 = initZip.second

            onUiThread {
                //val tvError = v.findViewById(R.id.zips_load_error) as TextView
                Log.d(XposedApp.TAG, "size 0: ${myZips0.size}\nsize 1: ${myZips1.size}")
                when {
                    !FrameworkZips.hasLoadedOnlineZips() -> {
                        //zips_load_error.setText(R.string.framework_zip_load_failed)
                        //zips_load_error.visibility = View.VISIBLE
                    }
                    myZips0.size == 0 || myZips1.size == 0 -> {
                        //zips_load_error.setText(R.string.framework_no_zips)
                        //zips_load_error.visibility = View.VISIBLE
                    }
                    else -> {
                        //zips_load_error.visibility = View.GONE
                       //populateRows(myZips0, myZips1)
                    }
                }
            }
        }
    }
}