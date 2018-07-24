package de.robv.android.xposed.installer.mobile.ui.fragments.base

import android.support.v4.app.Fragment
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.mobile.logic.adapters.info.viewholders.TabInfoBaseViewHolder

open class BaseViewFragment: Fragment(), TabInfoBaseViewHolder.Delegate
{
    override fun onItemClick(infoItem: InfoModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}