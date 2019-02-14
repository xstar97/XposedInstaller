package de.robv.android.xposed.installer.core.logic.models

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

data class StatusModel(val statusMessage: String?, @ColorInt val statusColor: Int?, @ColorInt val statusContainerColor: Int?, @DrawableRes val statusIcon: Drawable?, val disableView: Int?)