package de.robv.android.xposed.installer.ui.fragments

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.NavigationPosition
import de.robv.android.xposed.installer.logic.createFragment
import de.robv.android.xposed.installer.logic.getTag

class ViewDialogFragment : DialogFragment() {

    companion object {
        val TAG: String = ViewDialogFragment::class.java.simpleName
        const val BUNDLE_DIALOG_KEY = "initDialog"
        fun newInstance(tag: NavigationPosition): ViewDialogFragment {
            val frag = ViewDialogFragment()
            val args = Bundle()
            args.putString(BUNDLE_DIALOG_KEY, tag.getTag())
            frag.arguments = args
            return frag
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_sheet, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDialogFragment()
    }
    private fun setDialogFragment(){
        val frag = when(getFragTag()){
            NavigationPosition.DOWNLOAD.getTag() -> NavigationPosition.DOWNLOAD.createFragment()
            NavigationPosition.MODULES.getTag() -> NavigationPosition.MODULES.createFragment()
            NavigationPosition.HOME.getTag() -> NavigationPosition.HOME.createFragment()
            NavigationPosition.LOGS.getTag() -> NavigationPosition.LOGS.createFragment()
            NavigationPosition.SETTINGS.getTag() -> NavigationPosition.SETTINGS.createFragment()
            NavigationPosition.SUPPORT.getTag() -> NavigationPosition.SUPPORT.createFragment()
            NavigationPosition.ABOUT.getTag() -> NavigationPosition.ABOUT.createFragment()
            else -> NavigationPosition.HOME.createFragment()
        }
        childFragmentManager.beginTransaction().replace(R.id.view_sheet_content, frag).commitNowAllowingStateLoss()
    }
    private fun getFragTag(): String {
        val bundle = this.arguments
        if (bundle != null) {
            val i = bundle.getString(BUNDLE_DIALOG_KEY, NavigationPosition.HOME.getTag())
            Log.d(XposedApp.TAG, "initSheet: $i")
            return i
        }
        return NavigationPosition.HOME.getTag()
    }

}