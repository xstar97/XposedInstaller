package de.robv.android.xposed.installer.core.base.activities.utils;

import android.animation.ObjectAnimator;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class InstallationUtils
{
    public static ObjectAnimator FadeInResult(ImageView mConsoleResult, String propertyName, Float p1, Float p2){
        return ObjectAnimator.ofFloat(mConsoleResult, propertyName, p1, p2);
    }
    public static ObjectAnimator CountdownProgress(ProgressBar mProgress, String propertyName, Integer p1, Integer p2){
        return ObjectAnimator.ofInt(mProgress, propertyName, p1, p2);
    }
}