package de.robv.android.xposed.installer.core.base.fragments.download

import android.content.Context
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.repo.Module

class BaseDownloadDetails(mContext: Context, mModule: Module)
{
    private var module = mModule
    private var context = mContext

    fun getTitle(): String?{
        return module.name
    }
    fun getAuthor(): String?{
       return if(module.author != null && module.author.isNotEmpty())
         context.getString(R.string.download_author, module.author)
        else
        context.getString(R.string.download_unknown_author)
    }

    /**
     * first -> description
     * second -> hasHtml
     * third -> isVisible!?
     */
    fun getDescription(): Triple<String, Boolean, Boolean>?{
        return if (module.description != null) {
            val hasHtml = module.descriptionIsHtml
            Triple(module.description, hasHtml, true)
        } else {
            Triple("", false, false)
        }
    }
}