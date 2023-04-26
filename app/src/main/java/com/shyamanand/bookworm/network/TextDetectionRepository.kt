package com.shyamanand.bookworm.network

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.shyamanand.bookworm.TAG
import com.shyamanand.bookworm.data.model.Photo
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File

interface TextDetectionRepository {
    suspend fun detectText(key: String): List<String>

    suspend fun upload(image: Uri, context: Context): Photo
}

class BookwormTextDetectionRepository(
    private val textDetectionService: TextDetectionService
) : TextDetectionRepository {

    override suspend fun detectText(key: String): List<String> {
        Log.i(TAG, "detectText: key = $key")
        return textDetectionService.detectText(key)
    }

    override suspend fun upload(image: Uri, context: Context): Photo {
        context.contentResolver.getType(image)?.let {
            val file = File(image.path!!)
            val requestBody = InputStreamRequestBody(context.contentResolver, image)
            val filePart = MultipartBody.Part.createFormData(
                "file", file.name, requestBody
            )

            return textDetectionService.upload(filePart)
        }

        throw IllegalStateException("Failed to get type for $image.")
    }
}

class InputStreamRequestBody(
    private val contentResolver: ContentResolver,
    private val imageUri: Uri
) : RequestBody() {
    override fun contentType(): MediaType? =
        contentResolver.getType(imageUri)?.toMediaTypeOrNull()


    @SuppressLint("Recycle")
    override fun writeTo(sink: BufferedSink) {
        contentResolver.openInputStream(imageUri)?.source()?.use(sink::writeAll)
    }

    override fun contentLength(): Long {
        contentResolver.query(imageUri, null, null, null, null)?.use { cursor ->
            val sizeColumnIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            return cursor.getLong(sizeColumnIndex)
        } ?: super.contentLength()
        throw IllegalStateException("contentLength() failed")
    }

}