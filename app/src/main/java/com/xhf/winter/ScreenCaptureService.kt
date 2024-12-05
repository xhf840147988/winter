package com.xhf.winter

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenCaptureService : Service() {
    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var screenDensity: Int = 0
    private var isCapturing = false
    private val scope = CoroutineScope(Dispatchers.IO + Job())


    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannel(this)
        startForeground(NOTIFICATION_ID, NotificationUtils.createNotification(this))
        initScreenInfo()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val resultCode = it.getIntExtra("resultCode", Activity.RESULT_CANCELED)
            val resultData = it.getParcelableExtra<Intent>("resultData")

            if (resultCode == Activity.RESULT_OK && resultData != null) {
                val mediaProjectionManager =
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData)
                startCapture()
            }
        }
        return START_NOT_STICKY
    }

    private fun initScreenInfo() {
        val metrics = resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
        screenDensity = metrics.densityDpi
    }

    private fun startCapture() {
        if (isCapturing) return
        isCapturing = true
        createVirtualDisplay()
        startCaptureLoop()
    }

    private fun startCaptureLoop() {
        scope.launch {
            while (isCapturing) {
                try {
                    if(FloatWindowManager.getSwitchState()){
                        captureScreen()
                        delay(1000) // 延迟1秒
                    }
                } catch (e: Exception) {
                    Log.e("ScreenCapture", "Error capturing screen", e)
                }
            }
        }
    }

    private fun captureScreen() {
        imageReader?.acquireLatestImage()?.use { image ->
            val fullBitmap = imageToBitmap(image)
//            saveScreenshot(fullBitmap,"fullBitmap")
            //互助
            val regionBitmap = Bitmap.createBitmap(
                fullBitmap,
                728,
                1965,
                126,
                126,
                null,
                true
            )
            val huzhu = BitmapFactory.decodeResource(resources, R.drawable.huzhu)
            val value = SIFTUtils.similarity(regionBitmap, huzhu)
            Log.e("xhf", "互助 对比值$value")
            if (value > 0.4) {
                Log.e("xhf", "点击")
                EventBus.getDefault().post(ClickEvent(803, 2116))
            }
//            saveScreenshot(regionBitmap,"regionBitmap")

            fullBitmap.recycle()
            regionBitmap.recycle()
        }
    }


    private fun createVirtualDisplay() {
        imageReader = ImageReader.newInstance(
            screenWidth, screenHeight,
            PixelFormat.RGBA_8888, 2
        )

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            screenWidth, screenHeight, screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )
    }


    private fun imageToBitmap(image: Image): Bitmap {
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * screenWidth
        val bitmap = Bitmap.createBitmap(
            screenWidth + rowPadding / pixelStride,
            screenHeight, Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }

    private fun saveScreenshot(bitmap: Bitmap, path: String) {
        try {
            // 获取应用私有目录
            val dir = File(applicationContext.filesDir, "screenshots")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            // 创建文件名（使用时间戳）
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "$path $timestamp.png"
            val file = File(dir, fileName)

            // 保存图片
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }

            Log.d("Screenshot", "Saved to: ${file.absolutePath}")

        } catch (e: Exception) {
            Log.e("Screenshot", "Error saving screenshot", e)
        }
    }
}