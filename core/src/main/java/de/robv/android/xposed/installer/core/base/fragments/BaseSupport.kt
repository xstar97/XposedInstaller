package de.robv.android.xposed.installer.core.base.fragments

import android.app.Activity
import android.content.Context
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.core.util.NavUtil

class BaseSupport
{
    companion object {

        const val supportModulesLabel = 0
        const val supportFrameworkLabel = 1
        const val supportFaqLabel = 2
        const val supportDonateLabel = 3

        fun getSupportList(context: Context): ArrayList<InfoModel>{
            val list = ArrayList<InfoModel>()
            list.add(InfoModel(supportModulesLabel, R.drawable.ic_info, context.getString(R.string.support_modules_label), context.getString(R.string.support_modules_description, context.getString(R.string.module_support))))
            list.add(InfoModel(supportFrameworkLabel, R.drawable.ic_help, context.getString(R.string.support_framework_label), ""))
            list.add(InfoModel(supportFaqLabel, R.drawable.ic_nav_logs, context.getString(R.string.support_faq_label), ""))
            list.add(InfoModel(supportDonateLabel, R.drawable.ic_donate, context.getString(R.string.support_donate_label), context.getString(R.string.support_donate_description)))
            return list
         }
        //actions
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