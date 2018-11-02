package de.robv.android.xposed.installer.mobile.ui.base

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.R

import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.Navigation
import de.robv.android.xposed.installer.mobile.logic.createFragment

class ViewDialogFragment : DialogFragment() {

    companion object {
        val TAG: String = ViewDialogFragment::class.java.simpleName
        const val BUNDLE_DIALOG_KEY = "initDialog"
        fun newInstance(nav: Navigation): ViewDialogFragment {
            val frag = ViewDialogFragment()
            val args = Bundle()
            args.putSerializable(BUNDLE_DIALOG_KEY, nav)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.view_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialogFragment()
    }
    private fun setDialogFragment(){
        childFragmentManager.beginTransaction().replace(R.id.content, getFrag().createFragment()).commit()
    }
    private fun getFrag(): Navigation{
        val bundle = this.arguments ?: return Navigation.NAV_HOME
        val i = bundle.get(BUNDLE_DIALOG_KEY) as Navigation
        Log.d(XposedApp.TAG, "$BUNDLE_DIALOG_KEY: ${getString(i.title)}")
        return i
    }
}