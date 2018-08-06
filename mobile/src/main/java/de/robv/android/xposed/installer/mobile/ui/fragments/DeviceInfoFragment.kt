package de.robv.android.xposed.installer.mobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.core.base.fragments.BaseDevice
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter.Companion.SECTION_DEVICE
import de.robv.android.xposed.installer.mobile.ui.fragments.base.BaseViewFragment

class DeviceInfoFragment: BaseViewFragment()
{
    companion object {
        val TAG: String = DeviceInfoFragment::class.java.simpleName
        fun newInstance() = DeviceInfoFragment()
    }

    override fun onItemClick(infoItem: InfoModel) {
        //TODO add xda links for specific items...
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = BaseDevice().getDeviceInfoList(activity!!)
        initList(SECTION_DEVICE, list)
    }
}