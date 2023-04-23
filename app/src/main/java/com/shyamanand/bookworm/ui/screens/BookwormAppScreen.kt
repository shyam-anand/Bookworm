package com.shyamanand.bookworm.ui.screens

import androidx.annotation.DrawableRes
import com.shyamanand.bookworm.R

enum class BookwormAppScreen(
    @DrawableRes val filledIcon: Int? = null,
    @DrawableRes val outlinedIcon: Int? = null
) {
    BookDetails(),
    Bookshelf(R.drawable.bookshelf_filled, R.drawable.bookshelf_outlined),
    Search(R.drawable.search_filled, R.drawable.search_outlined),
    Camera(R.drawable.camera_filled, R.drawable.camera_outlined),
    PermissionsRequest()
}
