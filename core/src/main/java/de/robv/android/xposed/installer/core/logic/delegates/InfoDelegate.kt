package de.robv.android.xposed.installer.core.logic.delegates

import de.robv.android.xposed.installer.core.logic.models.InfoModel

interface InfoDelegate
{
    fun onItemClick(infoItem: InfoModel)
}