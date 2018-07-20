package de.robv.android.xposed.installer.ui.fragments.containers

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.logic.*

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
            args.putInt(BUNDLE_SHEET_KEY, tag.getPos())
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
        val nav = NavigationPosition.values()
        val sheet = nav[getFragPos()].createFragment()
        childFragmentManager.beginTransaction().replace(R.id.view_sheet_content, sheet).commitNowAllowingStateLoss()
    }
    private fun getFragPos(): Int {
        val bundle = this.arguments
         if (bundle != null) {
            val i = bundle.getInt(BUNDLE_SHEET_KEY, NavigationPosition.HOME.getPos())
            Log.d(XposedApp.TAG, "initSheet: $i")
            return i
        }
        return NavigationPosition.HOME.getPos()
    }
}