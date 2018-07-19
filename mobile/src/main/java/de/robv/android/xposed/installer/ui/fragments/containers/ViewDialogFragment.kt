package de.robv.android.xposed.installer.ui.fragments.containers

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.NavigationPosition
import de.robv.android.xposed.installer.logic.Utils
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
        childFragmentManager.beginTransaction().replace(R.id.view_sheet_content, Utils().getFragment(getFragTag())).commitNowAllowingStateLoss()
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