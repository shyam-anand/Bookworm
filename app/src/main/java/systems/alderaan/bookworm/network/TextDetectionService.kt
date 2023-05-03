package systems.alderaan.bookworm.network

import systems.alderaan.bookworm.data.model.Photo
import okhttp3.MultipartBody
import retrofit2.http.*

interface TextDetectionService {

    @GET("/photos/{key}/text")
    suspend fun detectText(@Path(value = "key", encoded = true) key: String): List<String>

    @Multipart
    @POST("/photos")
    suspend fun upload(@Part image: MultipartBody.Part): Photo

}
