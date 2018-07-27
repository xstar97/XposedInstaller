package de.robv.android.xposed.installer.mobile.ui.fragments.containers

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.NavigationPosition
import de.robv.android.xposed.installer.mobile.logic.createFragment

class ViewDialogFragment : DialogFragment() {

    companion object {
        val TAG: String = ViewDialogFragment::class.java.simpleName
        const val BUNDLE_DIALOG_KEY = "initDialog"
        fun newInstance(nav: NavigationPosition): ViewDialogFragment {
            val frag = ViewDialogFragment()
            val args = Bundle()
            args.putSerializable(BUNDLE_DIALOG_KEY, nav)
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
        childFragmentManager.beginTransaction().replace(R.id.view_sheet_content, getFrag().createFragment()).commitNowAllowingStateLoss()
    }
    private fun getFrag(): NavigationPosition{
        val bundle = this.arguments ?: return NavigationPosition.ERROR
        val i = bundle.get(BUNDLE_DIALOG_KEY) as NavigationPosition
        Log.d(XposedApp.TAG, "$BUNDLE_DIALOG_KEY: ${getString(i.title)}")
        return i
    }
}