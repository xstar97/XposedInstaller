package de.robv.android.xposed.installer.core.models

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import de.robv.android.xposed.installer.core.util.FrameworkZips

data class ZipModel(val key: String?, @DrawableRes val icon: Drawable?, val type: FrameworkZips.Type)