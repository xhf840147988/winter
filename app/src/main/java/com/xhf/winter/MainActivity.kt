package com.xhf.winter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xhf.winter.ui.theme.WinterTheme
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity() {
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private val startMediaProjection = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra("resultCode", result.resultCode)
                putExtra("resultData", result.data)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WinterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
        checkAccessibilityPermission()

    }

    private fun requestScreenCapture() {
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startMediaProjection.launch(mediaProjectionManager.createScreenCaptureIntent())
    }


    private fun checkAccessibilityPermission() {
        if (!AccessibilityUtil.isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
            // 显示对话框提示用户开启无障碍服务
            AlertDialog.Builder(this)
                .setTitle("需要无障碍权限")
                .setMessage("请开启无障碍服务以启用自动点击功能")
                .setPositiveButton("去开启") { _, _ ->
                    AccessibilityUtil.goToAccessibilitySetting(this)
                }
                .setNegativeButton("取消", null)
                .show()
        }

    }

    private fun checkFloatingWindowService(){
        Log.d("xhf", "Can draw overlays: ${Settings.canDrawOverlays(this)}")
        // 检查是否有悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            // 请求悬浮窗权限
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 0)
        } else {
            // 启动悬浮窗服务
            startFloatingWindowService()
        }
    }

    private fun startFloatingWindowService() {
        startService(Intent(this, FloatingWindowService::class.java))
//        if(OpenCVLoader.initDebug()){
//            requestScreenCapture()
//        }
    }
    // 处理设置页面返回结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            // 检查是否已开启
            if (AccessibilityUtil.isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                Toast.makeText(this, "无障碍服务已开启", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "无障碍服务未开启", Toast.LENGTH_SHORT).show()
            }
            checkFloatingWindowService()
        }
        if (requestCode == 0) {
            if (Settings.canDrawOverlays(this)) {
                startFloatingWindowService()
            } else {
                Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WinterTheme {
        Greeting("Android")
    }
}