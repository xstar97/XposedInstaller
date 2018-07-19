package de.robv.android.xposed.installer.ui.fragments.base

import android.support.v4.app.Fragment
import de.robv.android.xposed.installer.logic.adapters.info.TabInfoModel
import de.robv.android.xposed.installer.logic.adapters.info.viewholders.TabInfoBaseViewHolder

open class BaseViewFragment: Fragment(), TabInfoBaseViewHolder.Delegate
{
    override fun onItemClick(infoItem: TabInfoModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}