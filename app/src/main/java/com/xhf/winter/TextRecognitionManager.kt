//package com.xhf.winter
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.util.Log
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.text.TextRecognition
//import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
//import org.opencv.android.Utils
//import org.opencv.core.Core
//import org.opencv.core.Mat
//import org.opencv.imgproc.Imgproc
//
//class TextRecognitionManager(private val context: Context) {
//    private val textRecognizer =
//        TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
//
//    fun processImageForText(bitmap: Bitmap, callback: (String) -> Unit) {
//        // 1. OpenCV预处理
//        val processedBitmap = preprocessImage(bitmap)
//
//        // 2. ML Kit文字识别
//        val image = InputImage.fromBitmap(processedBitmap, 0)
//        textRecognizer.process(image)
//            .addOnSuccessListener { visionText ->
//                callback(visionText.text)
//            }
//            .addOnFailureListener { e ->
//                Log.e("xhf TextRecognition", "Error recognizing text", e)
//            }
//    }
//
//    private fun preprocessImage(bitmap: Bitmap): Bitmap {
//        // 转换为Mat
//        val mat = Mat()
//        Utils.bitmapToMat(bitmap, mat)
//
//        // 1. 转换为灰度图
//        val grayMat = Mat()
//        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY)
//
//        // 2. 二值化
//        val binaryMat = Mat()
//        Imgproc.threshold(
//            grayMat, binaryMat,
//            0.0, 255.0,
//            Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU
//        )
//
//        // 3. 降噪
//        val denoisedMat = Mat()
//        Imgproc.medianBlur(binaryMat, denoisedMat, 3)
//
//        // 4. 增强对比度
//        val enhancedMat = Mat()
//        Core.normalize(denoisedMat, enhancedMat, 0.0, 255.0, Core.NORM_MINMAX)
//
//        // 转回Bitmap
//        val resultBitmap = Bitmap.createBitmap(
//            enhancedMat.cols(), enhancedMat.rows(),
//            Bitmap.Config.ARGB_8888
//        )
//        Utils.matToBitmap(enhancedMat, resultBitmap)
//
//        // 清理Mat
//        mat.release()
//        grayMat.release()
//        binaryMat.release()
//        denoisedMat.release()
//        enhancedMat.release()
//
//        return resultBitmap
//    }
//}