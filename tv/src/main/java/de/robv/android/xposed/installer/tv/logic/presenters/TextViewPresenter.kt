package de.robv.android.xposed.installer.tv.logic.presenters

import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.support.v17.leanback.widget.Presenter
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.core.logic.models.StatusModel
import de.robv.android.xposed.installer.core.logic.models.ZipModel
import de.robv.android.xposed.installer.core.repo.RepoDbDefinitions
import de.robv.android.xposed.installer.tv.logic.Navigation

class TextViewPresenter(private val context: Context?): Presenter(){

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val view = TextView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(BasePresenter.GRID_ITEM_WIDTH, BasePresenter.GRID_ITEM_HEIGHT)
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.setBackgroundColor(ContextCompat.getColor(context!!, R.color.default_background))
        view.setTextColor(Color.WHITE)
        view.gravity = Gravity.CENTER

        return Presenter.ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val textView = (viewHolder.view as TextView)
        val background = (viewHolder.view as View)
        when(item){
            is Navigation -> {
                textView.text = context!!.getString(item.title)
                //background.setBackgroundResource(item.icon)
            }
            is StatusModel ->{
                textView.text = item.statusMessage
                //textView.setTextColor(item.statusColor!!)
                background.setBackgroundColor(item.statusContainerColor!!)
            }
            is ZipModel -> {
                textView.text = item.key
                //background.background = item.icon
            }
            is Cursor -> {
                textView.text = item.getString(RepoDbDefinitions.OverviewColumnsIndexes.TITLE)
            }
        }

    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {

    }
}