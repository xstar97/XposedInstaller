package de.robv.android.xposed.installer.tv.ui.fragments.list

import android.os.Bundle
import android.support.v17.leanback.widget.GuidanceStylist
import android.support.v17.leanback.widget.GuidedAction
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.base.fragments.BaseDevice
import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseGuidedFragment

class DeviceInfoFragment: BaseGuidedFragment()
{
    companion object {
        val TAG: String = DeviceInfoFragment::class.java.simpleName
        fun newInstance() = DeviceInfoFragment()
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.framework_device_info),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        try {
            val list = getActionsFromList(activity!!, BaseDevice().getDeviceList(activity!!))
            for (about in list) {
                actions.add(about)
            }
        }catch (npe: NullPointerException){
            error {"npe: ${npe.message}"}
        }catch (e: Exception){
            error {"e: ${e.message}"}
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        val pos = action!!.id.toInt()
        when (pos) {
            BaseDevice.deviceSdk -> {

            }
            BaseDevice.deviceManufacturer -> {

            }
            BaseDevice.deviceCpu -> {

            }
            BaseDevice.deviceVerified -> {

            }
        }
    }
}