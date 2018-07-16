package de.robv.android.xposed.installer.core.base.fragments.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import de.robv.android.xposed.installer.core.R;
import de.robv.android.xposed.installer.core.base.BaseXposedApp;

public class LogsUtils
{
    public File saveUtil(Context context, File mFileErrorLog, File targetFile){
        try {
            FileInputStream in = new FileInputStream(mFileErrorLog);
            FileOutputStream out = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();

            Toast.makeText(context, targetFile.toString(),Toast.LENGTH_LONG).show();
            return targetFile;
        } catch (IOException e) {
            Toast.makeText(context, context.getResources().getString(R.string.logs_save_failed) + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public String getLogsUtil(File... log){
        StringBuilder llog = new StringBuilder(15 * 10 * 1024);
        try {
            File logfile = log[0];
            BufferedReader br;
            br = new BufferedReader(new FileReader(logfile));
            long skipped = skipLargeFile(br, logfile.length());
            if (skipped > 0) {
                llog.append("-----------------\n");
                llog.append("Log too long");
                llog.append("\n-----------------\n\n");
            }

            char[] temp = new char[1024];
            int read;
            while ((read = br.read(temp)) > 0) {
                llog.append(temp, 0, read);
            }
            br.close();
        } catch (IOException e) {
            llog.append("Cannot read log");
            llog.append(e.getMessage());
        }
        Log.d(BaseXposedApp.TAG, "log util: " + llog);
        return llog.toString();
    }

    private static final int MAX_LOG_SIZE = 1000 * 1024; // 1000 KB
    private long skipLargeFile(BufferedReader is, long length) throws IOException {
        if (length < MAX_LOG_SIZE)
            return 0;

        long skipped = length - MAX_LOG_SIZE;
        long yetToSkip = skipped;
        do {
            yetToSkip -= is.skip(yetToSkip);
        } while (yetToSkip > 0);

        int c;
        do {
            c = is.read();
            if (c == -1)
                break;
            skipped++;
        } while (c != '\n');

        return skipped;

    }
}