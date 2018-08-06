package de.robv.android.xposed.installer.core.base.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.Log
import de.robv.android.xposed.installer.core.R
import de.robv.android.xposed.installer.core.base.BaseXposedApp
import de.robv.android.xposed.installer.core.models.InfoModel
import de.robv.android.xposed.installer.core.util.FrameworkZips
import java.io.File

open class BaseDevice
{
    companion object {
        const val deviceSdk = 0
        const val deviceManufacturer = 1
        const val deviceCpu = 2
        const val deviceVerified = 3

        private val androidVersion: String
            get() {
                return when (Build.VERSION.SDK_INT) {
                    15 -> "Ice Cream Sandwich"
                    16, 17, 18 -> "Jelly Bean"
                    19 -> "KitKat"
                    21, 22 -> "Lollipop"
                    23 -> "Marshmallow"
                    24, 25 -> "Nougat"
                    26, 27 -> "Oreo"
                    else -> "unknown"
                }
            }
        private val uiFramework: String
            get() {
                var manufacturer = Character.toUpperCase(Build.MANUFACTURER[0]) + Build.MANUFACTURER.substring(1)
                if (Build.BRAND != Build.MANUFACTURER) {
                    manufacturer += " " + Character.toUpperCase(Build.BRAND[0]) + Build.BRAND.substring(1)
                }
                manufacturer += " " + Build.MODEL + " "
                if (manufacturer.contains("Samsung")) {
                    manufacturer += if (File("/system/framework/twframework.jar").exists()) "(TouchWiz)" else "(AOSP-based ROM)"
                } else if (manufacturer.contains("Xioami")) {
                    manufacturer += if (File("/system/framework/framework-miui-res.apk").exists()) "(MIUI)" else "(AOSP-based ROM)"
                }
                return manufacturer
            }

        @SuppressLint("PrivateApi")
        private fun determineVerifiedBootState(context: Context): Pair<String, String> {
            return try {
                val c = Class.forName("android.os.SystemProperties")
                val m = c.getDeclaredMethod("get", String::class.java, String::class.java)
                m.isAccessible = true

                val propSystemVerified = m.invoke(null, "partition.system.verified", "0") as String
                val propState = m.invoke(null, "ro.boot.verifiedbootstate", "") as String
                val fileDmVerityModule = File("/sys/module/dm_verity")

                val verified = propSystemVerified != "0"
                val detected = !propState.isEmpty() || fileDmVerityModule.exists()

                return when {
                    verified -> {
                        Pair(context.getString(R.string.verified_boot_active), context.getString(R.string.verified_boot_explanation))
                    }
                    detected -> {
                        Pair(context.getString(R.string.verified_boot_deactivated), "")
                    }
                    else -> Pair("", "")
                }
            } catch (e: Exception) {
                Log.e(BaseXposedApp.TAG, "Could not detect Verified Boot state", e)
                Pair("", "")
            }
        }
    }

    open fun getDeviceInfoList(context: Context): ArrayList<InfoModel>{
        val androidSdk = context.getString(R.string.android_sdk, Build.VERSION.RELEASE, androidVersion, Build.VERSION.SDK_INT)
        val manufacturer = uiFramework
        val cpu = FrameworkZips.ARCH

        val initBootState = determineVerifiedBootState(context)
        val verifiedBootTitle = initBootState.first
        val verifiedBootExplanation = initBootState.second

        val list = ArrayList<InfoModel>()
        list.add(InfoModel(deviceSdk, ContextCompat.getDrawable(context,R.drawable.ic_android)!!, androidSdk, ""))
        list.add(InfoModel(deviceManufacturer, ContextCompat.getDrawable(context,R.drawable.ic_phone)!!, manufacturer, ""))
        list.add(InfoModel(deviceCpu, ContextCompat.getDrawable(context,R.drawable.ic_chip)!!, cpu, ""))
        if (verifiedBootTitle.isNotEmpty() || verifiedBootExplanation.isNotEmpty()) {
            list.add(InfoModel(deviceVerified, ContextCompat.getDrawable(context,R.drawable.ic_verified)!!, verifiedBootTitle, verifiedBootExplanation))
        }
        return list
    }
}