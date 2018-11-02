package de.robv.android.xposed.installer.mobile.ui.error

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.mvc.ErrorViewMvc
import kotlinx.android.synthetic.main.view_state.view.*

class ErrorViewMvcImp(private val context: Context, layoutInflater: LayoutInflater) : ErrorViewMvc, View.OnClickListener
{
    private var mRootView = layoutInflater.inflate(R.layout.view_state, null)
    private var stateDelegate : ErrorViewMvc.StateDelegate? = null

    init{
        mRootView.view_state_title.setOnClickListener(this)
    }

    override fun getRootView() = this.mRootView!!

    override fun setDelegate(delegate: ErrorViewMvc.StateDelegate) {
        stateDelegate = delegate
    }

    override fun onClick(view: View){
        val id = view.id
        when (id) {
            R.id.view_state_title ->{
             stateDelegate?.onErrorNavigate()
            }
        }
    }
}