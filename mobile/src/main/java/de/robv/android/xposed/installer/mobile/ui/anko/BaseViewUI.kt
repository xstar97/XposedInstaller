package de.robv.android.xposed.installer.mobile.ui.anko

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import de.robv.android.xposed.installer.core.logic.delegates.InfoDelegate
import de.robv.android.xposed.installer.core.logic.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.InfoBaseAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class BaseViewUI<T>(context: Context, delegate: InfoDelegate, mySection: Int, myList: ArrayList<InfoModel>) : AnkoComponent<T>
{
    private var mContext: Context? = context
    private var mDelegate: InfoDelegate? = delegate

    private val infoBaseAdapter by lazy { InfoBaseAdapter(mContext, mDelegate) }
    private var section: Int? = mySection
    private var list: ArrayList<InfoModel>? = myList

    override fun createView(ui: AnkoContext<T>): View = with(ui) {

        relativeLayout {
            val rv = recyclerView()
            rv.lparams(matchParent, wrapContent)
            rv.id = Ids.baseViewRV
            rv.adapter = infoBaseAdapter
            rv.layoutManager = LinearLayoutManager(context)
            if(infoBaseAdapter.itemCount == 0)
                infoBaseAdapter.addItems(section!!, list!!)
        }
    }
    object Ids {
        const val baseViewRV = 1
    }
}