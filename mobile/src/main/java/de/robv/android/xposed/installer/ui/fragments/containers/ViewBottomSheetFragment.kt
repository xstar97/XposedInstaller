package de.robv.android.xposed.installer.ui.fragments.containers

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.NavigationPosition
import de.robv.android.xposed.installer.logic.Utils
import de.robv.android.xposed.installer.logic.getTag

/**
 * BottomSheetFragment Container for all the fragments
 */
class ViewBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        val TAG: String = ViewBottomSheetFragment::class.java.simpleName
        const val BUNDLE_SHEET_KEY = "initSheet"
        fun newInstance(tag: NavigationPosition): ViewBottomSheetFragment {
            val frag = ViewBottomSheetFragment()
            val args = Bundle()
            args.putString(BUNDLE_SHEET_KEY, tag.getTag())
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
        childFragmentManager.beginTransaction().replace(R.id.view_sheet_content, Utils().getFragment(getFragTag())).commitNowAllowingStateLoss()
    }
    private fun getFragTag(): String {
        val bundle = this.arguments
         if (bundle != null) {
            val i = bundle.getString(BUNDLE_SHEET_KEY, NavigationPosition.HOME.getTag())
            Log.d(XposedApp.TAG, "initSheet: $i")
            return i
        }
        return NavigationPosition.HOME.getTag()
    }
}