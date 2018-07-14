package de.robv.android.xposed.installer.logic;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;

import de.robv.android.xposed.installer.core.R;
import de.robv.android.xposed.installer.core.base.BaseXposedApp;
import de.robv.android.xposed.installer.ui.activities.XposedBaseActivity;

@SuppressWarnings("WeakerAccess")
public final class ThemeUtil
{
	private static int[] THEMES = new int[] {
			R.style.Theme_XposedInstaller_Light,
			R.style.Theme_XposedInstaller_Dark,
			R.style.Theme_XposedInstaller_Dark_Black, };

	private ThemeUtil() {
	}

	public static int getSelectTheme() {
        String myTheme = BaseXposedApp.getPreferences().getString("theme", "0");
        int theme = Integer.parseInt(myTheme);
		return (theme >= 0 && theme < THEMES.length) ? theme : 0;
	}

	public static void setTheme(XposedBaseActivity activity) {
		activity.mTheme = getSelectTheme();
		activity.setTheme(THEMES[activity.mTheme]);
	}

	public static void reloadTheme(XposedBaseActivity activity) {
		int theme = getSelectTheme();
		if (theme != activity.mTheme)
			activity.recreate();
    }

    public static int getThemeColor(Context context, int id) {
        Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[] { id });
		int result = a.getColor(0, 0);
		a.recycle();
		return result;
	}
}
