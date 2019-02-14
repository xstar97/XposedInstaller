package de.robv.android.xposed.installer.core.logic.models

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import de.robv.android.xposed.installer.core.util.FrameworkZips

data class ZipModel(val key: String?, @DrawableRes val icon: Drawable?, val type: FrameworkZips.Type)