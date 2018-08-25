package de.robv.android.xposed.installer.core.installation;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.robv.android.xposed.installer.core.logic.base.BaseXposedApp;
import de.robv.android.xposed.installer.core.util.AssetUtil;
import de.robv.android.xposed.installer.core.util.FrameworkZips;
import de.robv.android.xposed.installer.core.util.InstallZipUtil.ZipCheckResult;
import de.robv.android.xposed.installer.core.util.RootUtil;

import static de.robv.android.xposed.installer.core.util.InstallZipUtil.closeSilently;
import static de.robv.android.xposed.installer.core.util.InstallZipUtil.triggerError;
import static de.robv.android.xposed.installer.core.util.RootUtil.getShellPath;

public class FlashDirectly extends Flashable {
    private final boolean mSystemless;

    public FlashDirectly(File zipPath, FrameworkZips.Type type, String title, boolean systemless) {
        super(zipPath, type, title);
        mSystemless = systemless;
    }

    @SuppressWarnings("OctalInteger")
    public void flash(Context context, FlashCallback callback) {
        ZipCheckResult zipCheck = openAndCheckZip(callback);
        if (zipCheck == null) {
            return;
        }

        // Do additional checks.
        ZipFile zip = zipCheck.getZip();
        if (!zipCheck.isFlashableInApp()) {
            triggerError(callback, FlashCallback.ERROR_NOT_FLASHABLE_IN_APP);
            closeSilently(zip);
            return;
        }

        // Extract update-binary.
        ZipEntry entry = zip.getEntry("META-INF/com/google/android/update-binary");
        File updateBinaryFile = new File(BaseXposedApp.getInstance().getCacheDir(), "update-binary");
        try {
            AssetUtil.writeStreamToFile(zip.getInputStream(entry), updateBinaryFile, 0700);
        } catch (IOException e) {
            Log.e(BaseXposedApp.TAG, "Could not extract update-binary", e);
            triggerError(callback, FlashCallback.ERROR_INVALID_ZIP);
            return;
        } finally {
            closeSilently(zip);
        }

        // Execute the flash commands.
        RootUtil rootUtil = new RootUtil();
        if (!rootUtil.startShell(callback)) {
            return;
        }

        callback.onStarted();

        rootUtil.execute("export NO_UIPRINT=1", callback);
        if (mSystemless) {
            rootUtil.execute("export SYSTEMLESS=1", callback);
        }

        int result = rootUtil.execute(getShellPath(updateBinaryFile) + " 2 1 " + getShellPath(mZipPath), callback);
        if (result != FlashCallback.OK) {
            triggerError(callback, result);
            return;
        }

        callback.onDone();
    }

    public static final Creator<FlashDirectly> CREATOR
            = new Creator<FlashDirectly>() {
        @Override
        public FlashDirectly createFromParcel(Parcel in) {
            return new FlashDirectly(in);
        }

        @Override
        public FlashDirectly[] newArray(int size) {
            return new FlashDirectly[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mSystemless ? 1 : 0);
    }

    @SuppressWarnings("WeakerAccess")
    protected FlashDirectly(Parcel in) {
        super(in);
        mSystemless = in.readInt() == 1;
    }
}
