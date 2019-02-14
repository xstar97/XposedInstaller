package de.robv.android.xposed.installer.mobile.logic.adapters.download

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.download.BaseDownloadDetailsVersions
import de.robv.android.xposed.installer.core.repo.ModuleVersion
import de.robv.android.xposed.installer.core.repo.ReleaseType
import de.robv.android.xposed.installer.core.repo.RepoParser
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.core.util.chrome.LinkTransformationMethod
import de.robv.android.xposed.installer.core.widget.DownloadView
import de.robv.android.xposed.installer.mobile.logic.ThemeUtil
import kotlinx.android.synthetic.main.list_item_version.view.*
import java.text.DateFormat
import java.util.*

class VersionsAdapter(context: Context, myActivity: Activity?, verFragment: Fragment, installed: ModuleUtil.InstalledModule?) : ArrayAdapter<ModuleVersion>(context, R.layout.list_item_version) {
    private val mDateFormatter = DateFormat
            .getDateInstance(DateFormat.SHORT)
    private val mColorRelTypeStable: Int = ThemeUtil.getThemeColor(context, android.R.attr.textColorTertiary)
    @Suppress("DEPRECATION")
    private val mColorRelTypeOthers: Int = ContextCompat.getColor(context, R.color.warning)//resources.getColor(R.color.warning)
    private val mColorInstalled: Int = ThemeUtil.getThemeColor(context, R.attr.download_status_installed)
    @Suppress("DEPRECATION")
    private val mColorUpdateAvailable: Int = context.resources.getColor(R.color.download_status_update_available)
    private val mTextInstalled: String = context.getString(R.string.download_section_installed) + ":"
    private val mTextUpdateAvailable: String = context.getString(R.string.download_section_update_available) + ":"
    private val mInstalledVersionCode: Int = installed?.versionCode ?: -1

    private var myFragment = verFragment
    private var mActivity =  myActivity

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_item_version, null, true)
            val viewHolder = ViewHolder()
            viewHolder.moduleStatus = view!!.txtStatus//view!!.findViewById<TextView>(R.id.txtStatus)
            viewHolder.moduleVersion = view.txtVersion//view.findViewById<TextView>(R.id.txtVersion)
            viewHolder.moduleRelType = view.txtRelType//view.findViewById<TextView>(R.id.txtRelType)
            viewHolder.moduleUploadDate = view.txtUploadDate//view.findViewById<TextView>(R.id.txtUploadDate)
            viewHolder.moduleDownloadView = view.downloadView//view.findViewById<DownloadView>(R.id.downloadView)
            viewHolder.moduleChangesTitle = view.txtChangesTitle//view.findViewById<TextView>(R.id.txtChangesTitle)
            viewHolder.moduleChanges = view.txtChanges//view.findViewById<TextView>(R.id.txtChanges)
            viewHolder.moduleDownloadView!!.fragment = this@VersionsAdapter.myFragment
            view.tag = viewHolder
        }

        val holder = view.tag as ViewHolder
        val item = getItem(position)

        holder.moduleVersion!!.text = item!!.name
        holder.moduleRelType!!.setText(item.relType.titleId)
        holder.moduleRelType!!.setTextColor(if (item.relType == ReleaseType.STABLE)
            mColorRelTypeStable
        else
            mColorRelTypeOthers)

        if (item.uploaded > 0) {
            holder.moduleUploadDate!!.text = mDateFormatter.format(Date(item.uploaded))
            holder.moduleUploadDate!!.visibility = View.VISIBLE
        } else {
            holder.moduleUploadDate!!.visibility = View.GONE
        }

        if (item.code <= 0 || mInstalledVersionCode <= 0
                || item.code < mInstalledVersionCode) {
            holder.moduleStatus!!.visibility = View.GONE
        } else if (item.code == mInstalledVersionCode) {
            holder.moduleStatus!!.text = mTextInstalled
            holder.moduleStatus!!.setTextColor(mColorInstalled)
            holder.moduleStatus!!.visibility = View.VISIBLE
        } else { // item.code > mInstalledVersionCode
            holder.moduleStatus!!.text = mTextUpdateAvailable
            holder.moduleStatus!!.setTextColor(mColorUpdateAvailable)
            holder.moduleStatus!!.visibility = View.VISIBLE
        }

        holder.moduleDownloadView!!.url = item.downloadLink
        holder.moduleDownloadView!!.title = ""//mActivity!!.module!!.name
        holder.moduleDownloadView!!.downloadFinishedCallback = BaseDownloadDetailsVersions.DownloadModuleCallback(item)

        if (item.changelog != null && !item.changelog.isEmpty()) {
            holder.moduleChangesTitle!!.visibility = View.VISIBLE
            holder.moduleChanges!!.visibility = View.VISIBLE

            if (item.changelogIsHtml) {
                holder.moduleChanges!!.text = RepoParser.parseSimpleHtml(context, item.changelog, holder.moduleChanges)
                holder.moduleChanges!!.transformationMethod = LinkTransformationMethod(mActivity!!)
                holder.moduleChanges!!.movementMethod = LinkMovementMethod.getInstance()
            } else {
                holder.moduleChanges!!.text = item.changelog
                holder.moduleChanges!!.movementMethod = null
            }

        } else {
            holder.moduleChangesTitle!!.visibility = View.GONE
            holder.moduleChanges!!.visibility = View.GONE
        }

        return view
    }

    internal class ViewHolder {
        var moduleStatus: TextView? = null
        var moduleVersion: TextView? = null
        var moduleRelType: TextView? = null
        var moduleUploadDate: TextView? = null
        var moduleDownloadView: DownloadView? = null
        var moduleChangesTitle: TextView? = null
        var moduleChanges: TextView? = null
    }
}