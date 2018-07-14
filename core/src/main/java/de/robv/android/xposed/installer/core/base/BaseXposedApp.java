package de.robv.android.xposed.installer.core.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import de.robv.android.xposed.installer.core.util.DownloadsUtil;
import de.robv.android.xposed.installer.core.util.InstallZipUtil;
import de.robv.android.xposed.installer.core.util.RepoLoader;

public class BaseXposedApp extends Application implements Application.ActivityLifecycleCallbacks
{
    public static final String TAG = "XposedInstaller";

    public static final String BASE_PKG = "de.robv.android.xposed.installer";

    public static final String PREF_DL_DIR = "download_location";

    public static BaseXposedApp getInstance() {
        return mInstance;
    }

    public static void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mMainHandler.post(action);
        } else {
            action.run();
        }
    }

    public static void postOnUiThread(Runnable action) {
        mMainHandler.post(action);
    }

    // This method is hooked by XposedBridge to return the current version
    public static int getActiveXposedVersion() {
        return -1;
    }

    public static int getInstalledXposedVersion() {
        InstallZipUtil.XposedProp prop = getXposedProp();
        return prop != null ? prop.getVersionInt() : -1;
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public static InstallZipUtil.XposedProp getXposedProp() {
        synchronized (mInstance) {
            return mInstance.mXposedProp;
        }
    }

    public static SharedPreferences getPreferences() {
        return mInstance.mPref;
    }

    public static void installApk(Context context, DownloadsUtil.DownloadInfo info) {
        Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, BASE_PKG + ".fileprovider", new File(info.localFilename));
            installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(info.localFilename));
        }
        installIntent.setDataAndType(uri, DownloadsUtil.MIME_TYPE_APK);
        installIntent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, context.getApplicationInfo().packageName);
        context.startActivity(installIntent);
    }

    //TODO enable additional download paths!
    public static String getDownloadPath() {
        return getPreferences().getString(PREF_DL_DIR, Environment.getExternalStorageDirectory() + "/XposedInstaller");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("SdCardPath")
    private final String BASE_DIR_LEGACY = "/data/data/" + BASE_PKG + "/";

    public final String BASE_DIR = Build.VERSION.SDK_INT >= 24
            ? "/data/user_de/0/" + BASE_PKG + "/" : BASE_DIR_LEGACY;

    public final String ENABLED_MODULES_LIST_FILE = BASE_DIR + "conf/enabled_modules.list";

    public static final String[] XPOSED_PROP_FILES = new String[]{
            "/su/xposed/xposed.prop", // official systemless
            "/system/xposed.prop",    // classical
    };

    public static int WRITE_EXTERNAL_PERMISSION = 69;
    public static BaseXposedApp mInstance = null;
    public static Thread mUiThread;
    public static Handler mMainHandler;
    public boolean mIsUiLoaded = false;
    public SharedPreferences mPref;

    public InstallZipUtil.XposedProp mXposedProp;

    //TODO add fields to the numbers to figure out what they are for!
    @SuppressWarnings({"OctalInteger", "JavaReflectionMemberAccess", "PrivateApi"})
    @SuppressLint("PrivateApi")
    public void createDirectories() {
        FileUtils.setPermissions(BASE_DIR, 00711, -1, -1);
        mkdirAndChmod("conf", 00771);
        mkdirAndChmod("log", 00777);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                 Method deleteDir = FileUtils.class.getDeclaredMethod("deleteContentsAndDir", File.class);
                deleteDir.invoke(null, new File(BASE_DIR_LEGACY, "bin"));
                deleteDir.invoke(null, new File(BASE_DIR_LEGACY, "conf"));
                deleteDir.invoke(null, new File(BASE_DIR_LEGACY, "log"));
            } catch (ReflectiveOperationException e) {
                Log.w(TAG, "Failed to delete obsolete directories", e);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void mkdirAndChmod(String dir, int permissions) {
        dir = BASE_DIR + dir;
        new File(dir).mkdir();
        FileUtils.setPermissions(dir, permissions, -1, -1);
    }

    public void reloadXposedProp() {
        InstallZipUtil.XposedProp prop = null;

        for (String path : XPOSED_PROP_FILES) {
            File file = new File(path);
            if (file.canRead()) {
                FileInputStream is = null;
                try {
                    is = new FileInputStream(file);
                    prop = InstallZipUtil.parseXposedProp(is);
                    break;
                } catch (IOException e) {
                    Log.e(TAG, "Could not read " + file.getPath(), e);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }

        synchronized (this) {
            mXposedProp = prop;
        }
    }

    // TODO find a better way to trigger actions only when any UI is shown for the first time
    @Override
    public synchronized void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (mIsUiLoaded)
            return;
        RepoLoader.getInstance().triggerFirstLoadIfNecessary();
        mIsUiLoaded = true;
    }
    @Override
    public synchronized void onActivityStarted(Activity activity) {

    }

    @Override
    public synchronized void onActivityResumed(Activity activity) {

    }

    @Override
    public synchronized void onActivityPaused(Activity activity) {

    }

    @Override
    public synchronized void onActivityStopped(Activity activity) {

    }

    @Override
    public synchronized void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public synchronized void onActivityDestroyed(Activity activity) {

    }
}