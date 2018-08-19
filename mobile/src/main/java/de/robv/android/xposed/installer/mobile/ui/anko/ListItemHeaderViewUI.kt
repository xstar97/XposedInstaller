package de.robv.android.xposed.installer.mobile.ui.anko

import android.content.Context
import android.view.View
import de.robv.android.xposed.installer.R
import kotlinx.android.synthetic.main.list_item_header.view.*
import org.jetbrains.anko.*

class ListItemHeaderViewUI<T>(context: Context) : AnkoComponent<T>
{
    private var mContext: Context? = context
    companion object {
        private const val mTop = 8
        private const val mBottom = mTop
        private const val mLeft = 16
        private const val mRight = mLeft
    }
    override fun createView(ui: AnkoContext<T>): View = with(ui) {

        linearLayout {
            //background = "?sticky_header_background"
            backgroundColor = R.style.stickyHeaderBackground
            topPadding = dip(mTop)
            bottomPadding = dip(mBottom)
            leftPadding = dip(mLeft)
            rightPadding = dip(mRight)

            textView {
                id = Ids.list_item_header
            }.lparams(width = matchParent, height = wrapContent) {
                margin = dip(4)
            }
        }
    }
    object Ids {
        const val list_item_header = 1
    }
}