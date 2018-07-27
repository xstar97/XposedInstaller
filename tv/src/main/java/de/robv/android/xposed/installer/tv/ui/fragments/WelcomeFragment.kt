package de.robv.android.xposed.installer.tv.ui.fragments

import android.os.Bundle
import android.support.v17.leanback.widget.*
import android.util.Log
import de.robv.android.xposed.installer.core.models.NavModel
import de.robv.android.xposed.installer.tv.logic.findNavByPos
import de.robv.android.xposed.installer.tv.ui.activities.containers.ViewActivity

import de.robv.android.xposed.installer.tv.ui.fragments.base.BaseNavFragment
import org.jetbrains.anko.startActivity

class WelcomeFragment : BaseNavFragment(), OnItemViewSelectedListener, OnItemViewClickedListener
{
    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        try {
            val model = item as NavModel
            val pos = model.pos
            val title = model.title

            val nav = findNavByPos(pos)
            activity!!.startActivity<ViewActivity>(ViewActivity.INTENT_NAV_KEY to nav)
        }catch (e: Exception){
            Log.d(TAG, e.message)
        }
    }

    override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val TAG: String = WelcomeFragment::class.java.simpleName
        fun newInstance() = WelcomeFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUIElements()
        loadRows(activity!!)
        onItemViewClickedListener = this
        onItemViewSelectedListener = this
    }
}