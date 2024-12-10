package com.xhf.winter

import android.graphics.Bitmap
//import com.google.mlkit.vision.common.InputImage
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.SIFT
import org.opencv.imgproc.Imgproc

object SIFTUtils {
    fun similarity(bitmap1: Bitmap, bitmap2: Bitmap): Double {
        try {
            // 转换为Mat
            val mat1 = Mat()
            val mat2 = Mat()
            Utils.bitmapToMat(bitmap1, mat1)
            Utils.bitmapToMat(bitmap2, mat2)

            // 转换为灰度图
            val gray1 = Mat()
            val gray2 = Mat()
            Imgproc.cvtColor(mat1, gray1, Imgproc.COLOR_RGBA2GRAY)
            Imgproc.cvtColor(mat2, gray2, Imgproc.COLOR_RGBA2GRAY)

            // 创建SIFT检测器
            val sift = SIFT.create()

            // 检测关键点和描述符
            val keyPoints1 = MatOfKeyPoint()
            val keyPoints2 = MatOfKeyPoint()
            val descriptors1 = Mat()
            val descriptors2 = Mat()

            sift.detectAndCompute(gray1, Mat(), keyPoints1, descriptors1)
            sift.detectAndCompute(gray2, Mat(), keyPoints2, descriptors2)

            // 检查是否成功检测到特征点
            if (descriptors1.empty() || descriptors2.empty()) {
                return 0.0
            }

            // 特征匹配
            val matches = ArrayList<MatOfDMatch>()
            val matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED)
            
            try {
                matcher.knnMatch(descriptors1, descriptors2, matches, 2)
            } catch (e: Exception) {
                // 如果FLANN匹配失败，尝试使用BRUTEFORCE匹配
                val bfMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE)
                bfMatcher.knnMatch(descriptors1, descriptors2, matches, 2)
            }

            // 应用比率测试
            var goodMatches = 0
            for (match in matches) {
                if (match.toArray().size >= 2) {
                    val m = match.toArray()[0]
                    val n = match.toArray()[1]
                    if (m.distance < 0.75 * n.distance) {
                        goodMatches++
                    }
                }
            }

            // 计算相似度
            val similarity = goodMatches.toDouble() /
                    keyPoints1.toArray().size.coerceAtMost(keyPoints2.toArray().size)

            // 释放资源
            mat1.release()
            mat2.release()
            gray1.release()
            gray2.release()
            descriptors1.release()
            descriptors2.release()
            keyPoints1.release()
            keyPoints2.release()

            return similarity

        } catch (e: Exception) {
            e.printStackTrace()
            return 0.0
        }
    }
}