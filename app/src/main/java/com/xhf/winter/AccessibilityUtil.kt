package com.xhf.winter

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.view.accessibility.AccessibilityManager
import android.app.Activity
import android.graphics.Path
import android.text.TextUtils

class AccessibilityUtil {
    companion object {
        /**
         * 检查无障碍服务是否开启
         */
        fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
            try {
                val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
                val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

                val serviceName = "${context.packageName}/${serviceClass.canonicalName}"

                for (service in enabledServices) {
                    val id = service.id
                    if (TextUtils.equals(id, serviceName)) {
                        return true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

        /**
         * 跳转到无障碍设置页面
         */
        fun goToAccessibilitySetting(activity: Activity, requestCode: Int = 100) {
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                activity.startActivityForResult(intent, requestCode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
