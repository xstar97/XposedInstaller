package de.robv.android.xposed.installer.mobile.ui.download

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.download.BaseDownloadDetails
import de.robv.android.xposed.installer.core.repo.RepoParser
import de.robv.android.xposed.installer.core.util.NavUtil
import de.robv.android.xposed.installer.core.util.chrome.LinkTransformationMethod
import kotlinx.android.synthetic.main.list_item_details.view.*
import kotlinx.android.synthetic.main.list_item_moreinfo.view.*

class DownloadDetailsFragment : Fragment() {

    companion object {
        val TAG: String = DownloadDetailsFragment::class.java.simpleName
        fun newInstance() = DownloadDetailsFragment()
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
        val base = BaseDownloadDetails(activity!!, module)
        val view = inflater.inflate(R.layout.list_item_details, container, false)

       view!!.download_title.text = base.getTitle()//module.title

        view.download_author.text = base.getAuthor()

        val baseDescription = base.getDescription()
        val description = baseDescription!!.first
        val hasHtml = baseDescription.second
        val isVisible = baseDescription.third
        if (hasHtml){
            view.download_description.text = RepoParser.parseSimpleHtml(activity, module.description, view.download_description)
            view.download_description.transformationMethod = LinkTransformationMethod(activity!!)
            view.download_description.movementMethod = LinkMovementMethod.getInstance()
        }else {
            view.download_description.text = description
        }
        view.download_description.visibility = if (isVisible) View.VISIBLE else View.GONE

        for (moreInfoEntry in module.moreInfo) {
            val moreInfoView = inflater.inflate(R.layout.list_item_moreinfo, view.download_moreinfo_container, false)
            val txtTitle = moreInfoView.list_item_moreinfo_title//moreInfoView.findViewById<TextView>(R.id.list_item_moreinfo_title)
            val txtValue = moreInfoView.list_item_moreinfo_message//moreInfoView.findViewById<TextView>(R.id.list_item_moreinfo_message)

            txtTitle.text = moreInfoEntry.first + ":"
            txtValue.text = moreInfoEntry.second

            val link = NavUtil.parseURL(moreInfoEntry.second)
            if (link != null) {
                txtValue.setTextColor(txtValue.linkTextColors)
                moreInfoView.setOnClickListener { NavUtil.startURL(activity!!, link) }
            }

            view.download_moreinfo_container.addView(moreInfoView)
        }
        return view
    }
}
