package de.robv.android.xposed.installer.mobile.ui.fragments.containers

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.NavigationPosition
import de.robv.android.xposed.installer.mobile.logic.createFragment

/**
 * BottomSheetFragment Container for all the fragments
 */
class ViewBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        val TAG: String = ViewBottomSheetFragment::class.java.simpleName
        const val BUNDLE_SHEET_KEY = "initSheet"
        fun newInstance(nav: NavigationPosition): ViewBottomSheetFragment {
            val frag = ViewBottomSheetFragment()
            val args = Bundle()
            args.putSerializable(BUNDLE_SHEET_KEY, nav)
            frag.arguments = args
            return frag
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v=  inflater.inflate(R.layout.view_sheet, container, false)
        setSheetFragment()
        return v
    }
    private fun setSheetFragment(){
        childFragmentManager.beginTransaction().replace(R.id.view_sheet_content, getFrag().createFragment()).commitNowAllowingStateLoss()
    }
    private fun getFrag(): NavigationPosition{
        val bundle = this.arguments ?: return NavigationPosition.ERROR
        val i = bundle.get(BUNDLE_SHEET_KEY) as NavigationPosition
        Log.d(XposedApp.TAG, "$BUNDLE_SHEET_KEY: ${getString(i.title)}")
        return i
    }
}