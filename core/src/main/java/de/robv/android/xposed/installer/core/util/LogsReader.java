package de.robv.android.xposed.installer.core.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import de.robv.android.xposed.installer.core.R;

public class LogsReader extends AsyncTask<File, Integer, String> {

    public interface onAsyncComplete
    {
        void getData(String data);
    }
    private onAsyncComplete delegate = null;


    @SuppressLint("StaticFieldLeak")
    private Context context = null;

    private static final int MAX_LOG_SIZE = 1000 * 1024; // 1000 KB
    private MaterialDialog mProgressDialog;

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

    public LogsReader(Context context, onAsyncComplete delegate){
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new MaterialDialog.Builder(context).content(R.string.loading).progress(true, 0).show();
    }

    @Override
    protected String doInBackground(File... log) {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 2);

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

        return llog.toString();
    }

    @Override
    protected void onPostExecute(String llog) {
        mProgressDialog.dismiss();
        if (llog.length() != 0)
            delegate.getData(llog);
        else
            delegate.getData(context.getResources().getString(R.string.log_is_empty));
    }
}