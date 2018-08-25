package de.robv.android.xposed.installer.mobile.logic.adapters.module

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.R.layout.list_item_module
import de.robv.android.xposed.installer.core.repo.Module

class BookmarkModuleAdapter(context: Context) : ArrayAdapter<Module>(context, list_item_module, R.id.moduleTitle) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        view.findViewById<CheckBox>(R.id.moduleCheckbox).visibility = View.GONE
        view.findViewById<TextView>(R.id.moduleVersionName).visibility = View.GONE
        view.findViewById<ImageView>(R.id.moduleIcon).visibility = View.GONE

        val item = getItem(position)

        view.findViewById<TextView>(R.id.moduleTitle).text = item!!.name.toString()
        view.findViewById<TextView>(R.id.moduleDescription).text = item.summary

        return view
    }
}