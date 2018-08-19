package de.robv.android.xposed.installer.mobile.ui.anko

import android.content.Context
import android.view.View
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.frameLayout

class ContainerViewUI<T>(context: Context) : AnkoComponent<T>
{
    private var mContext: Context? = null
    init {
        mContext = context
    }
    override fun createView(ui: AnkoContext<T>): View = with(ui) {

        frameLayout{
            id = Ids.baseViewFL
        }
    }
    object Ids {
        const val baseViewFL = 1
    }
}