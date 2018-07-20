package de.robv.android.xposed.installer.ui.fragments.containers

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.*

class ViewDialogFragment : DialogFragment() {

    companion object {
        val TAG: String = ViewDialogFragment::class.java.simpleName
        const val BUNDLE_DIALOG_KEY = "initDialog"
        fun newInstance(pos: NavigationPosition): ViewDialogFragment {
            val frag = ViewDialogFragment()
            val args = Bundle()
            args.putInt(BUNDLE_DIALOG_KEY, pos.getPos())
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
        val nav = NavigationPosition.values()
        val dialog = nav[getFragPos()].createFragment()
        childFragmentManager.beginTransaction().replace(R.id.view_sheet_content, dialog).commitNowAllowingStateLoss()
    }
    private fun getFragPos(): Int {
        val bundle = this.arguments
        if (bundle != null) {
            val i = bundle.getInt(BUNDLE_DIALOG_KEY, NavigationPosition.HOME.getPos())
            Log.d(XposedApp.TAG, "initSheet: $i")
            return i
        }
        return NavigationPosition.HOME.getPos()
    }

}