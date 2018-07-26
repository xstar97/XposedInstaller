package de.robv.android.xposed.installer.mobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.BaseDeviceInfo
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter.Companion.SECTION_DEVICE
import de.robv.android.xposed.installer.mobile.ui.fragments.base.BaseViewFragment

class DeviceInfoFragment: BaseViewFragment()
{
    companion object {
        val TAG: String = DeviceInfoFragment::class.java.simpleName
        fun newInstance() = DeviceInfoFragment()
    }

    private val adapter by lazy { InfoBaseAdapter(activity!!, this) }

    override fun onItemClick(infoItem: InfoModel) {
        //TODO add xda links for specific items...
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = BaseDeviceInfo.getDeviceInfoList(activity!!)
        initViews(adapter, SECTION_DEVICE, list)
    }
}