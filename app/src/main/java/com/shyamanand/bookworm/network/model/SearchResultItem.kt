package com.shyamanand.bookworm.network.model

import com.shyamanand.bookworm.data.model.Book

@kotlinx.serialization.Serializable
data class SearchResultItem(
    val id: String,
    val selfLink: String,
    val volumeInfo: VolumeInfo
) {
    fun toBook(): Book {
        return Book(
            id = id,
            selfLink = selfLink,
            title = volumeInfo.title,
            subtitle = volumeInfo.subtitle,
            authors = volumeInfo.authors.joinToString(", "),
            description = volumeInfo.description,
            categories = volumeInfo.categories.joinToString(", "),
            imageUrl = volumeInfo.getImageLink(),
            ratingsCount = volumeInfo.ratingsCount,
            averageRating = volumeInfo.averageRating
        )
    }
}
