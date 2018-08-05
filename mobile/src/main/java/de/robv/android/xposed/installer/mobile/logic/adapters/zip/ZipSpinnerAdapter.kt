package de.robv.android.xposed.installer.mobile.logic.adapters.zip

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.models.ZipModel

class ZipSpinnerAdapter(private val ctx: Context, private val zip: ArrayList<ZipModel>)
    : ArrayAdapter<ZipModel>(ctx, R.layout.spinner_list_zip, R.id.spinner_item_zip_title, zip)
{
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, view: View?, parent: ViewGroup): View {

        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(R.layout.spinner_list_zip, parent, false)

        val textView = row.findViewById<TextView>(R.id.spinner_item_zip_title)
        textView.text = zip[position].key

        val imageView = row.findViewById<ImageView>(R.id.spinner_item_zip_status)
        imageView.setImageDrawable(zip[position].icon)

        return row
    }
}