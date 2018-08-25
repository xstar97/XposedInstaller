package de.robv.android.xposed.installer.core.logic.base.fragments.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import de.robv.android.xposed.installer.core.R;
import de.robv.android.xposed.installer.core.repo.Module;
import de.robv.android.xposed.installer.core.util.RepoLoader;

public class ModulesUtil
{
 public boolean onMenuEnabledModules(Context context, File targetDir, File listModules, File enabledModulesPath){
     try {
         if (!targetDir.exists())
             targetDir.mkdir();

         FileInputStream in = new FileInputStream(listModules);
         FileOutputStream out = new FileOutputStream(enabledModulesPath);

         byte[] buffer = new byte[1024];
         int len;
         while ((len = in.read(buffer)) > 0) {
             out.write(buffer, 0, len);
         }
         in.close();
         out.close();
         return true;
     } catch (IOException e) {
         Toast.makeText(context, context.getResources().getString(R.string.logs_save_failed) + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
         return false;
     }
 }

 public void onImportModulesUtil(Context context, InputStream ips, RepoLoader repoLoader, List list){
     try {
         assert ips != null;
         InputStreamReader ipsr = new InputStreamReader(ips);
         BufferedReader br = new BufferedReader(ipsr);
         String line;
         while ((line = br.readLine()) != null) {
             Module m = repoLoader.getModule(line);

             if (m == null) {
                 Toast.makeText(context, context.getResources().getString(R.string.download_details_not_found,
                         line), Toast.LENGTH_SHORT).show();
             } else {
                 list.add(m);
             }
         }
         br.close();
     } catch (ActivityNotFoundException | IOException e) {
         Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
     }
 }
}