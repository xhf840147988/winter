package com.xhf.winter
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Binder
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MyAccessibilityService : AccessibilityService() {
    private var clickJob: Job? = null
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MyAccessibilityService = this@MyAccessibilityService
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 可以根据需要处理辅助功能事件
        Log.e("xhf", "onAccessibilityEvent: $event")
        var packageName = event.packageName
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e("xhf", "服务已连接:")
        startClickLoop()
    }

    private fun startClickLoop() {
        clickJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (FloatWindowManager.getSwitchState()) {
                    // 执行点击
                    Log.e("xhf", "执行点击:")
                    //首页互助的小图标
                    performClick(803, 2116)
                    //联盟里的互助
//                    performClick(532, 2229)
                }
                delay(2000) // 延迟2秒
            }
        }
    }

    override fun onInterrupt() {
    }

    override fun onDestroy() {
        super.onDestroy()
        clickJob?.cancel()
    }
    fun performClick(x: Int, y: Int) {
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())

        val builder = GestureDescription.Builder()
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        dispatchGesture(gestureDescription, null, null)
    }


}