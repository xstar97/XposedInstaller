package de.robv.android.xposed.installer.mobile.logic.adapters.module

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.base.fragments.BaseModules
import de.robv.android.xposed.installer.core.util.ModuleUtil
import de.robv.android.xposed.installer.mobile.logic.ThemeUtil

class ModuleAdapter(context: Context) : ArrayAdapter<ModuleUtil.InstalledModule>(context, R.layout.list_item_module, R.id.moduleTitle) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        if (convertView == null) {
            // The reusable view was created for the first time, set up the
            // listener on the checkbox
            view.findViewById<CheckBox>(R.id.moduleCheckbox).setOnCheckedChangeListener { buttonView, isChecked ->
                val packageName = buttonView.tag as String
                BaseModules().isModuleEnabled(packageName, isChecked)
            }
        }

        val item = getItem(position)!!
        val version = view.findViewById<TextView>(R.id.moduleVersionName)
        val initWarning = BaseModules().getModuleWarnDescription(context, item.minVersion, item.isInstalledOnExternalStorage)

        version.text = item.versionName

        // Store the package name in some views' tag for later access
        view.findViewById<View>(R.id.moduleCheckbox).tag = item.packageName
        view.tag = item.packageName

        view.findViewById<ImageView>(R.id.moduleIcon).setImageDrawable(item.icon)

        val descriptionText = view.findViewById<TextView>(R.id.moduleDescription)
        if (!item.description.isEmpty()) {
            descriptionText.text = item.description
            descriptionText.setTextColor(ThemeUtil.getThemeColor(context, android.R.attr.textColorSecondary))
        } else {
            descriptionText.text = context.getString(R.string.module_empty_description)
            val warningColor = ContextCompat.getColor(context, R.color.warning)
            descriptionText.setTextColor(warningColor)
        }

        val checkbox = view.findViewById<CheckBox>(R.id.moduleCheckbox)
        checkbox.isChecked = BaseModules.mModuleUtil!!.isModuleEnabled(item.packageName)
        val warningText = view.findViewById<TextView>(R.id.moduleWarning)
        checkbox.isEnabled = initWarning.second
        warningText.text = initWarning.first
        warningText.visibility = if(!initWarning.second) View.VISIBLE else View.GONE
        return view
    }
}