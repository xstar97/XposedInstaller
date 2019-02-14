package de.robv.android.xposed.installer.mobile.ui.error

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.mvc.ErrorViewMvc
import kotlinx.android.synthetic.main.view_state.view.*


//TODO add error dialog
class ErrorFragment: Fragment(), ErrorViewMvc.StateDelegate
{
    companion object {
        val TAG: String = ErrorFragment::class.java.simpleName
        fun newInstance() = ErrorFragment()
    }
    private lateinit var mErrorViewMvc : ErrorViewMvc

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mErrorViewMvc = ErrorViewMvcImp(activity!!, layoutInflater)
        mErrorViewMvc.setDelegate(this)
        return mErrorViewMvc.getRootView()
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        setIcon(R.drawable.ic_error)
        setTitle(R.string.error_fragment_message)
    }
    private fun setIcon(@DrawableRes icon: Int?){
        mErrorViewMvc.getRootView().view_state_icon.setImageResource(icon!!)
    }
    private fun setTitle(@StringRes title: Int?){
        mErrorViewMvc.getRootView().view_state_title.setText(title!!)
    }

    override fun onErrorNavigate() {
        activity!!.finish()
    }
}