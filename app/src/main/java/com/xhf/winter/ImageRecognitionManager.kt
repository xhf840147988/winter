//package com.xhf.winter
//
//class ImageRecognitionManager {
//    private val textRecognizer = TextRecognition.getClient()
//
//    fun recognizeText(bitmap: Bitmap, callback: (String) -> Unit) {
//        val image = InputImage.fromBitmap(bitmap, 0)
//        textRecognizer.process(image)
//            .addOnSuccessListener { visionText ->
//                callback(visionText.text)
//            }
//            .addOnFailureListener { e ->
//                Log.e("ImageRecognition", "Text recognition failed", e)
//            }
//    }
//
//    fun findTextInRegion(bitmap: Bitmap, region: Rect, targetText: String): Boolean {
//        // 裁剪指定区域
//        val croppedBitmap = Bitmap.createBitmap(
//            bitmap,
//            region.left, region.top,
//            region.width(), region.height()
//        )
//
//        var found = false
//        val latch = CountDownLatch(1)
//
//        recognizeText(croppedBitmap) { text ->
//            found = text.contains(targetText, ignoreCase = true)
//            latch.countDown()
//        }
//
//        latch.await(5, TimeUnit.SECONDS)
//        return found
//    }
//
//    // 颜色匹配
//    fun findColorInRegion(bitmap: Bitmap, region: Rect, targetColor: Int, tolerance: Int = 25): Boolean {
//        val croppedBitmap = Bitmap.createBitmap(
//            bitmap,
//            region.left, region.top,
//            region.width(), region.height()
//        )
//
//        for (x in 0 until croppedBitmap.width) {
//            for (y in 0 until croppedBitmap.height) {
//                val pixel = croppedBitmap.getPixel(x, y)
//                if (colorsMatch(pixel, targetColor, tolerance)) {
//                    return true
//                }
//            }
//        }
//        return false
//    }
//
//    private fun colorsMatch(color1: Int, color2: Int, tolerance: Int): Boolean {
//        val r1 = Color.red(color1)
//        val g1 = Color.green(color1)
//        val b1 = Color.blue(color1)
//        val r2 = Color.red(color2)
//        val g2 = Color.green(color2)
//        val b2 = Color.blue(color2)
//
//        return Math.abs(r1 - r2) <= tolerance &&
//               Math.abs(g1 - g2) <= tolerance &&
//               Math.abs(b1 - b2) <= tolerance
//    }
//}