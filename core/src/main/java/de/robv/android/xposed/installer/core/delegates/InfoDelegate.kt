package de.robv.android.xposed.installer.core.delegates

import de.robv.android.xposed.installer.core.models.InfoModel

interface InfoDelegate
{
    fun onItemClick(infoItem: InfoModel)
}