package de.robv.android.xposed.installer.ui.fragments

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.preference.Preference
import android.util.Log
import android.widget.Toast
import de.robv.android.xposed.installer.R
import de.robv.android.xposed.installer.XposedApp
import de.robv.android.xposed.installer.core.util.RepoLoader
import de.robv.android.xposed.installer.ui.fragments.base.BasePreferenceFragment
import java.io.File
import java.io.IOException

class SettingsFragment: BasePreferenceFragment()
{
    companion object {
        private val mDisableResourcesFlag = File(XposedApp().BASE_DIR + "conf/disable_resources")
        val TAG: String = SettingsFragment::class.java.simpleName
        fun newInstance() = SettingsFragment()
    }

    private var mClickedPreference: Preference? = null
    private val downloadLocation: Preference? = null
    private val PREF_TYPE = "release_type_global"
    private val PREF_RES = "disable_resources"

    override fun onPreferenceClick(preference: Preference?): Boolean {
        if (preference!!.key == downloadLocation!!.key) {
            if (checkPermissions()) {
                mClickedPreference = downloadLocation
                return false
            }

            //TODO enable method to choose download location
            /*new FolderChooserDialog.Builder((SettingsActivity) getActivity())
                        .cancelButton(android.R.string.cancel)
                        .initialPath(XposedAppfu.getDownloadPath())
                        .show();*/
        }

        return true
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preference = findPreference(key)
        if (preference != null) {
            Log.d(XposedApp.TAG, "key: $key")
            val value = sharedPreferences!!.getString(preference.key, "")
            setPreferenceSummery(preference, value)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.prefs)
        val sharedPreferences = preferenceScreen.sharedPreferences
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        if (sharedPreferences != null) {
            //val preferenceScreen = preferenceScreen
            findPreference(PREF_TYPE).onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val type = newValue as String
                RepoLoader.getInstance().setReleaseTypeGlobal(type)
                true
            }

            val prefDisableResources = findPreference(PREF_RES) as android.support.v7.preference.CheckBoxPreference
            prefDisableResources.isChecked = mDisableResourcesFlag.exists()
            prefDisableResources.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val enabled = newValue as Boolean
                if (enabled) {
                    try {
                        mDisableResourcesFlag.createNewFile()
                    } catch (e: IOException) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    mDisableResourcesFlag.delete()
                }
                enabled == mDisableResourcesFlag.exists()
            }
        }

        // TODO maybe enable again after checking the implementation
        //downloadLocation = findPreference("download_location");
        //downloadLocation.setOnPreferenceClickListener(this);

    }
    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT < 23) return false

        if (ActivityCompat.checkSelfPermission(context!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mClickedPreference != null) {
                android.os.Handler().postDelayed({ onPreferenceClick(mClickedPreference) }, 500)
            }
        } else {
            Toast.makeText(activity, R.string.permissionNotGranted, Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}