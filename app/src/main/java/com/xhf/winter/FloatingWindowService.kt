package com.xhf.winter

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

class FloatingWindowService : Service() {
    private var windowManager: WindowManager? = null
    private var floatView: View? = null
    private var params: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("xhf", "Service onCreate")
        // 初始化WindowManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        // 加载悬浮窗布局
        createFloatView()
        
        // 设置WindowManager.LayoutParams参数
        params = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.START or Gravity.TOP
            x = 0
            y = 0
        }

        // 设置悬浮窗可拖动
        setupTouchListener()

        // 将悬浮窗添加到WindowManager
        // 添加try-catch来捕获可能的异常
        try {
            windowManager?.addView(floatView, params)
            Log.d("xhf", "View added successfully")
        } catch (e: Exception) {
            Log.e("xhf", "Failed to add view", e)
        }
    }

    private fun createFloatView() {
        // 创建主布局
        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(32, 16, 32, 16) // 设置内边距
            setBackgroundColor(Color.parseColor("#80000000")) // 半透明黑色背景
        }

        // 创建文本视图
        val textView = TextView(this).apply {
            text = "联盟互助"
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = 32 // 设置右边距
            }
        }

        // 创建开关
        val switch = Switch(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnCheckedChangeListener { _, isChecked ->
                FloatWindowManager.setSwitchState(isChecked)
            }
        }

        // 将视图添加到布局中
        linearLayout.addView(textView)
        linearLayout.addView(switch)

        floatView = linearLayout
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        var initialX: Int = 0
        var initialY: Int = 0
        var initialTouchX: Float = 0f
        var initialTouchY: Float = 0f

        floatView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params?.x ?: 0
                    initialY = params?.y ?: 0
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    params?.x = initialX + (event.rawX - initialTouchX).toInt()
                    params?.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(floatView, params)
                }
            }
            false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        floatView?.let { windowManager?.removeView(it) }
    }
}