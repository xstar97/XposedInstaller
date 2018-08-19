package de.robv.android.xposed.installer.mobile.ui.fragments.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.core.base.fragments.BaseDevice
import de.robv.android.xposed.installer.core.delegates.InfoDelegate
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter.Companion.mSectionDevice
import de.robv.android.xposed.installer.mobile.ui.anko.BaseViewUI
import org.jetbrains.anko.AnkoContext

class DeviceInfoFragment: Fragment(), InfoDelegate
{
    companion object {
        val TAG: String = DeviceInfoFragment::class.java.simpleName
        fun newInstance() = DeviceInfoFragment()
    }

    private lateinit var baseUI : BaseViewUI<Fragment>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val section = mSectionDevice
        val list = BaseDevice().getDeviceList(activity!!)
        baseUI = BaseViewUI(activity!!, this, section, list)
        return this.baseUI.createView(AnkoContext.create(activity!!, this))
    }

    override fun onItemClick(infoItem: InfoModel) {
        //TODO add xda links for specific items...
    }
}