package de.robv.android.xposed.installer.tv;

import android.os.Handler;
import android.preference.PreferenceManager;

import de.robv.android.xposed.installer.core.logic.base.BaseXposedApp;
import de.robv.android.xposed.installer.core.util.AssetUtil;
import de.robv.android.xposed.installer.core.util.NotificationUtil;

public class XposedApp extends BaseXposedApp
{
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mUiThread = Thread.currentThread();
        BaseXposedApp.mMainHandler = new Handler();

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        reloadXposedProp();
        createDirectories();
        NotificationUtil.init();
        AssetUtil.removeBusybox();
    }
}