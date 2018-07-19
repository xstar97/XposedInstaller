package de.robv.android.xposed.installer.logic

import android.support.v4.app.Fragment

open class Utils
{
    fun getFragment(tag: String): Fragment {
        return when(tag){
        //NavigationPosition.HOME.getTag() -> NavigationPosition.HOME.createFragment()
        //NavigationPosition.MODULES.getTag() -> NavigationPosition.MODULES.createFragment()
        //NavigationPosition.DOWNLOAD.getTag() -> NavigationPosition.DOWNLOAD.createFragment()
        //NavigationPosition.LOGS.getTag() -> NavigationPosition.LOGS.createFragment()
        //NavigationPosition.SUPPORT.getTag() -> NavigationPosition.SUPPORT.createFragment()
        //NavigationPosition.ABOUT.getTag() -> NavigationPosition.ABOUT.createFragment()
            NavigationPosition.SETTINGS.getTag() -> NavigationPosition.SETTINGS.createFragment()
            else -> NavigationPosition.ERROR.createFragment()
        }
    }
    fun getFragment(pos: Int): String {
        return when(pos){
        //NavigationPosition.HOME.getTag() -> NavigationPosition.HOME.createFragment()
        //NavigationPosition.MODULES.getTag() -> NavigationPosition.MODULES.createFragment()
        //NavigationPosition.DOWNLOAD.getTag() -> NavigationPosition.DOWNLOAD.createFragment()
        //NavigationPosition.LOGS.getTag() -> NavigationPosition.LOGS.createFragment()
        //NavigationPosition.SUPPORT.getTag() -> NavigationPosition.SUPPORT.createFragment()
        //NavigationPosition.ABOUT.getTag() -> NavigationPosition.ABOUT.createFragment()
            NavigationPosition.SETTINGS.getPos() -> NavigationPosition.SETTINGS.getTag()
            else -> NavigationPosition.ERROR.getTag()
        }
    }
}
