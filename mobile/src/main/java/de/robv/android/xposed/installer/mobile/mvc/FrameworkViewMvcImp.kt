package de.robv.android.xposed.installer.mobile.mvc

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.models.ZipModel
import de.robv.android.xposed.installer.core.mvc.FrameworkViewMvc
import de.robv.android.xposed.installer.mobile.logic.Utils
import kotlinx.android.synthetic.main.fragment_status_installer.view.*
import kotlinx.android.synthetic.main.view_state.view.*
import kotlinx.android.synthetic.main.view_status_installer_actions.view.*

class FrameworkViewMvcImp(context: Context, layoutInflater: LayoutInflater) : FrameworkViewMvc, View.OnClickListener, PopupMenu.OnMenuItemClickListener
{
    private var mRootView = layoutInflater.inflate(R.layout.fragment_status_installer, null)
    private var frameworkSelectedListenerMvc : FrameworkViewMvc.FrameworkDelegate? = null
    private var myContext: Context? = context//null

    init{
        mRootView.fabStatusInstallerActions.setOnClickListener(this)
        mRootView.button_zip_spinner0.setOnClickListener(this)
        mRootView.button_zip_spinner1.setOnClickListener(this)
        mRootView.view_state_title.setOnClickListener(this)
    }

    override fun getRootView() = this.mRootView!!

    override fun setDelegate(delegate: FrameworkViewMvc.FrameworkDelegate){
        frameworkSelectedListenerMvc = delegate
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val id = item!!.itemId
        return when(id) {
            R.id.reboot, R.id.soft_reboot, R.id.reboot_recovery -> {
                frameworkSelectedListenerMvc?.onReboot(id)
                return true
            }
            R.id.dexopt_now -> {
                frameworkSelectedListenerMvc?.onOptimizedAppDialog()
                return true
            } else -> false
        }
    }

    override fun onClick(view: View){
        val id = view.id
        when (id) {
            R.id.button_zip_spinner0, R.id.button_zip_spinner1 -> {
                val isZip0 = id == R.id.button_zip_spinner0
                val zip = if(isZip0) mRootView.zip_spinner0.selectedItem as ZipModel else mRootView.zip_spinner1.selectedItem as ZipModel
                frameworkSelectedListenerMvc?.onFrameworkSelected(zip)
            }
            R.id.view_state_title ->{
                val uri = mRootView.view_state_title.tag as String
                frameworkSelectedListenerMvc?.onKnownIssueSelected(uri)
            }
            R.id.fabStatusInstallerActions ->{
                Utils().launchMenu(myContext!!, getRootView().fabStatusInstallerActions, R.menu.menu_installer, this).show()
            }
        }
    }
}