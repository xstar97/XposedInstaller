package de.robv.android.xposed.installer.tv.ui.download

import android.os.Bundle
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.tv.ui.base.BaseGuidedFragment

class DownloadGuidedFragment : BaseGuidedFragment()
{
    companion object {
        val TAG: String = DownloadGuidedFragment::class.java.simpleName
        fun newInstance() = DownloadGuidedFragment()
    }
    private val bluetoothServer = 0

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.nav_item_download),
                getString(R.string.app_name),
                "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        actions.add(setBluetoothAction())
        actions.add(setBluetoothFileAction())
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        val pos = action!!.id.toInt()

    }
    //actions
    private fun setBluetoothAction(): GuidedAction{
        return GuidedAction.Builder(activity!!)
                .id(bluetoothServer.toLong())
                .title("connect to bluetooth device")
                .description(getBluetoothDescription())
                .build()
    }
    private fun getBluetoothDescription(): String{
        return ""
    }
    private fun setBluetoothFileAction(): GuidedAction{
        return GuidedAction.Builder(activity!!)
                .id(bluetoothServer.toLong())
                .title("bluetooth files")
                .subActions(getActionsFromList(activity!!, getBluetoothFiles()))
                .build()
    }
    private fun getBluetoothFiles(): Array<String>{
        val files = arrayOf("","","")
        return files
    }
}