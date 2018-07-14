package de.robv.android.xposed.installer.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.LinearLayout
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoModel
import de.robv.android.xposed.installer.logic.adapters.info.viewholders.TabInfoBaseViewHolder
import org.jetbrains.anko.*

open class BaseViewFragment: Fragment(), TabInfoBaseViewHolder.Delegate
{
    override fun onItemClick(infoItem: TabInfoModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}