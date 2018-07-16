package de.robv.android.xposed.installer.core.base.fragments

import android.app.Activity
import android.content.Context
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.util.NavUtil

class BaseSupport
{
    companion object {

        fun showSupportPage(context: Context){
            setNavUtil(context, context.getString(R.string.about_support))
        }

        fun showIssuesPage(context: Context){
            setNavUtil(context, context.getString(R.string.support_faq_url))
        }

        fun showDonationPage(context: Context){
            setNavUtil(context, context.getString(R.string.support_donate_url))
        }

        private fun setNavUtil(context: Context,string: String){
            NavUtil.startURL(context as Activity, string)
        }
    }
}