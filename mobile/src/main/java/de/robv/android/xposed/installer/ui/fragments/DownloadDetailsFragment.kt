package de.robv.android.xposed.installer.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import de.robv.android.xposed.installer.ui.activities.DownloadDetailsActivity
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.repo.RepoParser
import de.robv.android.xposed.installer.core.util.NavUtil
import de.robv.android.xposed.installer.core.util.chrome.LinkTransformationMethod

class DownloadDetailsFragment : Fragment() {

    companion object {
        val TAG: String = DownloadFragment::class.java.simpleName
        fun newInstance() = DownloadFragment()
    }
    private var mActivity: DownloadDetailsActivity? = null

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mActivity = activity as DownloadDetailsActivity
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val module = mActivity!!.module ?: return null

        val view = inflater.inflate(R.layout.download_details, container, false)

        val title = view.findViewById<View>(R.id.download_title) as TextView
        title.text = module.name

        val author = view.findViewById<View>(R.id.download_author) as TextView
        if (module.author != null && !module.author.isEmpty())
            author.text = getString(R.string.download_author, module.author)
        else
            author.setText(R.string.download_unknown_author)

        val description = view
                .findViewById<View>(R.id.download_description) as TextView
        if (module.description != null) {
            if (module.descriptionIsHtml) {
                description.text = RepoParser.parseSimpleHtml(activity, module.description, description)
                description.transformationMethod = LinkTransformationMethod(activity)
                description.movementMethod = LinkMovementMethod.getInstance()
            } else {
                description.text = module.description
            }
        } else {
            description.visibility = View.GONE
        }

        val moreInfoContainer = view.findViewById<View>(R.id.download_moreinfo_container) as ViewGroup
        for (moreInfoEntry in module.moreInfo) {
            val moreInfoView = inflater.inflate(R.layout.download_moreinfo, moreInfoContainer, false)
            val txtTitle = moreInfoView.findViewById<View>(android.R.id.title) as TextView
            val txtValue = moreInfoView.findViewById<View>(android.R.id.message) as TextView

            txtTitle.text = moreInfoEntry.first + ":"
            txtValue.text = moreInfoEntry.second

            val link = NavUtil.parseURL(moreInfoEntry.second)
            if (link != null) {
                txtValue.setTextColor(txtValue.linkTextColors)
                moreInfoView.setOnClickListener { NavUtil.startURL(activity, link) }
            }

            moreInfoContainer.addView(moreInfoView)
        }

        return view
    }
}
