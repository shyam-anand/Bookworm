package com.shyamanand.bookworm.data.model

import androidx.compose.ui.text.Placeholder
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(

    @PrimaryKey(autoGenerate = false)
    val id: String = "",

    val title: String = "",
    val subtitle: String? = null,
    val authors: String = "",
    val description: String? = null,
    val categories: String? = null,
    val selfLink: String? = null,
    val averageRating: Float = 0f,
    val ratingsCount: Int = 0,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null
)
