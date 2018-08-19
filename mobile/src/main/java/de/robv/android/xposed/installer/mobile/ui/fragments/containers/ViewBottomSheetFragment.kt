package de.robv.android.xposed.installer.mobile.ui.fragments.containers

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import de.robv.android.xposed.installer.mobile.XposedApp
import de.robv.android.xposed.installer.mobile.logic.Navigation
import de.robv.android.xposed.installer.mobile.logic.createFragment
import de.robv.android.xposed.installer.mobile.ui.anko.ContainerViewUI
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.support.v4.find

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
    private lateinit var containerView : ContainerViewUI<BottomSheetDialogFragment>
    private lateinit var frameLayout: FrameLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        containerView = ContainerViewUI(activity!!)
        return this.containerView.createView(AnkoContext.create(activity!!, this))
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSheetFragment()
    }
    private fun setSheetFragment(){
        frameLayout = find(ContainerViewUI.Ids.baseViewFL)
        childFragmentManager.beginTransaction().replace(frameLayout.id, getFrag().createFragment()).commit()
    }
    private fun getFrag(): Navigation{
        val bundle = this.arguments ?: return Navigation.FRAG_ERROR
        val i = bundle.get(BUNDLE_SHEET_KEY) as Navigation
        Log.d(XposedApp.TAG, "$BUNDLE_SHEET_KEY: ${getString(i.title)}")
        return i
    }
}