package com.fang.arrangement.definition.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import com.fang.arrangement.Arrangement
import com.fang.arrangement.definition.AttendanceImage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

internal class AttendanceImageRepository(
    private val context: Context,
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
) {
    suspend fun upload(
        attendanceMillis: Long,
        siteId: Long,
        uri: Uri,
    ): AttendanceImage {
        val path = "attendance/${Arrangement.current.id}/$attendanceMillis/$siteId/${UUID.randomUUID()}.jpg"
        val reference = storage.reference.child(path)
        val bytes = withContext(Dispatchers.Default) { compress(uri) }
        val metadata = StorageMetadata.Builder().setContentType("image/jpeg").build()
        reference.putBytes(bytes, metadata).await()
        return AttendanceImage(path = reference.path, downloadUrl = reference.downloadUrl.await().toString())
    }

    suspend fun delete(path: String) {
        storage.reference
            .child(path)
            .delete()
            .await()
    }

    private fun compress(uri: Uri): ByteArray {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri).use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw IOException("無法讀取圖片")
        }

        val options =
            BitmapFactory.Options().apply {
                inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight)
            }
        val decoded =
            context.contentResolver.openInputStream(uri).use { input ->
                BitmapFactory.decodeStream(input, null, options)
            } ?: throw IOException("無法解碼圖片")
        val oriented = decoded.rotate(exifRotation(uri))
        if (oriented !== decoded) decoded.recycle()
        val scaled = oriented.scaleToMaxDimension()
        if (scaled !== oriented) oriented.recycle()

        return try {
            scaled.toCompressedJpeg()
        } finally {
            scaled.recycle()
        }
    }

    private fun calculateInSampleSize(
        width: Int,
        height: Int,
    ): Int {
        var sampleSize = 1
        while (width / sampleSize > MAX_DIMENSION * 2 || height / sampleSize > MAX_DIMENSION * 2) {
            sampleSize *= 2
        }
        return sampleSize
    }

    private fun exifRotation(uri: Uri): Float =
        context.contentResolver.openInputStream(uri).use { input ->
            when (ExifInterface(requireNotNull(input)).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        }

    private fun Bitmap.rotate(degrees: Float): Bitmap =
        if (degrees == 0f) {
            this
        } else {
            Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees) }, true)
        }

    private fun Bitmap.scaleToMaxDimension(): Bitmap {
        val maxDimension = maxOf(width, height)
        if (maxDimension <= MAX_DIMENSION) return this
        val scale = MAX_DIMENSION.toFloat() / maxDimension
        return Bitmap.createScaledBitmap(this, (width * scale).toInt(), (height * scale).toInt(), true)
    }

    private fun Bitmap.toCompressedJpeg(): ByteArray {
        var quality = INITIAL_JPEG_QUALITY
        var bytes: ByteArray
        do {
            bytes =
                ByteArrayOutputStream().use { output ->
                    compress(Bitmap.CompressFormat.JPEG, quality, output)
                    output.toByteArray()
                }
            quality -= JPEG_QUALITY_STEP
        } while (bytes.size > TARGET_BYTES && quality >= MIN_JPEG_QUALITY)
        return bytes
    }

    private companion object {
        const val MAX_DIMENSION = 1600
        const val TARGET_BYTES = 500 * 1024
        const val INITIAL_JPEG_QUALITY = 80
        const val MIN_JPEG_QUALITY = 55
        const val JPEG_QUALITY_STEP = 6
    }
}
