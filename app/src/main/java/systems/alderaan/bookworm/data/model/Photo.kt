package systems.alderaan.bookworm.data.model

// Represents a Photo stored in S3
@kotlinx.serialization.Serializable
data class Photo(
    // The key of the object
    val name: String = "",

    // The hash of the object
    val eTag: String = ""
)
