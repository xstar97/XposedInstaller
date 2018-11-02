package de.robv.android.xposed.installer.mobile.ui.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseDevice
import de.robv.android.xposed.installer.core.logic.delegates.InfoDelegate
import de.robv.android.xposed.installer.core.logic.models.InfoModel
import de.robv.android.xposed.installer.core.logic.mvc.BaseViewMvc
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter.Companion.mSectionDevice

class DeviceInfoFragment: Fragment(), InfoDelegate
{
    companion object {
        val TAG: String = DeviceInfoFragment::class.java.simpleName
        fun newInstance() = DeviceInfoFragment()
    }

    private lateinit var mBaseViewMvc : BaseViewMvc

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val section = mSectionDevice
        val list = BaseDevice().getDeviceList(activity!!)
        mBaseViewMvc = BaseViewMvcImp(activity!!, layoutInflater, section, list, this)
        mBaseViewMvc.setDelegate(this)
        return mBaseViewMvc.getRootView()
    }

    override fun onItemClick(infoItem: InfoModel) {
        //TODO add xda links for specific items...
    }
}