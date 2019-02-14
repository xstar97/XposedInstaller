package de.robv.android.xposed.installer.core.logic.models

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

data class InfoModel(val pos: Int, @DrawableRes val icon: Drawable?, val key: String, val desciption: String)