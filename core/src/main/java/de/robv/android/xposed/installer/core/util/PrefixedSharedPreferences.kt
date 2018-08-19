package de.robv.android.xposed.installer.core.util

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.preference.PreferenceManager

import java.util.HashMap

class PrefixedSharedPreferences(private val mBase: SharedPreferences, prefix: String) : SharedPreferences {
    private val mPrefix: String = prefix + "_"

    override fun getAll(): Map<String, *> {
        val baseResult = mBase.all
        val prefixedResult = HashMap<String, Any>(baseResult)
        for ((key, value) in baseResult) {
            prefixedResult[mPrefix + key] = value!!
        }
        return prefixedResult
    }

    override fun getString(key: String, defValue: String?): String? {
        return mBase.getString(mPrefix + key, defValue)
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        return mBase.getStringSet(mPrefix + key, defValues)
    }

    override fun getInt(key: String, defValue: Int): Int {
        return mBase.getInt(mPrefix + key, defValue)
    }

    override fun getLong(key: String, defValue: Long): Long {
        return mBase.getLong(mPrefix + key, defValue)
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return mBase.getFloat(mPrefix + key, defValue)
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mBase.getBoolean(mPrefix + key, defValue)
    }

    override fun contains(key: String): Boolean {
        return mBase.contains(mPrefix + key)
    }

    @SuppressLint("CommitPrefEdits")
    override fun edit(): SharedPreferences.Editor {
        return EditorImpl(mBase.edit())
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        throw UnsupportedOperationException("listeners are not supported in this implementation")
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        throw UnsupportedOperationException("listeners are not supported in this implementation")
    }

    private inner class EditorImpl(private val mEditorBase: SharedPreferences.Editor) : SharedPreferences.Editor {

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            mEditorBase.putString(mPrefix + key, value)
            return this
        }

        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor {
            mEditorBase.putStringSet(mPrefix + key, values)
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            mEditorBase.putInt(mPrefix + key, value)
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            mEditorBase.putLong(mPrefix + key, value)
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            mEditorBase.putFloat(mPrefix + key, value)
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            mEditorBase.putBoolean(mPrefix + key, value)
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            mEditorBase.remove(mPrefix + key)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            mEditorBase.clear()
            return this
        }

        override fun commit(): Boolean {
            return mEditorBase.commit()
        }

        override fun apply() {
            mEditorBase.apply()
        }
    }

    companion object {

        fun injectToPreferenceManager(manager: PreferenceManager, prefix: String) {
            val prefixedPrefs = PrefixedSharedPreferences(manager.sharedPreferences, prefix)

            try {
                val fieldSharedPref = PreferenceManager::class.java.getDeclaredField("mSharedPreferences")
                fieldSharedPref.isAccessible = true
                fieldSharedPref.set(manager, prefixedPrefs)
            } catch (t: Throwable) {
                throw RuntimeException(t)
            }

        }
    }
}
