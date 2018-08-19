package de.robv.android.xposed.installer.core.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.support.annotation.AnyThread
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify

import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton

object NavUtil {

    fun parseURL(str: String?): Uri? {
        if (str == null || str.isEmpty())
            return null

        val spannable = SpannableString(str)
        Linkify.addLinks(spannable, Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES)
        val spans = spannable.getSpans(0, spannable.length, URLSpan::class.java)
        return if (spans.size > 0) Uri.parse(spans[0].url) else null
    }

    fun startURL(activity: Activity, uri: Uri?) {
        if (!BaseXposedApp.getPreferences().getBoolean("chrome_tabs", true)) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, activity.packageName)
            activity.startActivity(intent)
            return
        }

        val customTabsIntent = CustomTabsIntent.Builder()
        customTabsIntent.setShowTitle(true)
        //customTabsIntent.setToolbarColor(activity.resources.getColor(R.color.colorPrimary))
        customTabsIntent.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        customTabsIntent.build().launchUrl(activity, uri)
    }

    fun startURL(activity: Activity, url: String) {
        startURL(activity, parseURL(url))
    }

    @AnyThread
    fun showMessage(context: Context, message: CharSequence) {
        BaseXposedApp.runOnUiThread {
            /*
                new MaterialDialog.Builder(context)
                        .content(message)
                        .positiveText(android.R.string.ok)
                        .show();*/
            context.alert(title = null, message = message) {
                okButton {}
            }.show()
        }
    }
}
