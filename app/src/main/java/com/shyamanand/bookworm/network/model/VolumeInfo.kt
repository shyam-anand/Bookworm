package com.shyamanand.bookworm.network.model

import com.shyamanand.bookworm.ui.state.BookDetailsScreenState
import kotlinx.serialization.Serializable

@Serializable
data class VolumeInfo(

    val title: String = "",

    val subtitle: String = "",

    val authors: List<String> = listOf(),

    val description: String = "",

    val categories: List<String> = listOf(),

    val imageLinks: ImageLinks = ImageLinks(),

    val averageRating: Float = 0.0f,

    val ratingsCount: Int = 0
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
