package de.robv.android.xposed.installer.mobile.ui.base

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.R

import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.Navigation
import de.robv.android.xposed.installer.mobile.logic.createFragment

/**
 * BottomSheetFragment Container for all the fragments
 */
class ViewBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        val TAG: String = ViewBottomSheetFragment::class.java.simpleName
        const val BUNDLE_SHEET_KEY = "initSheet"
        fun newInstance(nav: Navigation): ViewBottomSheetFragment {
            val frag = ViewBottomSheetFragment()
            val args = Bundle()
            args.putSerializable(BUNDLE_SHEET_KEY, nav)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.view_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSheetFragment()
    }
    private fun setSheetFragment(){
        childFragmentManager.beginTransaction().replace(R.id.content, getFrag().createFragment()).commit()
    }
    private fun getFrag(): Navigation{
        val bundle = this.arguments ?: return Navigation.FRAG_ERROR
        val i = bundle.get(BUNDLE_SHEET_KEY) as Navigation
        Log.d(XposedApp.TAG, "$BUNDLE_SHEET_KEY: ${getString(i.title)}")
        return i
    }
}