package de.robv.android.xposed.installer.tv.logic.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.models.ActionModel
import kotlinx.android.synthetic.main.list_item_action.view.*

class ActionAdapter internal constructor(private val context: Context) : RecyclerView.Adapter<ActionAdapter.ViewHolder>() {

    private val TAG: String = ActionAdapter::class.java.simpleName

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val list = ArrayList<ActionModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.list_item_action, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (icon, title) = list[position]

        holder.mIcon.setImageDrawable(ContextCompat.getDrawable(context, icon))
        holder.mTitle.text = title
    }

    override fun getItemCount(): Int {
        return list.size
    }

    open inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        open var mIcon: ImageView = itemView.actionIcon
        open var mTitle: TextView = itemView.actionTitle
    }

    open fun addActions(actions: ArrayList<ActionModel>?) {
        list.addAll(actions!!)
        notifyDataSetChanged()
    }
    open fun addAction(action: ActionModel?, position: Int) {
        list.add(action!!)
        notifyItemInserted(position)
    }

    open fun removeActions(){
        list.clear()
        notifyDataSetChanged()
    }
    open fun removeAction(action: ActionModel?, position: Int){
        list.remove(action!!)
        notifyItemRemoved(position)
    }

    open fun updateAction(action: ActionModel?, position: Int){
        list[position] = action!!
        notifyItemChanged(position)
    }
}