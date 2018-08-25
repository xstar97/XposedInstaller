package de.robv.android.xposed.installer.core.logic.models

import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes

data class StatusModel(val errorMes: String?, @ColorInt val errorColor: Int?, @ColorInt val statusContainerColor: Int?, @DrawableRes val statusIcon: Drawable?, val disableView: Int?)