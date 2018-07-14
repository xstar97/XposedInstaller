package de.robv.android.xposed.installer.ui.fragments.utils;

import android.content.Context;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.robv.android.xposed.installer.R;

public class LogsUtil
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

}
