package de.robv.android.xposed.installer.mobile.ui.base

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

open class BasePreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
       TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    open fun setPreferenceSummery(preference: Preference, value: Any) {

        val stringValue = value.toString()

        if (preference is ListPreference) {
            val prefIndex = preference.findIndexOfValue(stringValue)

            if (prefIndex >= 0) {
                preference.summary = preference.entries[prefIndex]
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.summary = stringValue
        }
    }
}