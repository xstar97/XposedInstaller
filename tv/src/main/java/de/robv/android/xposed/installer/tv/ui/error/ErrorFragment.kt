/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.robv.android.xposed.installer.tv.ui.error

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.ErrorSupportFragment
import androidx.core.content.ContextCompat
import android.view.View
import de.robv.android.xposed.installer.R

/**
 * This class demonstrates how to extend [android.support.v17.leanback.app.ErrorFragment].
 */
class ErrorFragment : ErrorSupportFragment(),
        BrowseSupportFragment.MainFragmentAdapterProvider {

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val TRANSLUCENT = true
        val TAG: String = ErrorFragment::class.java.simpleName
        fun newInstance() = ErrorFragment()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = resources.getString(R.string.app_name)
        setErrorContent()
    }

    private fun setErrorContent() {
        imageDrawable = ContextCompat.getDrawable(activity!!, R.drawable.lb_ic_sad_cloud)
        message = resources.getString(R.string.error_fragment_message)
        setDefaultBackground(TRANSLUCENT)

        buttonText = resources.getString(R.string.dismiss_error)
        buttonClickListener = View.OnClickListener {
            activity!!.finish()
        }
    }
}