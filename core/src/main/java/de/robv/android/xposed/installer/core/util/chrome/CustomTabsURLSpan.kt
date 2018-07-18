package de.robv.android.xposed.installer.core.util.chrome

import android.app.Activity
import android.text.style.URLSpan
import android.view.View

import de.robv.android.xposed.installer.core.util.NavUtil

/**
 * Created by Nikola D. on 12/23/2015.
 */
class CustomTabsURLSpan(private val activity: Activity, url: String) : URLSpan(url) {

    override fun onClick(widget: View) {
        val url = url
        NavUtil.startURL(activity, url)
    }
}