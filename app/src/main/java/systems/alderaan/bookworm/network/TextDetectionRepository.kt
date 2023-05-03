package systems.alderaan.bookworm.network

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import systems.alderaan.bookworm.TAG
import systems.alderaan.bookworm.data.model.Photo
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.BufferedSink
import okio.source
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

interface TextDetectionRepository {
    suspend fun detectText(key: String): List<String>

    suspend fun upload(image: Uri, context: Context, compressImage: Boolean = true): Photo
}

class BookwormTextDetectionRepository(
    private val textDetectionService: TextDetectionService
) : TextDetectionRepository {

    override suspend fun detectText(key: String): List<String> {
        Log.i(TAG, "detectText: key = $key")
        try {
            return textDetectionService.detectText(key)
        } catch (e: HttpException) {
            Log.e(TAG, e.stackTraceToString())
            throw RuntimeException(e)
        }
    }

    override suspend fun upload(image: Uri, context: Context, compressImage: Boolean): Photo {

        context.contentResolver.getType(image)?.let {
            image.path?.let {
                val file: File
                val requestBody = if (compressImage) {
                    file = ImageCompressor(image, context).compress()
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                } else {
                    file = File(it)
                    InputStreamRequestBody(context.contentResolver, image)
                }
                val filePart = MultipartBody.Part.createFormData(
                    "file", file.name, requestBody
                )

                try {
                    val uploadedImage = textDetectionService.upload(filePart)
                    Log.i(TAG, "Image uploaded: ${uploadedImage.name}")
                    file.delete()

                    return uploadedImage
                } catch (e: HttpException) {
                    Log.e(TAG, e.stackTraceToString())
                    throw RuntimeException(e)
                }

            } ?: throw IllegalStateException("image.path cannot be null")
        } ?: throw IllegalStateException("Failed to get type for $image.")
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

class ImageCompressor(
    private val image: Uri,
    private val context: Context
) {

    fun compress(): File {
        val compressedImageByteArray = reduceImageSize()
        return bitMapToFile(compressedImageByteArray, image.lastPathSegment.toString())
    }
    private fun reduceImageSize(): ByteArray {
        try {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, image)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, image)
                ImageDecoder.decodeBitmap(source)
            }
            val scaledWidth = bitmap.width / 4
            val scaledHeight = bitmap.height / 3

            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap, scaledWidth, scaledHeight, true
            )
            val byteArrayOutputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            Log.e(TAG, e.stackTraceToString())
            throw RuntimeException(e)
        }
    }

    private fun bitMapToFile(byteArray: ByteArray, filename: String): File {
        val file = File(context.cacheDir, filename)
        file.createNewFile()

        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(byteArray)
            fileOutputStream.flush()
            fileOutputStream.close()

            return file
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.stackTraceToString())
            throw RuntimeException(e)
        } catch (e: IOException) {
            Log.e(TAG, e.stackTraceToString())
            throw RuntimeException(e)
        }
    }

}