package de.robv.android.xposed.installer.mobile.mvc

import android.view.LayoutInflater
import android.view.View
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.models.ZipModel
import de.robv.android.xposed.installer.core.mvc.FrameworkView
import kotlinx.android.synthetic.main.fragment_status_installer.view.*

class FrameworkViewImp(layoutInflater: LayoutInflater) : FrameworkView, View.OnClickListener {

    private var mRootView = layoutInflater.inflate(R.layout.fragment_status_installer, null)
    private var frameworkSelectedListener : FrameworkView.FrameworkDelegate? = null
    init{
        mRootView.button_zip_spinner0.setOnClickListener(this)
        mRootView.button_zip_spinner1.setOnClickListener(this)
        mRootView.framework_known_issue.setOnClickListener(this)
    }

    override fun getRootView() = this.mRootView

    override fun setDelegate(listener: FrameworkView.FrameworkDelegate){
        frameworkSelectedListener = listener
    }

    override fun onClick(view: View){
        val id = view.id
        when (id) {
            R.id.button_zip_spinner0, R.id.button_zip_spinner1 -> {
                val isZip0 = id == R.id.button_zip_spinner0
                val zip = if(isZip0) mRootView.zip_spinner0.selectedItem as ZipModel else mRootView.zip_spinner1.selectedItem as ZipModel
                frameworkSelectedListener?.onFrameworkSelected(zip)
            }
            R.id.framework_known_issue ->{
                val uri = mRootView.framework_known_issue.tag as String
                frameworkSelectedListener?.onKnownIssueSelected(uri)
            }
            else -> {

            }
        }

    }

}