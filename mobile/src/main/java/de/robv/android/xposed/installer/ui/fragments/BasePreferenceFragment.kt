package de.robv.android.xposed.installer.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat

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
            val listPreference = preference as ListPreference
            val prefIndex = listPreference.findIndexOfValue(stringValue)

            if (prefIndex >= 0) {
                listPreference.summary = listPreference.entries[prefIndex]
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.summary = stringValue
        }
    }
}