package de.robv.android.xposed.installer.tv.ui.framework

import android.content.Intent
import android.os.Bundle
import androidx.leanback.widget.*
import androidx.core.content.ContextCompat
import android.util.Log
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseStatusInstaller
import de.robv.android.xposed.installer.core.logic.models.StatusModel
import de.robv.android.xposed.installer.core.logic.models.ZipModel
import de.robv.android.xposed.installer.core.util.FrameworkZips
import de.robv.android.xposed.installer.core.util.Loader
import de.robv.android.xposed.installer.tv.XposedApp
import de.robv.android.xposed.installer.tv.logic.Navigation
import de.robv.android.xposed.installer.tv.logic.presenters.CardPresenter
import de.robv.android.xposed.installer.tv.logic.presenters.TextViewPresenter
import de.robv.android.xposed.installer.tv.ui.installation.InstallationActivity
import de.robv.android.xposed.installer.tv.ui.base.ViewActivity
import de.robv.android.xposed.installer.tv.ui.base.ViewActivity.Companion.INTENT_NAV_KEY
import de.robv.android.xposed.installer.tv.ui.base.BaseBrowseSupportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class StatusInstallerBrowseFragment : BaseBrowseSupportFragment()
{
    companion object {
        val TAG: String = StatusInstallerBrowseFragment::class.java.simpleName
        fun newInstance() = StatusInstallerBrowseFragment()
        const val xposedHeaderItemPos = 0
        const val installerHeaderItemPos = 1
        const val unInstallerHeaderItemPos = 2
        const val settingsHeaderItemPos = 3
    }

    private val mOnlineZipListener = Loader.Listener<FrameworkZips.OnlineZipLoader> { reloadData() }
    private val mLocalZipListener = Loader.Listener<FrameworkZips.LocalZipLoader> { reloadData() }

    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        try {
            when (item) {
                is Navigation -> {
                    Log.d(XposedApp.TAG, "item: $item")
                    activity!!.startActivity<ViewActivity>(INTENT_NAV_KEY to item)
                }
                is ZipModel -> {
                    val title = item.key!!
                    val type = item.type
                    BaseStatusInstaller().showActionDialog(activity!!, Intent(activity!!, InstallationActivity::class.java), title, type)
                }
            }
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

        reloadData()

        BaseStatusInstaller().mOnlineZipLoader.addListener(mOnlineZipListener)
        BaseStatusInstaller().mOnlineZipLoader.triggerFirstLoadIfNecessary()

        BaseStatusInstaller().mLocalZipLoader.addListener(mLocalZipListener)
        BaseStatusInstaller().mLocalZipLoader.triggerFirstLoadIfNecessary()
        onItemViewClickedListener = this
        onItemViewSelectedListener = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BaseStatusInstaller().mOnlineZipLoader.removeListener(mOnlineZipListener)
        BaseStatusInstaller().mLocalZipLoader.removeListener(mLocalZipListener)
    }
    override fun onDestroy() {
        super.onDestroy()
        BaseStatusInstaller().mOnlineZipLoader.removeListener(mOnlineZipListener)
        BaseStatusInstaller().mLocalZipLoader.removeListener(mLocalZipListener)
    }

    private fun reloadData() {
        doAsync {
            val initZip = BaseStatusInstaller().getZips(activity!!)
            val myZips0 = initZip.first
            val myZips1 = initZip.second

            uiThread {
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
                        loadHeaderRows(myZips0, myZips1)
                    }
                }
            }
        }
    }

    private fun loadHeaderRows(zip0: ArrayList<ZipModel>, zip1: ArrayList<ZipModel>) {
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val mCardPresenter = CardPresenter(activity!!)
        val mGridPresenter = TextViewPresenter(activity!!)

        val xposedRowAdapter = ArrayObjectAdapter(mGridPresenter)
        val zipRowAdapter0 = ArrayObjectAdapter(mCardPresenter)
        val zipRowAdapter1 = ArrayObjectAdapter(mCardPresenter)
        val navRowAdapter = ArrayObjectAdapter(mCardPresenter)

        loadChildXposedRows(xposedRowAdapter, zip0, zip1)
        loadChildZipsRows(zipRowAdapter0, zipRowAdapter1, zip0, zip1)

        navRowAdapter.add(Navigation.FRAG_DEVICE)

        mRowsAdapter!!.add(ListRow(HeaderItem(xposedHeaderItemPos.toLong(), activity!!.getString(R.string.status)), xposedRowAdapter))
        mRowsAdapter!!.add(ListRow(HeaderItem(installerHeaderItemPos.toLong(), activity!!.getString(R.string.framework_install)), zipRowAdapter0))
        mRowsAdapter!!.add(ListRow(HeaderItem(unInstallerHeaderItemPos.toLong(), activity!!.getString(R.string.framework_uninstall)), zipRowAdapter1))
        mRowsAdapter!!.add(ListRow(HeaderItem(settingsHeaderItemPos.toLong(), activity!!.getString(R.string.nav_item_settings)), navRowAdapter))
        adapter = mRowsAdapter
    }
    private fun loadChildXposedRows(xposedRowAdapter: ArrayObjectAdapter, zip0: ArrayList<ZipModel>, zip1: ArrayList<ZipModel>){
        xposedRowAdapter.add(getXposedData())
        if (getErrorDescription(zip0, zip1)!!.statusMessage!!.isNotEmpty())
            xposedRowAdapter.add(getErrorDescription(zip0, zip1))
    }
    private fun loadChildZipsRows(zipRowAdapter0: ArrayObjectAdapter, zipRowAdapter1: ArrayObjectAdapter, zip0: ArrayList<ZipModel>, zip1: ArrayList<ZipModel>){
        zipRowAdapter0.clear()
        zipRowAdapter1.clear()
        for (zip in zip0) {
            zipRowAdapter0.add(ZipModel(zip.key, zip.icon, zip.type))
        }

        for (zip in zip1) {
            zipRowAdapter1.add(ZipModel(zip.key, zip.icon, zip.type))
        }
    }

    private fun getXposedData(): StatusModel? {
        return BaseStatusInstaller().getInstallerStatusData(activity!!)
    }
    private fun getErrorDescription(myZips0: ArrayList<ZipModel>, myZips1: ArrayList<ZipModel>): StatusModel?{
        return when {
            !FrameworkZips.hasLoadedOnlineZips() -> {
                val msg = activity!!.getString(R.string.framework_zip_load_failed)
                val backgroundColor = ContextCompat.getColor(activity!!, R.color.amber_500)
                StatusModel(statusMessage = msg, statusColor = null, statusContainerColor = backgroundColor, statusIcon = null, disableView = null)
            }
            myZips0.size == 0 || myZips1.size == 0 ->  {
                val msg = activity!!.getString(R.string.framework_no_zips)
                val backgroundColor = ContextCompat.getColor(activity!!, R.color.warning)
                StatusModel(statusMessage = msg, statusColor = null, statusContainerColor = backgroundColor, statusIcon = null, disableView = null)
            }
            else -> StatusModel(statusMessage = "", statusColor = null, statusContainerColor = null, statusIcon = null, disableView = null)
        }
    }

}