package systems.alderaan.bookworm.network.model

import kotlinx.serialization.Serializable

@Serializable
data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

@Serializable
data class Price(val amount: Float = 0f, val currencyCode: String = "")

@Serializable
data class SaleInfo(
    val country: String = "",
    val saleability: String = "",
    val isEBook: Boolean = false,
    val listPrice: Price = Price(),
    val salePrice: Price = Price(),
    val buyLink: String = ""
)

@Serializable
data class EBook(val isAvailable: Boolean = false, val acsTokenLink: String = "")

@Serializable
data class AccessInfo(
    val country: String = "",
    val viewability: String = "",
    val embeddable: Boolean = false,
    val publicDomain: Boolean = false,
    val epub: EBook = EBook(),
    val pdf: EBook = EBook(),
    val webReaderLink: String = "",
    val accessViewStatus: String = ""
)

@Serializable
data class VolumeInfo(

    val title: String = "",

    val subtitle: String = "",

    val authors: List<String> = listOf(),

    val publisher: String = "",

    val publishedDate: String = "",

    val description: String = "",

    val industryIdentifiers: List<IndustryIdentifier> = listOf(),

    val categories: List<String> = listOf(),

    val imageLinks: ImageLinks = ImageLinks(),

    val averageRating: Float = 0.0f,

    val ratingsCount: Int = 0,

    val saleInfo: SaleInfo = SaleInfo(),

    val accessInfo: AccessInfo = AccessInfo()
) {
    fun getImageLink(): String? {
        return if (imageLinks.extraLarge.isNotEmpty()) {
            imageLinks.extraLarge
        } else if (imageLinks.large.isNotEmpty()) {
            imageLinks.large
        } else if (imageLinks.medium.isNotEmpty()) {
            imageLinks.medium
        } else if (imageLinks.small.isNotEmpty()) {
            imageLinks.small
        } else if (imageLinks.thumbnail.isNotEmpty()) {
            imageLinks.thumbnail
        } else if (imageLinks.smallThumbnail.isNotEmpty()) {
            imageLinks.smallThumbnail
        } else {
            null
        }
    }
}
